package com.rinko1231.peyroscythe.network.spell;

import com.rinko1231.peyroscythe.client.FlashOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 -> 客户端：触发一次"眼前一白"的屏幕闪光。
 * <p>
 * 相比用 MobEffect 实现，这里完全由客户端覆盖层绘制，不会出现原版药水的旋转粒子，
 * 也不会在 HUD/物品栏里留下效果图标；淡入/保持/淡出的时长由服务端决定，
 * 因此"闪光最亮时传送"可以被精确控制。
 */
public class ClientboundFlashPacket {
    private final int fadeInTicks;
    private final int holdTicks;
    private final int fadeOutTicks;
    private final int argb;

    public ClientboundFlashPacket(int fadeInTicks, int holdTicks, int fadeOutTicks, int argb) {
        this.fadeInTicks = fadeInTicks;
        this.holdTicks = holdTicks;
        this.fadeOutTicks = fadeOutTicks;
        this.argb = argb;
    }

    public ClientboundFlashPacket(FriendlyByteBuf buf) {
        this.fadeInTicks = buf.readVarInt();
        this.holdTicks = buf.readVarInt();
        this.fadeOutTicks = buf.readVarInt();
        this.argb = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(this.fadeInTicks);
        buf.writeVarInt(this.holdTicks);
        buf.writeVarInt(this.fadeOutTicks);
        buf.writeInt(this.argb);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> FlashOverlay.start(this.fadeInTicks, this.holdTicks, this.fadeOutTicks, this.argb)));
        return true;
    }
}
