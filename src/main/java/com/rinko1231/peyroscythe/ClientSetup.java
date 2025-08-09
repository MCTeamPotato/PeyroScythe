package com.rinko1231.peyroscythe;

import com.rinko1231.peyroscythe.init.EntityRegistry;
import io.redspace.ironsspellbooks.entity.spells.ice_block.IceBlockRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.rinko1231.peyroscythe.renderer.GlacierIceBlockRenderer;

import static com.rinko1231.peyroscythe.PeyroScythe.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void rendererRegister(EntityRenderersEvent.RegisterRenderers event) {
event.registerEntityRenderer(EntityRegistry.FIRE_ERUPTION_AOE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntityRegistry.GLACIER_ICE_BLOCK_PROJECTILE.get(), GlacierIceBlockRenderer::new);
    }
}
