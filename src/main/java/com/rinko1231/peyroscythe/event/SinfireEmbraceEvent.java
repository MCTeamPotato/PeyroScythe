package com.rinko1231.peyroscythe.event;

import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SinfireEmbraceEvent {

    // 注册攻击监听
    @SubscribeEvent
    public void onAttack(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        if (!attacker.hasEffect(MobEffectRegistry.SINFIRE_EMBRACE.get())) return;

        // 攻击目标时点燃
        event.getEntity().setSecondsOnFire(4); // 可配置
    }

    // 注册击杀监听
    @SubscribeEvent
    public void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity killer)) return;
        if (!killer.hasEffect(MobEffectRegistry.SINFIRE_EMBRACE.get())) return;

        MobEffectInstance current = killer.getEffect(MobEffectRegistry.SINFIRE_EMBRACE.get());
        if (current != null) {
            int newDuration = current.getDuration() + 100; // 延长5秒
            killer.addEffect(new MobEffectInstance(MobEffectRegistry.SINFIRE_EMBRACE.get(), newDuration, current.getAmplifier()));
            killer.setRemainingFireTicks(newDuration);
        }
    }
}
