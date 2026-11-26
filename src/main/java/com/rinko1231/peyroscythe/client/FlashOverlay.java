package com.rinko1231.peyroscythe.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

/**
 * 客户端白屏闪光覆盖层。
 * <p>
 * 时间轴：淡入(fadeIn) -> 保持纯白(hold) -> 淡出(fadeOut)。
 * 服务端在"保持纯白"的阶段执行传送，所以玩家看不到地点突变。
 * 渲染注册在 {@code ClientSetup#registerOverlays}，位于所有原版 HUD 之上。
 */
public final class FlashOverlay {

    private static boolean active = false;
    private static int startTick = 0;
    private static int fadeInTicks = 10;
    private static int holdTicks = 8;
    private static int fadeOutTicks = 45;
    private static int argb = 0xFFFFFFFF;

    private FlashOverlay() {
    }

    public static void start(int fadeIn, int hold, int fadeOut, int color) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        active = true;
        startTick = mc.player.tickCount;
        fadeInTicks = Math.max(1, fadeIn);
        holdTicks = Math.max(0, hold);
        fadeOutTicks = Math.max(1, fadeOut);
        argb = color;
    }

    public static void stop() {
        active = false;
    }

    public static boolean isActive() {
        return active;
    }

    public static void render(GuiGraphics graphics) {
        if (!active) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            active = false;
            return;
        }

        int elapsed = mc.player.tickCount - startTick;
        int total = fadeInTicks + holdTicks + fadeOutTicks;
        if (elapsed < 0 || elapsed >= total) {
            active = false;
            return;
        }

        float alpha;
        if (elapsed < fadeInTicks) {
            float t = elapsed / (float) fadeInTicks;
            // ease-out：一开始就快速泛白，末尾放缓，避免"啪一下变白"
            alpha = 1.0F - (1.0F - t) * (1.0F - t);
        } else if (elapsed < fadeInTicks + holdTicks) {
            alpha = 1.0F;
        } else {
            float t = (elapsed - fadeInTicks - holdTicks) / (float) fadeOutTicks;
            alpha = 1.0F - t * t * (3.0F - 2.0F * t);
        }

        int baseAlpha = (argb >>> 24) & 0xFF;
        int a = Mth.clamp((int) (alpha * baseAlpha), 0, 255);
        if (a <= 0) {
            return;
        }

        int color = (a << 24) | (argb & 0xFFFFFF);
        graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), color);
    }
}
