package com.rinko1231.peyroscythe.spell;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.ice_block.IceBlockProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

@AutoSpellConfig
public class GlacierSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation("peyroscythe", "glacier");
    private final DefaultConfig defaultConfig;

    public GlacierSpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.RARE)
                .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
                .setMaxLevel(6)
                .setCooldownSeconds(15.0)
                .build();
        this.manaCostPerLevel = 8;
        this.baseSpellPower = 14;
        this.spellPowerPerLevel = 2;
        this.castTime = 24;
        this.baseManaCost = 36;
    }


    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return this.spellId;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.ICE_BLOCK_CAST.get());
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData data) {
        // 与原“霜降”一致：预锁定（若有）
        Utils.preCastTargetHelper(level, caster, data, this, 48, 0.35F, false);
        return true;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        Vec3 spawn = null;
        LivingEntity target = null;
        ICastData livingEntity = playerMagicData.getAdditionalCastData();
        if (livingEntity instanceof TargetEntityCastData castTargetingData) {
            target = castTargetingData.getTarget((ServerLevel)level);
            if (target != null) {
                spawn = target.position();
            }
        }

        if (spawn == null) {
            HitResult raycast = Utils.raycastForEntity(level, entity, 32.0F, true, 0.25F);
            if (raycast.getType() == HitResult.Type.ENTITY) {
                spawn = ((EntityHitResult)raycast).getEntity().position();
                Entity var10 = ((EntityHitResult)raycast).getEntity();
                if (var10 instanceof LivingEntity) {
                    target = (LivingEntity)var10;
                }
            } else {
                spawn = raycast.getLocation().subtract(entity.getForward().normalize());
            }
        }

        GlacierIceBlockProjectile iceBlock = new GlacierIceBlockProjectile(level, entity, target);

        float growthPerLevel = 0.35F;
        float scale = Mth.clamp(1.0F + growthPerLevel * (spellLevel - 1), 1.0F, 4.0F);
        iceBlock.setScaleFactor(scale); // ← 同时驱动碰撞箱与渲染

        iceBlock.moveTo(this.raiseWithCollision(spawn, 20, level));
        iceBlock.setAirTime(target == null ? 25 : 35);
        iceBlock.setDamage(this.getDamage(spellLevel, entity));
        level.addFreshEntity(iceBlock);
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }


    private Vec3 raiseWithCollision(Vec3 start, int blocks, Level level) {
        for (int i = 0; i < blocks; ++i) {
            Vec3 next = start.add(0, 1, 0);
            if (!level.getBlockState(BlockPos.containing(next)).isAir()) break;
            start = next;
        }
        return start;
    }

    private float getDamage(int spellLevel, LivingEntity entity) {
        return this.getSpellPower(spellLevel, entity);
    }

    @Override
    public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        // 与霜降保持一致：附加冻伤时间
        return super.getDamageSource(projectile, attacker).setFreezeTicks(100);
    }
}
