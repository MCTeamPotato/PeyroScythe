package com.rinko1231.peyroscythe.utils.mundus;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rinko1231.peyroscythe.renderer.holy.MundusBlockRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.rinko1231.peyroscythe.PeyroScythe.MODID;

@Mod.EventBusSubscriber(
        modid = MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class MundusClientEvents {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            RenderSystem.runAsFancy(() ->
                    MundusBlockRenderer.renderEntireBatch(
                            event.getLevelRenderer(),
                            event.getPoseStack(),
                            event.getRenderTick(),
                            event.getCamera(),
                            event.getPartialTick()
                    )
            );
        }
    }
}
