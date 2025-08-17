package com.rinko1231.peyroscythe.spell;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import com.rinko1231.peyroscythe.init.TagsRegistry;
import com.rinko1231.peyroscythe.spellentity.FrostFogEntity;
import com.rinko1231.peyroscythe.spellentity.IceTombEntity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class TheFrozenWorldSpell extends AbstractSpell {
    private static final ResourceLocation ID = new ResourceLocation("peyroscythe", "the_frozen_world");

    private final DefaultConfig defaultConfig;

    public TheFrozenWorldSpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.LEGENDARY)
                .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
                .setMaxLevel(3)
                .setCooldownSeconds(100.0)
                .build();
        this.manaCostPerLevel = 80;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 0;
        this.castTime = 20;
        this.baseManaCost = 100;
    }

    @Override
    public boolean allowLooting() {
        return PeyroScytheConfig.frozenWorldAllowLooting.get();
    }


    @Override
    public CastType getCastType() {
        return CastType.LONG; // 有读条
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return ID;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.ICE_BLOCK_CAST.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.ICE_SPIKE_EMERGE.get());
    }
    private float getRadius(int spellLevel, LivingEntity entity) {
        return (float)(2 * spellLevel + 2) + 0.1F * this.getSpellPower(spellLevel, entity);
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.radius",
                        new Object[]{Utils.stringTruncation((double)this.getRadius(spellLevel, caster), 1)}),
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(200, 1)) // 冻结效果10秒
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData data) {
        if (level.isClientSide) {
            super.onCast(level, spellLevel, caster, castSource, data);
            return;
        }

        final double r = getRadius(spellLevel, caster);
        final double rSqr = r * r;

        final double innerR = PeyroScytheConfig.frozenWorldInnerCircleRatio.get() *r ;
        final double innerRSqr = innerR * innerR;
        final Vec3 center = caster.position();

        // 1.生物冻结
        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                caster.getBoundingBox().inflate(innerR, innerR, innerR),
                e -> e != caster
                        && e.distanceToSqr(center) <= innerRSqr
                        && !DamageSources.isFriendlyFireBetween(caster, e)
        );
        SpellDamageSource damageSource = ((AbstractSpell) NewSpellRegistry.FROZEN_WORLD_SPELL.get()).getDamageSource(caster);

        for (LivingEntity t : targets) {
            if (t.getType().is(TagsRegistry.MUST_FREEZE_BOSS)) {
                // ---- 强制冰棺逻辑 ----
                IceTombEntity iceTombEntity = new IceTombEntity(t.level(), null);
                iceTombEntity.moveTo(t.position());
                iceTombEntity.setDeltaMovement(t.getDeltaMovement());
                iceTombEntity.setEvil();
                iceTombEntity.setLifetime(20*4); // 可以做个配置
                t.level().addFreshEntity(iceTombEntity);

                t.startRiding(iceTombEntity, true);

                float itsLife = t.getMaxHealth();
                t.hurt(damageSource, itsLife * spellLevel * PeyroScytheConfig.frozenWorldMaxHealthDamageRatioPerLevel.get().floatValue());
                t.setTicksFrozen(Math.min(t.getTicksRequiredToFreeze(), t.getTicksFrozen() + 140)); // 累加式更柔和
                t.addEffect(new MobEffectInstance(
                        MobEffectRegistry.FROZEN.get(),
                        PeyroScytheConfig.frozenWorldFrozenEffectDurationToEntityInInnerCircle.get(),
                        4, false, true, true
                ));
            } else {
                // ---- 普通冰冻逻辑 ----
                float itsLife = t.getMaxHealth();
                t.hurt(damageSource, itsLife * PeyroScytheConfig.frozenWorldMaxHealthDamageRatioPerLevel.get().floatValue());
                t.setTicksFrozen(Math.min(t.getTicksRequiredToFreeze(), t.getTicksFrozen() + 140)); // 累加式更柔和
                t.addEffect(new MobEffectInstance(
                        MobEffectRegistry.FROZEN.get(),
                        PeyroScytheConfig.frozenWorldFrozenEffectDurationToEntityInInnerCircle.get(),
                        4, false, true, true
                ));
            }
        }

        FrostFogEntity fog = new FrostFogEntity(caster.level());
        fog.setOwner(caster);
        fog.setDuration(PeyroScytheConfig.frozenWorldIceFogDuration.get());
        fog.setRadius((float)r);
        fog.setCircular();
        fog.moveTo(center);
        caster.level().addFreshEntity(fog);

        // 2.冻结周围环境
        if(PeyroScytheConfig.frozenWorldFreezesWorld.get()) {
            freezeWorld(level, BlockPos.containing(center), (int) Math.ceil(r), level.getRandom());
        }
        // 3.简单粒子
        int particleCount = 150; // 每种粒子数量，可调整
        MagicManager.spawnParticles(level, ParticleHelper.SNOW_DUST, center.x, center.y, center.z,
                particleCount, r, r / 2, r, 0.05, false);
        MagicManager.spawnParticles(level, ParticleHelper.SNOWFLAKE, center.x, center.y, center.z,
                particleCount, r, r / 2, r, 0.05, false);

        super.onCast(level, spellLevel, caster, castSource, data);
    }
    private static boolean allowY(double dy, double radius) {
        if (dy < -2) return false;

        return (long) dy  <= ((long) radius) / 2L;

    }

    private void freezeWorld(Level level, BlockPos center, int radius, RandomSource rand) {
        // 1.冻水冻岩浆灭火
        BlockPos min = center.offset(-radius, -radius, -radius);
        BlockPos max = center.offset(radius, radius, radius);
        int rSqr = radius * radius;
        double innerRadius = PeyroScytheConfig.frozenWorldInnerCircleRatio.get() * radius;
        double innerRadiusSqr = innerRadius * innerRadius;
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (pos.distSqr(center) > innerRadiusSqr) continue;

            int dy = pos.getY() - center.getY();
            if (!allowY(dy, innerRadius)) continue;  // ★ 只保留 dy ≥ 0 且 dy^2 ≤ (r^2)/2 的层
            BlockState state = level.getBlockState(pos);
            if (!state.isAir() && state.getFluidState().isSource() && state.getFluidState().is(FluidTags.WATER)) {
                level.setBlock(pos, Blocks.FROSTED_ICE.defaultBlockState(), 3);
            }
            if (!state.isAir() && state.getFluidState().isSource() && state.getFluidState().is(FluidTags.LAVA)) {
                level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
            }
            if (!state.isAir() && state.is(Blocks.FIRE)) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }

        // 2.落雪
        final int MAX_LAYERS = 6; // 1.20.1 是 8
        final int yBottom = center.getY()-2; // dy >= 0
        final int yTop = center.getY() + (int)Math.floor(Math.sqrt((radius * radius) / 2.0));   // 竖向扫描下界（可调）
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int d2 = dx*dx + dz*dz;
                if (d2 > rSqr) continue;

                int x = center.getX() + dx;
                int z = center.getZ() + dz;

                // 从高到低找“上面空气 & 下面可承托”的落雪位点（不再要求能见天）
                boolean placed = false;
                for (int y = yTop; y >= yBottom; y--) {
                    cursor.set(x, y, z);

                    if (!level.isEmptyBlock(cursor)) continue; // 该格必须是空气
                    BlockState below = level.getBlockState(cursor.below());
                    if (!below.isAir() && below.isFaceSturdy(level, cursor.below(), Direction.UP)) {
                        // 距离衰减：越远期望层数越少/概率越低
                        double dist = Math.sqrt(d2);
                        double falloff = 1.0 - (dist / radius);         // [0,1]
                        if (falloff <= 0.0) break;

                        // 期望层数（线性）+ 随机抖动
                        double expected = falloff * MAX_LAYERS;
                        int base = (int)Math.floor(expected);
                        double frac = expected - base;
                        if (rand.nextDouble() < frac) base++;

                        int layers = Mth.clamp(base, 0, MAX_LAYERS);
                        if (layers <= 0) break; // 很远处可能不堆雪

                        BlockState snow = Blocks.SNOW.defaultBlockState()
                                .setValue(SnowLayerBlock.LAYERS, layers);

                        if (snow.canSurvive(level, cursor)) {
                            level.setBlock(cursor, snow, 3);
                            placed = true;
                        }
                        break; // 找到一个位置就停
                    }
                }
                // placed==false 时，本列找不到合适位置，跳过即可
            }
        }
    }


