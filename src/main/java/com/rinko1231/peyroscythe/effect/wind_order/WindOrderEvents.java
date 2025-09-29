package com.rinko1231.peyroscythe.effect.wind_order;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;


import static com.rinko1231.peyroscythe.init.MobEffectRegistry.WIND_ORDER;


public class WindOrderEvents {
    @SubscribeEvent
    public void onEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MobEffectInstance inst = event.getEffectInstance(); // Expired 这里一般不为空，但也防御
            if (inst != null && inst.getEffect() == WIND_ORDER.get()) {
                // 逻辑：只在空中时才加缓降
                if (!player.onGround() && !player.isInWater() && !player.isCreative() && !player.isSpectator()) {
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0, true, false));
                }
            }
        }
    }


}

