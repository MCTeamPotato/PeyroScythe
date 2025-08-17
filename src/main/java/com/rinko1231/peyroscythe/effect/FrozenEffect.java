package com.rinko1231.peyroscythe.effect;

import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.spellentity.IceTombEntity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import static java.lang.Math.max;

public class FrozenEffect extends MagicMobEffect {

    public FrozenEffect() {
        super(MobEffectCategory.HARMFUL, 13695487);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // 每 tick 都执行
    }


    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {

        if (pLivingEntity instanceof ServerPlayer pp) {
            if (pp.gameMode.isCreative()) return;
        }

        if (pLivingEntity.getVehicle() instanceof IceTombEntity) {
            pLivingEntity.removeEffect(this);
            return;
        }
        if (pLivingEntity.isFullyFrozen() && !pLivingEntity.hasEffect(MobEffectRegistry.FROZEN_RESISTANCE.get())) {
            IceTombEntity iceTombEntity = new IceTombEntity(pLivingEntity.level(), null);
            iceTombEntity.moveTo(pLivingEntity.position());
            iceTombEntity.setDeltaMovement(pLivingEntity.getDeltaMovement());
            iceTombEntity.setEvil();
            if (pAmplifier >= 4)
                iceTombEntity.setLifetime(20 * 6);
            else
                iceTombEntity.setLifetime(20 * 5);
            pLivingEntity.level().addFreshEntity(iceTombEntity);
            pLivingEntity.startRiding(iceTombEntity, true);
            pLivingEntity.playSound(SoundRegistry.ICE_SPIKE_EMERGE.get(), 2, Utils.random.nextInt(9, 11) * .1f);
            pLivingEntity.removeEffect(this);
            pLivingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.FROZEN_RESISTANCE.get(), max(0, 100 - 20 * pAmplifier), 0, false, false, false));
            return;
        }
        return;
    }
}