package com.rinko1231.peyroscythe.event;

import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;


public class SinfireEmbraceEvent {

    // 注册攻击监听
    @SubscribeEvent
    public void onAttack(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        if (!attacker.hasEffect(MobEffectRegistry.SINFIRE_EMBRACE)) return;

        // 攻击目标时点燃
        if(event.getEntity().getRemainingFireTicks()<=4*20)
           event.getEntity().setRemainingFireTicks(4*20);
    }

    // 注册击杀监听
    @SubscribeEvent
    public void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity killer)) return;
        if (!killer.hasEffect(MobEffectRegistry.SINFIRE_EMBRACE)) return;

        MobEffectInstance current = killer.getEffect(MobEffectRegistry.SINFIRE_EMBRACE);
        if (current != null) {
            int newDuration = current.getDuration() + 100; // 延长5秒
            killer.addEffect(new MobEffectInstance(MobEffectRegistry.SINFIRE_EMBRACE, newDuration, current.getAmplifier()));
            killer.setRemainingFireTicks(newDuration);
        }
    }
}
