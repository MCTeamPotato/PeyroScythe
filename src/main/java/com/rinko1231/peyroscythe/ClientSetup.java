package com.rinko1231.peyroscythe;

import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.renderer.*;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
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
        event.registerEntityRenderer(EntityRegistry.ICY_COMET.get(), (context) -> new IcyCometRenderer(context, 1.5F));
        event.registerEntityRenderer(EntityRegistry.ICE_TOMB.get(), IceTombRenderer::new);
        event.registerEntityRenderer(EntityRegistry.FROST_FOG.get(), NoopRenderer::new);

    }
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(IceTombModel.LAYER_LOCATION, IceTombModel::createBodyLayer);
    }
}
