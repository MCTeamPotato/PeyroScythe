package com.rinko1231.peyroscythe.effect.death_smoke;


import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class DeathSmokeErosionEffect extends MagicMobEffect {

    public DeathSmokeErosionEffect() {
        super(MobEffectCategory.HARMFUL, 0x555555); // 灰色
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
       entity.hurt(entity.damageSources().wither(), 1.0F + 0.2F* amplifier);
       return true;

    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        //return true;
        return duration%20==0; // 每 tick 检查一次
    }

    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
