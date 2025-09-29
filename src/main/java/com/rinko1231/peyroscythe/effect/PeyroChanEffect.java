package com.rinko1231.peyroscythe.effect;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class PeyroChanEffect extends MagicMobEffect {

        public PeyroChanEffect() {
            super(MobEffectCategory.NEUTRAL, 13695487);
        }

        @Override
        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
return true;
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
            return false;
        }
    }
