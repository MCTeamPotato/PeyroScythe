package com.rinko1231.peyroscythe.effect.abyssal_grace;

import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.utils.abyssmud.AbyssMudLink;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;


import static com.rinko1231.peyroscythe.PeyroScythe.MODID;
import static com.rinko1231.peyroscythe.effect.abyssal_grace.AbyssalGraceEffect.TAG_HAS_GRACE;
import static com.rinko1231.peyroscythe.effect.abyssal_grace.AbyssalGraceEvent.TAG_PENDING_REV;


public final class AbyssalGraceEffectHooks {

    @SubscribeEvent
    public void onClone(net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        Player oldP = event.getOriginal();
        Player newP = event.getEntity();

        CompoundTag oldTag = oldP.getPersistentData();
        CompoundTag newTag = newP.getPersistentData();

        // 拷贝“维度 -> 渊泥UUID”映射
        if (oldTag.contains(AbyssMudLink.TAG_ABYSS_MUDS, Tag.TAG_COMPOUND)) {
            newTag.put(AbyssMudLink.TAG_ABYSS_MUDS,
                    oldTag.getCompound(AbyssMudLink.TAG_ABYSS_MUDS).copy());
        }

        // 这些标志别继承，避免脏状态
        newTag.remove(AbyssalGraceEffect.TAG_HAS_GRACE);
        newTag.remove(AbyssalGraceEvent.TAG_PENDING_REV);
    }

    // 到期：双保险
    @SubscribeEvent
    public void onEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEntity().level().isClientSide) return;

        MobEffectInstance inst = event.getEffectInstance(); // Expired 这里一般不为空，但也防御
        if (inst != null && inst.getEffect() == MobEffectRegistry.ABYSSAL_GRACE.get()) {
            event.getEntity().getPersistentData().remove(TAG_HAS_GRACE);
            event.getEntity().getPersistentData().remove(TAG_PENDING_REV);
        }
    }
}
