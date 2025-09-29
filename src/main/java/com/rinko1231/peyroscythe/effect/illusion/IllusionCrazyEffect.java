package com.rinko1231.peyroscythe.effect.illusion;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.TagsRegistry;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.NeutralWizard;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class IllusionCrazyEffect extends MagicMobEffect {

    public IllusionCrazyEffect() {

            super(MobEffectCategory.HARMFUL, 0);

    }

        @Override
        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {

            return duration % 5 == 0;
        }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof PathfinderMob mob)) {
            return false;
        }
        if (mob.level().isClientSide) return true;

        if (entity.getType().is(TagsRegistry.ILLUSION_IMMUNE)) {
            return false;
        }

        CompoundTag tag = mob.getPersistentData();
        if (tag == null || !tag.hasUUID("peyroscythe:crazy_caster")) return true;

        Entity casterE = ((ServerLevel) mob.level()).getEntity(tag.getUUID("peyroscythe:crazy_caster"));
        if (!(casterE instanceof LivingEntity caster)) return true;

        // 搜索半径 9 格
        double range = PeyroScytheConfig.illusionCrazySearchRadius.get();
        AABB box = mob.getBoundingBox().inflate(range);

        // 如果已有目标且目标仍在范围内，且不是施法者或友方，则直接返回
        LivingEntity currentTarget = mob.getTarget();
        if (currentTarget != null
                && currentTarget.isAlive()
                && !DamageSources.isFriendlyFireBetween(caster, currentTarget)
                && box.contains(currentTarget.position())) {
            return true; // 不刷新目标
        }

        List<LivingEntity> candidates = mob.level().getEntitiesOfClass(LivingEntity.class, box,
                target -> target.isAlive()
                        && target != mob
                        && target != caster
                        && !DamageSources.isFriendlyFireBetween(caster, target));

        if (candidates.isEmpty()) return true;

        // 找最近的目标
        LivingEntity nearest = null;
        double minDistSq = Double.MAX_VALUE;
        for (LivingEntity target : candidates) {
            double distSq = mob.distanceToSqr(target);
            if (distSq < minDistSq) {
                minDistSq = distSq;
                nearest = target;
            }
        }

        if (nearest != null) {
            mob.setTarget(nearest);
            mob.setLastHurtByMob(nearest);
            if (mob instanceof NeutralWizard wizard) {
                wizard.increaseAngerLevel(4, true);
                wizard.setPersistentAngerTarget(nearest.getUUID());
                wizard.updatePersistentAnger((ServerLevel) wizard.level(), true);
                wizard.setLastHurtMob(nearest);
            }
        }
        return true;
    }

    @Override
        public void onEffectRemoved(LivingEntity entity, int amplifier) {
            super.onEffectRemoved(entity, amplifier);
            CompoundTag tag = entity.getPersistentData();

            if (tag != null) {
                tag.remove("peyroscythe:crazy_caster");
            }
        }
    }
