package com.rinko1231.peyroscythe.network.data;

import com.rinko1231.peyroscythe.init.PeyroMessages;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class PeyroSyncedSpellData {

    public static final long BLACK_FLAME_WINGS = 1L;

    private final int serverPlayerId;
    private @Nullable LivingEntity livingEntity;
    private long effectFlags; // 先用 long，方便后续扩展

    public PeyroSyncedSpellData(int serverPlayerId) {
        this.serverPlayerId = serverPlayerId;
        this.effectFlags = 0L;
    }

    public PeyroSyncedSpellData(@Nullable LivingEntity livingEntity) {
        this(livingEntity == null ? -1 : livingEntity.getId());
        this.livingEntity = livingEntity;
    }

    // ---- 基础方法 ----
    public int getServerPlayerId() {
        return this.serverPlayerId;
    }

    public boolean hasEffect(long effect) {
        return (this.effectFlags & effect) == effect;
    }

    public void addEffect(long effect) {
        this.effectFlags |= effect;
        this.doSync();
    }

    public void removeEffect(long effect) {
        this.effectFlags &= ~effect;
        this.doSync();
    }

    public long getEffectFlags() {
        return this.effectFlags;
    }

    public void setEffectFlags(long flags) {
        this.effectFlags = flags;
        this.doSync();
    }

    // ---- NBT 持久化 ----
    public void saveNBTData(CompoundTag tag) {
        tag.putLong("peyroEffectFlags", this.effectFlags);
    }

    public void loadNBTData(CompoundTag tag) {
        this.effectFlags = tag.getLong("peyroEffectFlags");
    }

    // ---- 网络同步 ----
    public void doSync() {
        if (this.livingEntity instanceof ServerPlayer serverPlayer) {
            PeyroMessages.sendToPlayer(new PeyroClientboundSyncPlayerData(this), serverPlayer);
            PeyroMessages.sendToPlayersTrackingEntity(new PeyroClientboundSyncPlayerData(this), serverPlayer);
        }
    }

    // ---- 序列化给 packet 用 ----
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.serverPlayerId);
        buf.writeLong(this.effectFlags);
    }

    public static PeyroSyncedSpellData read(FriendlyByteBuf buf) {
        PeyroSyncedSpellData data = new PeyroSyncedSpellData(buf.readInt());
        data.effectFlags = buf.readLong();
        return data;
    }
}
