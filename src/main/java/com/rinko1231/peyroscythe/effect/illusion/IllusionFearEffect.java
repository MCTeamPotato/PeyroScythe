package com.rinko1231.peyroscythe.effect.illusion;

import com.rinko1231.peyroscythe.init.TagsRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class IllusionFearEffect extends MagicMobEffect {

    public IllusionFearEffect() {

            super(MobEffectCategory.HARMFUL, 0);

    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // 每 40 tick (~2秒) 执行一次
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof PathfinderMob mob)) return false;
        if (mob.level().isClientSide) return true;
        if(entity.getType().is(TagsRegistry.ILLUSION_IMMUNE)) {

            return false;
        }

        CompoundTag tag = mob.getPersistentData();
        if (!tag.hasUUID("peyroscythe:fear_caster")) return true;

        Entity casterE = ((ServerLevel) mob.level()).getEntity(tag.getUUID("peyroscythe:fear_caster"));
        if (!(casterE instanceof LivingEntity caster)) return true;

        double avoid = 12.0 + 4.0 * amplifier;
        if (mob.distanceTo(caster) >= avoid) return true;

        //生成随机逃跑目标
        Vec3 away = DefaultRandomPos.getPosAway(mob, 16, 7, caster.position());
        if (away == null) return true;

        // 2. 强制重新导航
        var nav = mob.getNavigation();
        mob.setTarget(null); // 清空攻击目标
        mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        boolean shouldMove = nav.isDone() || nav.getTargetPos() == null
                || caster.distanceToSqr(nav.getTargetPos().getCenter()) < avoid * avoid;

        if (shouldMove)
            nav.moveTo(away.x, away.y, away.z, 1.5 + 0.4 * amplifier); // 稍微加速逃跑
return true;
    }



    @Override
    public void onEffectRemoved(LivingEntity entity, int amplifier) {
        super.onEffectRemoved(entity, amplifier);
        CompoundTag tag = entity.getPersistentData();
        tag.remove("peyroscythe:fear_caster");
    }
}
