package com.rinko1231.peyroscythe.network.data;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PeyroClientboundSyncPlayerData {
    private final PeyroSyncedSpellData syncedData;

    public PeyroClientboundSyncPlayerData(PeyroSyncedSpellData data) {
        this.syncedData = data;
    }

    public PeyroClientboundSyncPlayerData(FriendlyByteBuf buf) {
        this.syncedData = PeyroSyncedSpellData.read(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        this.syncedData.write(buf);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // 这里是客户端逻辑，例如保存到 ClientData 或触发渲染
            PeyroClientData.setSyncedData(this.syncedData);
        });
        return true;
    }

    public PeyroSyncedSpellData getData() {
        return syncedData;
    }
}