/*
    private void freezeWorld(Level level, BlockPos center, int radius, RandomSource rand) {
        // 扫描一个包围立方体，内部用半径平方筛选成球
        BlockPos min = center.offset(-radius, -radius, -radius);
        BlockPos max = center.offset(radius, radius, radius);
        int rSqr = radius * radius;

        // 预计算地表高度需要用到的XZ范围（落雪只看水平半径）
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (pos.distSqr(center) > rSqr) continue;

            // 冻水：只改水源方块
            BlockState state = level.getBlockState(pos);
            if (!state.isAir()) {
                if (state.getFluidState().isSource() && state.getFluidState().is(FluidTags.WATER)) {
                    level.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                }
            }
        }

        // 落雪：对每个 (x,z) 在半径内取最高实心位点上方落雪层
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx*dx + dz*dz > rSqr) continue;

                int x = center.getX() + dx;
                int z = center.getZ() + dz;

                // 顶部位置（避免把雪塞到洞穴里）
                BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));

                // 只在近地表落雪：避免半空或室内
                if (top.closerThan(center, radius + 1)) {
                    // 要求 top 是空气，且下方可承托
                    cursor.set(top);
                    BlockState below = level.getBlockState(cursor.below());
                    if (level.isEmptyBlock(cursor) && below.isFaceSturdy(level, cursor.below(), Direction.UP)) {
                        int layers = 1 + rand.nextInt(4); // 1~8 层
                        BlockState snow = Blocks.SNOW.defaultBlockState()
                                .setValue(SnowLayerBlock.LAYERS, Math.min(layers, SnowLayerBlock.HEIGHT_IMPASSABLE));
                        if (snow.canSurvive(level, cursor)) {
                            level.setBlock(cursor, snow, 3);
                        }
                    }
                }
            }
        }
    }*/
}
