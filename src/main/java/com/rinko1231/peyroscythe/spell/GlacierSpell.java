package com.rinko1231.peyroscythe.spell;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.spellentity.GlacierIceBlockProjectile;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
                .setMinRarity(SpellRarity.EPIC)
                .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
                .setMaxLevel(6)
                .setCooldownSeconds(20.0)
                .build();
        this.manaCostPerLevel = 20;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 3;
        this.castTime = 30;
        this.baseManaCost = 40;
    }

    @Override
    public boolean allowLooting() {
        return PeyroScytheConfig.glacierAllowLooting.get();
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
        // 与原霜降一致
        Utils.preCastTargetHelper(level, caster, data, this, 48, 0.35F, false);
        return true;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        Vec3 spawn = null;
        LivingEntity target = null;
        ICastData livingEntity = playerMagicData.getAdditionalCastData();
        if (livingEntity instanceof TargetEntityCastData castTargetingData) {
            target = castTargetingData.getTarget((ServerLevel) level);
            if (target != null) {
                spawn = target.position();
            }
        }

        if (spawn == null) {
            HitResult raycast = Utils.raycastForEntity(level, entity, 48.0F, true, 0.25F);
            if (raycast.getType() == HitResult.Type.ENTITY) {
                spawn = ((EntityHitResult) raycast).getEntity().position();
                Entity var10 = ((EntityHitResult) raycast).getEntity();
                if (var10 instanceof LivingEntity) {
                    target = (LivingEntity) var10;
                }
            } else {
                spawn = raycast.getLocation().subtract(entity.getForward().normalize());
            }
        }

        GlacierIceBlockProjectile glacierIceBlockProjectile = new GlacierIceBlockProjectile(level, entity, target);

        float growthPerLevel = PeyroScytheConfig.glacierScaleGrowthPerLevel.get().floatValue();
        float scale = Mth.clamp(1.0F + growthPerLevel * (spellLevel - 1), 1.0F, PeyroScytheConfig.glacierScaleMax.get().floatValue());
        glacierIceBlockProjectile.setScaleFactor(scale);

        glacierIceBlockProjectile.moveTo(this.raiseWithCollision(spawn, 20 + (int) scale, level));
        glacierIceBlockProjectile.setAirTime(target == null ? 25 : 35);
        glacierIceBlockProjectile.setDamage(this.getDamage(spellLevel, entity));
        level.addFreshEntity(glacierIceBlockProjectile);
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }


    private Vec3 raiseWithCollision(Vec3 start, int maxUp, Level level) {
        Vec3 cur = start;
        int climbed = 0;

        // 从水里抬出去
        while (climbed < maxUp) {
            BlockPos here = BlockPos.containing(cur);
            BlockState stateHere = level.getBlockState(here);
            if (stateHere.getFluidState().is(FluidTags.WATER)) {
                cur = cur.add(0, 1, 0);
                climbed++;
            } else {
                break;
            }
        }

        //向上找空气
        while (climbed < maxUp) {
            Vec3 next = cur.add(0, 1, 0);
            BlockState nextState = level.getBlockState(BlockPos.containing(next));
            if (!nextState.isAir()) break;
            cur = next;
            climbed++;
        }

        return cur;
    }

    private float getDamage(int spellLevel, LivingEntity entity) {
        return this.getSpellPower(spellLevel, entity);
    }

    @Override
    public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return super.getDamageSource(projectile, attacker).setFreezeTicks(120);
    }
}
