package com.rinko1231.peyroscythe.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

/**
 * 客户端白屏闪光覆盖层。
 * <p>
 * 两阶段：
 * <ul>
 *   <li>{@link #start(int, int)}：淡入到全白后一直保持（等待服务端的传送完成信号）；</li>
 *   <li>{@link #end(int)}：从当前亮度开始淡出。</li>
 * </ul>
 * 这样"传送瞬间一定是全白"，不受网络延迟影响；
 * 同时有 12 秒兜底超时，避免服务端意外没发 END 时永久白屏。
 */
public final class FlashOverlay {

    private static final int SAFETY_TIMEOUT_TICKS = 240;

    private static boolean active = false;
    private static boolean fadingOut = false;

    private static int startTick = 0;
    private static int fadeInTicks = 10;
    private static int endTick = 0;
    private static int fadeOutTicks = 45;
    private static int argb = 0xFFFFFDF5;

    private FlashOverlay() {
    }

    public static void start(int fadeIn, int color) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        active = true;
        fadingOut = false;
        startTick = mc.player.tickCount;
        fadeInTicks = Math.max(0, fadeIn);
        argb = color;
    }

    public static void end(int fadeOut) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        if (!active) {
            // 没收到 START（极端情况）：直接全白再淡出
            start(0, argb);
        }
        fadingOut = true;
        endTick = mc.player.tickCount;
        fadeOutTicks = Math.max(1, fadeOut);
    }

    public static void stop() {
        active = false;
        fadingOut = false;
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
            stop();
            return;
        }

        float alpha;
        if (fadingOut) {
            int elapsed = mc.player.tickCount - endTick;
            if (elapsed < 0 || elapsed >= fadeOutTicks) {
                stop();
                return;
            }
            float t = elapsed / (float) fadeOutTicks;
            alpha = 1.0F - t * t * (3.0F - 2.0F * t);
        } else {
            int elapsed = mc.player.tickCount - startTick;
            if (elapsed < 0 || elapsed > SAFETY_TIMEOUT_TICKS) {
                stop();
                return;
            }
            float t = fadeInTicks <= 0 ? 1.0F : Math.min(1.0F, elapsed / (float) fadeInTicks);
            // ease-out：一开始就快速泛白，末尾放缓
            alpha = 1.0F - (1.0F - t) * (1.0F - t);
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
