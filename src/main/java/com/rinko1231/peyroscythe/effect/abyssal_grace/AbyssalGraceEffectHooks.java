package com.rinko1231.peyroscythe.effect.abyssal_grace;

import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.rinko1231.peyroscythe.PeyroScythe.MODID;
import static com.rinko1231.peyroscythe.effect.abyssal_grace.AbyssalGraceEffect.TAG_HAS_GRACE;
import static com.rinko1231.peyroscythe.effect.abyssal_grace.AbyssalGraceEvent.TAG_PENDING_REV;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AbyssalGraceEffectHooks {

    // 获得效果：写入 persistentData
    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (event.getEntity().level().isClientSide) return;

        MobEffectInstance inst = event.getEffectInstance(); // Added 通常非空，但仍然防御
        if (inst != null && inst.getEffect() == MobEffectRegistry.ABYSSAL_GRACE.get()) {
            event.getEntity().getPersistentData().putBoolean(TAG_HAS_GRACE, true);
        }
    }

    // 效果被移除（包括被牛奶、/effect clear、死亡、他模组清空等触发）
    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (event.getEntity().level().isClientSide) return;

        // 有些路径 inst 为 null，但 getEffect() 可能给得出来；两个都判
        MobEffectInstance inst = event.getEffectInstance();
        MobEffect effect = inst != null ? inst.getEffect() : event.getEffect();

        if (effect == MobEffectRegistry.ABYSSAL_GRACE.get()) {
            event.getEntity().getPersistentData().remove(TAG_HAS_GRACE);
            event.getEntity().getPersistentData().remove(TAG_PENDING_REV);
        }
    }

    // 到期：双保险
    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEntity().level().isClientSide) return;

        MobEffectInstance inst = event.getEffectInstance(); // Expired 这里一般不为空，但也防御
        if (inst != null && inst.getEffect() == MobEffectRegistry.ABYSSAL_GRACE.get()) {
            event.getEntity().getPersistentData().remove(TAG_HAS_GRACE);
            event.getEntity().getPersistentData().remove(TAG_PENDING_REV);
        }
    }
}
