package com.rinko1231.peyroscythe.effect;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ViatorMundiEffect extends MagicMobEffect {

        public ViatorMundiEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xFFD700);
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {

        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            return false;
        }
    }
