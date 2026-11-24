package com.rinko1231.peyroscythe.network.data;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class PeyroClientData {
    // 存一个玩家自己的同步数据
    private static final PeyroSyncedSpellData emptyData = new PeyroSyncedSpellData(-999);
    private static final Map<Integer, PeyroSyncedSpellData> playerSyncedDataLookup = new HashMap<>();

    // 设置/更新某个玩家的数据（packet 里调用）
    public static void setSyncedData(PeyroSyncedSpellData data) {
        playerSyncedDataLookup.put(data.getServerPlayerId(), data);
    }

    // 获取某个实体的同步数据
    public static PeyroSyncedSpellData getSyncedData(LivingEntity entity) {
        if (entity instanceof Player) {
            return playerSyncedDataLookup.getOrDefault(entity.getId(), emptyData);
        } else {
            return emptyData;
        }
    }


    // 获取本地玩家的数据
    public static PeyroSyncedSpellData getLocalPlayerData() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            return getSyncedData(player);
        }
        return emptyData;
    }
}
