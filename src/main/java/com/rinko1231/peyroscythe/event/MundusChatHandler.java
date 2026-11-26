package com.rinko1231.peyroscythe.event;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.spellentity.holy.MundusEntity;
import com.rinko1231.peyroscythe.utils.mundus.TeleportHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * 监听玩家聊天：站在"世界"投影附近时，说出咒语即可触发传送。
 * <p>
 * 咒语默认是 {@code mundus}（可在配置里改），匹配时不区分大小写；
 * 触发后聊天内容会被取消，不会广播给其他玩家。
 */
@Mod.EventBusSubscriber(modid = PeyroScythe.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MundusChatHandler {

    /** 玩家必须距离小球多近才能念咒。 */
    private static final double ORB_RANGE = 16.0D;

    private MundusChatHandler() {
    }

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        if (player == null || player.level().isClientSide()) {
            return;
        }

        String said = event.getMessage().getString().trim();
        if (!isIncantation(said)) {
            return;
        }

        MundusEntity orb = findNearestOrb(player);
        if (orb == null) {
            return;
        }

        // 咒语不进入公共聊天
        event.setCanceled(true);

        int spellLevel = Math.max(1, orb.getSpellLevel());
        int searchRadius = Math.min(32 + 32 * spellLevel, 160);

        TeleportHelper.tpToNearestVillage(player, searchRadius, orb);
    }

    private static boolean isIncantation(String said) {
        if (said.isEmpty()) {
            return false;
        }
        String configured = PeyroScytheConfig.mundusIncantation.get();
        if (configured != null && !configured.isBlank() && said.equalsIgnoreCase(configured.trim())) {
            return true;
        }
        // 原著风格的两个别名，保持旧存档/旧文案可用
        return said.equalsIgnoreCase("ad mundum") || said.equalsIgnoreCase("mundus");
    }

    private static MundusEntity findNearestOrb(ServerPlayer player) {
        List<MundusEntity> orbs = player.level().getEntitiesOfClass(
                MundusEntity.class,
                player.getBoundingBox().inflate(ORB_RANGE),
                orb -> orb.isAlive() && !orb.isRemoved() && orb.getUses() > 0);
        MundusEntity nearest = null;
        double best = Double.MAX_VALUE;
        for (MundusEntity orb : orbs) {
            double d = orb.distanceToSqr(player);
            if (d < best) {
                best = d;
                nearest = orb;
            }
        }
        return nearest;
    }
}
