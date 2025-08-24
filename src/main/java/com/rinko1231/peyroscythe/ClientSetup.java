package com.rinko1231.peyroscythe;

import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.init.BlockRegistry;
import com.rinko1231.peyroscythe.renderer.*;
import com.rinko1231.peyroscythe.renderer.holy.*;
import com.rinko1231.peyroscythe.renderer.ice.GlacierIceBlockRenderer;
import com.rinko1231.peyroscythe.renderer.ice.IceTombModel;
import com.rinko1231.peyroscythe.renderer.ice.IceTombRenderer;
import com.rinko1231.peyroscythe.renderer.ice.IcyCometRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.rinko1231.peyroscythe.PeyroScythe.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void rendererRegister(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.ICE_QUAKE_AOE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntityRegistry.GLACIER_ICE_BLOCK_PROJECTILE.get(), GlacierIceBlockRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DEATH_SMOKE_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntityRegistry.CRIMSON_MOON.get(), CrimsonMoonRenderer::new);
        event.registerEntityRenderer(EntityRegistry.GOLDEN_BELL.get(), GoldenBellRenderer::new);
        //event.registerEntityRenderer(EntityRegistry.MUNDUS.get(), MundusRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ICY_COMET.get(), (context) -> new IcyCometRenderer(context, 1.5F));
        event.registerEntityRenderer(EntityRegistry.ICE_TOMB.get(), IceTombRenderer::new);
        event.registerEntityRenderer(EntityRegistry.FROST_FOG.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MUNDUS.get(), MundusRenderer::new);
        event.registerEntityRenderer(EntityRegistry.HOLY_LANCE_PROJECTILE.get(), HolyLanceRenderer::new);
        event.registerEntityRenderer(EntityRegistry.TEMPEST_EDGE_PROJECTILE.get(), TempestEdgeRenderer::new);
        event.registerEntityRenderer(EntityRegistry.TEMPEST_EDGE_H_PROJECTILE.get(), TempestEdgeHRenderer::new);
        event.registerEntityRenderer(EntityRegistry.NERO_HOLY_RAY_VISUAL_ENTITY.get(), NeroHolyRayRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ABYSS_MUD.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntityRegistry.SUMMONED_BLAZE.get(), BlazeRenderer::new);

        event.registerEntityRenderer(EntityRegistry.MADE_IN_HEAVEN_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MADE_IN_HEAVEN_REAL_PROJECTILE.get(), NoopRenderer::new);

    }
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(IceTombModel.LAYER_LOCATION, IceTombModel::createBodyLayer);
        event.registerLayerDefinition(MundusRenderer.MODEL_LAYER_LOCATION, MundusRenderer::createBodyLayer);
        event.registerLayerDefinition(GoldenBellRenderer.MODEL_LAYER_LOCATION, GoldenBellRenderer::createBodyLayer);
        event.registerLayerDefinition(NeroHolyRayRenderer.MODEL_LAYER_LOCATION, NeroHolyRayRenderer::createBodyLayer);


    }
    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockRegistry.MUNDUS_BE.get(), MundusBlockRenderer::new);
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent e) {
        e.registerAboveAll("flashbang_whiteout", (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (player == null) return;

            MobEffectInstance inst = player.getEffect(MobEffectRegistry.FLASHBANGED.get());
            if (inst == null) return;

            float alpha = getAlpha(inst);
            if (alpha <= 0.01f) return;

            int a = (int)(alpha * 255) << 24;
            int color = a | 0xFFFFFF; // ARGB
            guiGraphics.fill(0, 0, screenWidth, screenHeight, color);

        });
    }


    private static float getAlpha(MobEffectInstance inst) {
        int remaining = inst.getDuration();   // 剩余tick
        int amp = inst.getAmplifier();

// 只在最后 fadeTail tick 内淡出；其余时间保持全白
        int fadeTail = 30; // 尾段淡出窗口（例：30tick=1.5s），可调
        float alpha;
        if (remaining > fadeTail) {
            alpha = 1.0f; // 瞬间且持续全白
        } else {
            // 线性淡出（更干脆）
//  alpha = remaining / (float) fadeTail;

            // 或者使用“柔一点”的 ease-out（开头慢、末端快地消失）
            float x = remaining / (float) fadeTail;     // 0..1
            alpha = x * x * (3f - 2f * x);              // smoothstep
        }

// 放大强度（可选）
        alpha = Math.min(1.0f, alpha + amp * 0.1f);
        return alpha;
    }
}
