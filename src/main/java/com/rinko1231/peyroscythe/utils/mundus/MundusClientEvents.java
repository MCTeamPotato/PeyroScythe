package com.rinko1231.peyroscythe.utils.mundus;

import com.mojang.blaze3d.systems.RenderSystem;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;


import static com.rinko1231.peyroscythe.PeyroScythe.MODID;
/*
@EventBusSubscriber(
        modid = MODID,
        bus = EventBusSubscriber.Bus.MOD,
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
*/