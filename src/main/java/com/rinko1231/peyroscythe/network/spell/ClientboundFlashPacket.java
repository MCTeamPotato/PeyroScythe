package com.rinko1231.peyroscythe.network.spell;

import com.rinko1231.peyroscythe.client.FlashOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 -> 客户端：控制白屏闪光。
 * <p>
 * 分两段发送，保证"传送那一刻屏幕一定是全白"：
 * 1. {@link #MODE_START}：开始淡入并一直保持纯白（时长只用于淡入）；
 * 2. 服务端真正执行传送的同一 tick 再发送 {@link #MODE_END}：开始淡出。
 * 这样即使网络有延迟，也不会出现"还没全白就被传送"。
 * <p>
 * 相比用 MobEffect 实现，这里完全由客户端覆盖层绘制，不会出现原版药水的旋转粒子，
 * 也不会在 HUD/物品栏里留下效果图标。
 */
public class ClientboundFlashPacket {

    public static final byte MODE_START = 0;
    public static final byte MODE_END = 1;

    private final byte mode;
    private final int durationTicks;
    private final int argb;

    public ClientboundFlashPacket(byte mode, int durationTicks, int argb) {
        this.mode = mode;
        this.durationTicks = durationTicks;
        this.argb = argb;
    }

    public ClientboundFlashPacket(FriendlyByteBuf buf) {
        this.mode = buf.readByte();
        this.durationTicks = buf.readVarInt();
        this.argb = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeByte(this.mode);
        buf.writeVarInt(this.durationTicks);
        buf.writeInt(this.argb);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (this.mode == MODE_END) {
                FlashOverlay.end(this.durationTicks);
            } else {
                FlashOverlay.start(this.durationTicks, this.argb);
            }
        }));
        return true;
    }

    public static ClientboundFlashPacket start(int fadeInTicks, int argb) {
        return new ClientboundFlashPacket(MODE_START, fadeInTicks, argb);
    }

    public static ClientboundFlashPacket end(int fadeOutTicks) {
        return new ClientboundFlashPacket(MODE_END, fadeOutTicks, 0xFFFFFFFF);
    }
}
