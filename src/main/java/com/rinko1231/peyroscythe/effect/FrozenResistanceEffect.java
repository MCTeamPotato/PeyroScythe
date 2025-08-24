package com.rinko1231.peyroscythe.effect;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FrozenResistanceEffect extends MagicMobEffect {

        public FrozenResistanceEffect() {
            super(MobEffectCategory.NEUTRAL, 0x555555); // 灰色
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {

        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            return false; // 每 tick 检查一次
        }
    }
