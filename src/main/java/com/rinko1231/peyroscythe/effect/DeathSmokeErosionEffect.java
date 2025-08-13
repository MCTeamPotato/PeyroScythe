package com.rinko1231.peyroscythe.effect;


import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class DeathSmokeErosionEffect extends MagicMobEffect {

    public DeathSmokeErosionEffect() {
        super(MobEffectCategory.NEUTRAL, 0x555555); // 灰色
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.hurt(entity.damageSources().wither(), 1.0F + 0.3F* amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // 每 tick 检查一次
    }
}
