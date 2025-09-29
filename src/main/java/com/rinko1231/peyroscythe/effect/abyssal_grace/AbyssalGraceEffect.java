package com.rinko1231.peyroscythe.effect.abyssal_grace;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

import static com.rinko1231.peyroscythe.effect.abyssal_grace.AbyssalGraceEvent.TAG_PENDING_REV;

public class AbyssalGraceEffect extends MagicMobEffect {
    public static final String TAG_HAS_GRACE = "peyroscythe:abyss_undead";

    public AbyssalGraceEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
        // 获得效果：写入 persistentData
        //if(pLivingEntity instanceof ServerPlayer sp) sp.displayClientMessage(Component.literal("undead"),false);
        pLivingEntity.getPersistentData().putBoolean(TAG_HAS_GRACE, true);
        super.onEffectAdded(pLivingEntity, pAmplifier);

    }

    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        pLivingEntity.getPersistentData().remove(TAG_HAS_GRACE);
        pLivingEntity.getPersistentData().remove(TAG_PENDING_REV);
    }
    // 非持续伤害效果，无需 tick
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) { return false; }
}