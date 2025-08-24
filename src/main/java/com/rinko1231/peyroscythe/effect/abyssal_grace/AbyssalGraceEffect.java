package com.rinko1231.peyroscythe.effect.abyssal_grace;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class AbyssalGraceEffect extends MagicMobEffect {
    public static final String TAG_HAS_GRACE = "peyroscythe:abyss_undead";

    public AbyssalGraceEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }


    // 非持续伤害效果，无需 tick
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) { return false; }
}