package com.rinko1231.peyroscythe.network.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;

public class PeyroMagicData {
    private ServerPlayer serverPlayer;
    private boolean isMob;

    // 自己的同步数据
    private PeyroSyncedSpellData syncedSpellData;

    public PeyroMagicData() {
        this(false);
    }

    public PeyroMagicData(boolean isMob) {
        this.isMob = isMob;
        this.serverPlayer = null;
    }

    public PeyroMagicData(ServerPlayer serverPlayer) {
        this(false);
        this.serverPlayer = serverPlayer;
    }

    public void setServerPlayer(ServerPlayer serverPlayer) {
        if (this.serverPlayer == null && serverPlayer != null) {
            this.serverPlayer = serverPlayer;
        }
    }

    /**
     * 获取玩家的 MySyncedSpellData
     */
    public PeyroSyncedSpellData getSyncedData() {
        if (this.syncedSpellData == null) {
            this.syncedSpellData = new PeyroSyncedSpellData(this.serverPlayer);
        }
        return this.syncedSpellData;
    }

    /**
     * 保存 NBT 数据
     */
    public void saveNBTData(CompoundTag tag) {
        // 保存同步数据
        if (this.syncedSpellData != null) {
            this.syncedSpellData.saveNBTData(tag);
        }
    }

    /**
     * 读取 NBT 数据
     */
    public void loadNBTData(CompoundTag tag) {
        this.getSyncedData().loadNBTData(tag);

            //this.serverPlayer.displayClientMessage(Component.literal("[DEBUG] Loaded effectFlags=" + this.syncedSpellData.getEffectFlags()), false);

    }

    /**
     * 获取玩家绑定的 PeyroMagicData
     */
    public static PeyroMagicData getPlayerData(LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            LazyOptional<PeyroMagicData> cap = serverPlayer.getCapability(PeyroMagicProvider.PLAYER_PEYRO_MAGIC);
            if (cap.isPresent()) {
                PeyroMagicData data = cap.resolve().orElseGet(() -> new PeyroMagicData(serverPlayer));
                data.setServerPlayer(serverPlayer);
                return data;
            } else {
                return new PeyroMagicData(serverPlayer);
            }
        } else {
            return new PeyroMagicData(true); // 非玩家实体
        }
    }
}
