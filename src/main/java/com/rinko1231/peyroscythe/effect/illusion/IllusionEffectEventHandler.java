package com.rinko1231.peyroscythe.effect.illusion;

import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;



public class IllusionEffectEventHandler {

    @SubscribeEvent
    public void onMobEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance newEffect = event.getEffectInstance();

        //被byd神秘遗物搞怕了
        if (entity == null || newEffect == null ||newEffect.getEffect() == null) return;

        // 如果获得恐惧，就移除疯狂
        if (newEffect.getEffect() == MobEffectRegistry.ILLUSION_FEAR) {
            if (entity.hasEffect(MobEffectRegistry.ILLUSION_CRAZY)) {
                entity.removeEffect(MobEffectRegistry.ILLUSION_CRAZY);
            }
        }

        // 如果获得疯狂，就移除恐惧
        if (newEffect.getEffect() == MobEffectRegistry.ILLUSION_CRAZY.get()) {
            if (entity.hasEffect(MobEffectRegistry.ILLUSION_FEAR)) {
                entity.removeEffect(MobEffectRegistry.ILLUSION_FEAR);
            }
        }
    }
}
