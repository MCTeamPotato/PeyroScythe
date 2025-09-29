package com.rinko1231.peyroscythe.spell.ice;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.spellentity.ice.IcyCometEntity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.TargetAreaCastData;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class IcyCometRainSpell extends AbstractSpell {
    private final ResourceLocation spellId = PeyroScythe.id("icy_comet_rain");
    private final DefaultConfig defaultConfig;

    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage",
                new Object[]{Utils.stringTruncation((double)this.getDamage(spellLevel, caster), 2)}),
                Component.translatable("ui.irons_spellbooks.radius",
                new Object[]{Utils.stringTruncation((double)this.getRadius(spellLevel, caster), 1)}));
    }

    public IcyCometRainSpell() {
        this.defaultConfig = (new DefaultConfig()).setMinRarity(SpellRarity.RARE)
                .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
                .setMaxLevel(6)
                .setCooldownSeconds((double)30.0F).build();
        this.manaCostPerLevel = 3;
        this.baseSpellPower = 8;
        this.spellPowerPerLevel = 1;
        this.castTime = 160;
        this.baseManaCost = 8;
    }

    @Override
    public boolean allowLooting() {
        return PeyroScytheConfig.icyCometRainAllowLooting.get();
    }

    public CastType getCastType() {
        return CastType.CONTINUOUS;
    }

    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    public ResourceLocation getSpellResource() {
        return this.spellId;
    }

    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of((SoundEvent) SoundRegistry.ICE_CAST.get());
    }

    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!(playerMagicData.getAdditionalCastData() instanceof TargetAreaCastData)) {
            Vec3 targetArea = Utils.moveToRelativeGroundLevel(world, Utils.raycastForEntity(world, entity, 40.0F, true).getLocation(), 12);
            playerMagicData.setAdditionalCastData(new TargetAreaCastData(targetArea, TargetedAreaEntity.createTargetAreaEntity(world, targetArea, this.getRadius(spellLevel,entity), 6291596)));
        }

        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    private int iceCometCountPerTick(int spellLevel, LivingEntity entity)
    {
        return (int) Math.min(2 + spellLevel/2.0f + this.getSpellPower(spellLevel, entity)/20, PeyroScytheConfig.icyCometRainMaxCountPerTick.get());
    }

    public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
        if (playerMagicData != null && (playerMagicData.getCastDurationRemaining() + 1) % 4 == 0) {
            ICastData i = playerMagicData.getAdditionalCastData();
            if (i instanceof TargetAreaCastData) {
                TargetAreaCastData targetAreaCastData = (TargetAreaCastData)i;

                for(int x = 0; x < iceCometCountPerTick(spellLevel,entity); ++x) {
                    Vec3 center = targetAreaCastData.getCenter();
                    float radius = this.getRadius(spellLevel, entity);
                    Vec3 spawn = center.add((new Vec3((double)0.0F, (double)0.0F, (double)(entity.getRandom().nextFloat() * radius))).yRot((float)entity.getRandom().nextInt(360)));
                    spawn = this.raiseWithCollision(spawn, 12, level);
                    this.shootComet(level, spellLevel, entity, spawn);
                    MagicManager.spawnParticles(level, ParticleHelper.ICY_FOG, spawn.x, spawn.y, spawn.z, 1, (double)1.0F, (double)1.0F, (double)1.0F, (double)1.0F, false);
                    MagicManager.spawnParticles(level, ParticleHelper.ICY_FOG, spawn.x, spawn.y, spawn.z, 1, (double)1.0F, (double)1.0F, (double)1.0F, (double)1.0F, true);
                }
            }
        }

    }

    private Vec3 raiseWithCollision(Vec3 start, int blocks, Level level) {
        for(int i = 0; i < blocks; ++i) {
            Vec3 raised = start.add((double)0.0F, (double)1.0F, (double)0.0F);
            if (!level.getBlockState(BlockPos.containing(raised)).isAir()) {
                break;
            }

            start = raised;
        }

        return start;
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return this.getSpellPower(spellLevel, caster) * PeyroScytheConfig.icyCometRainDamageRatioOfSpellPower.get().floatValue();
    }

    private float getRadius(int SpellLevel, LivingEntity caster) {
        return Math.min((6.0F +0.125F * this.getSpellPower(SpellLevel, caster)), PeyroScytheConfig.icyCometRainMaxRadius.get().floatValue()) ;
    }

    public void shootComet(Level world, int spellLevel, LivingEntity entity, Vec3 spawn) {
        IcyCometEntity icyCometEntity = new IcyCometEntity(world, entity);
        icyCometEntity.setPos(spawn.add((double)-1.0F, (double)0.0F, (double)0.0F));
        icyCometEntity.shoot(new Vec3((double)0.15F, (double)-0.85F, (double)0.0F), 0.075F);
        icyCometEntity.setDamage(this.getDamage(spellLevel, entity));
        icyCometEntity.setExplosionRadius(2.0F);
        world.addFreshEntity(icyCometEntity);
        world.playSound((Player)null, spawn.x, spawn.y, spawn.z, SoundRegistry.ICE_CAST.get(), SoundSource.PLAYERS, 3.0F, 0.7F + Utils.random.nextFloat() * 0.3F);
    }

    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.ANIMATION_CONTINUOUS_OVERHEAD;
    }
}
