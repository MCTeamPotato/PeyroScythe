package com.rinko1231.peyroscythe.utils.mundus;

// MRenderTypes.java
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;


public class MundusRenderTypes extends RenderType {


    private static final TransparencyStateShard GOLD_ALPHA_TRANSPARENCY =
            new TransparencyStateShard("gold_alpha_transparency",
                    () -> {
                        RenderSystem.enableBlend();
                        RenderSystem.blendFuncSeparate(
                                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE,
                                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.ONE,
                                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
                        );
                    },
                    () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultBlendFunc();
                    });

    private MundusRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode,
                              int bufferSize, boolean affectsCrumbling, boolean sortOnUpload,
                              Runnable setup, Runnable clear) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
    }


    public static RenderType getMundusShine() {
        return create("mundus_shine",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.QUADS,
                256,
                true,
                true,
                CompositeState.builder()
                        .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                        .setTransparencyState(GOLD_ALPHA_TRANSPARENCY)
                        .setCullState(CULL)
                        .setLightmapState(NO_LIGHTMAP)
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .setOutputState(PARTICLES_TARGET)
                        .createCompositeState(true));
    }
}
