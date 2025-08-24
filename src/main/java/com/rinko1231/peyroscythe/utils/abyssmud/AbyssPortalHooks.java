package com.rinko1231.peyroscythe.utils.abyssmud;

import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "peyroscythe", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AbyssPortalHooks {
    // 你的标记键
    public static final String TAG_HAS     = "peyroscythe:abyss_undead";
    public static final String TAG_PENDING = "peyroscythe:abyss_pending_revive";

    /** 维度旅行“之前”触发（可取消旅行的那个事件）——在这里先清掉 BUFF 与标记 */
    @SubscribeEvent
    public static void onTravel(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;

        clearAbyssUndead(p);

    }

    /** 维度旅行“之后”触发——作为兜底（以防某些传送器不触发 onTravel） */
    @SubscribeEvent
    public static void onChanged(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;
        clearAbyssUndead(p); // 再清一次，双保险
    }

    private static void clearAbyssUndead(ServerPlayer serverPlayer) {
        // 去掉效果与数据标记
        if (serverPlayer.hasEffect(MobEffectRegistry.ABYSSAL_GRACE.get()))  serverPlayer.removeEffect(MobEffectRegistry.ABYSSAL_GRACE.get());
        serverPlayer.getPersistentData().remove(TAG_HAS);
        serverPlayer.getPersistentData().remove(TAG_PENDING);
    }
}

