package com.rinko1231.peyroscythe.utils;


import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.util.Mth;

import java.util.Map;
import java.util.UUID;

import static com.rinko1231.peyroscythe.PeyroScythe.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)

public final class TimeFlowAccelerator {

    private TimeFlowAccelerator() {}

    private static final class Entry {
        int factor;      // 期望倍率（>=1）
        int lastTick;    // 最后声明的服务器tick
    }

    // 每个维度一张表
    private static final Map<ResourceKey<Level>, Object2ObjectOpenHashMap<UUID, Entry>> ACTIVE
            = new java.util.concurrent.ConcurrentHashMap<>();

    //在本tick声明：爷要加速！
    public static void declare(ServerLevel level, UUID playerId, int factor) {
        factor = Mth.clamp(factor, 1, 1000); // 上限随意
        var map = ACTIVE.computeIfAbsent(level.dimension(), k -> new Object2ObjectOpenHashMap<>());
        var e = map.get(playerId);
        int curTick = level.getServer().getTickCount();
        if (e == null) {
            e = new Entry();
            map.put(playerId, e);
        }
        e.factor = factor;
        e.lastTick = curTick;
    }

    /** 维度tick末尾：取本tick内声明过的最大倍率，追加时间推进。 */
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.level instanceof ServerLevel sl)) return;

        var map = ACTIVE.get(sl.dimension());
        if (map == null || map.isEmpty()) return;

        int curTick = sl.getServer().getTickCount();

        // 只统计“本tick声明过”的玩家，顺便清理过期项
        int maxFactor = 1;
        var it = map.entrySet().iterator();
        while (it.hasNext()) {
            var ent = it.next().getValue();
            if (ent.lastTick == curTick) {
                if (ent.factor > maxFactor) maxFactor = ent.factor;
            } else if (ent.lastTick < curTick - 1) {
                it.remove(); // 没有持续声明就视为停止施法
            }
        }

        if (maxFactor <= 1) return;

        // 可选：尊重 gamerule doDaylightCycle
        if (!sl.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) return;

        long extra = maxFactor - 1L;      // 本tick额外推进的时间
        sl.setDayTime(sl.getDayTime() + extra);
        boolean daylight = sl.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT);

        //发包
        for (ServerPlayer sp : sl.players()) {
            sp.connection.send(new ClientboundSetTimePacket(
                    sl.getGameTime(),
                    sl.getDayTime(),
                    daylight
            ));
        }
    }
}
