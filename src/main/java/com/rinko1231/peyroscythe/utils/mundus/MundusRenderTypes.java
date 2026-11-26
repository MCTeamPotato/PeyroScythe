package com.rinko1231.peyroscythe.utils.mundus;

// MRenderTypes.java
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;


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

    /**
     * 发光球体用的加色混合渲染类型：带贴图、无光照、双面、不写深度。
     * 适合做圣光/光晕/符文环这类“自发光”元素。
     */
    public static RenderType getMundusGlow(ResourceLocation texture) {
        return create("mundus_glow_" + texture.getPath().replace('/', '_'),
                DefaultVertexFormat.POSITION_COLOR_TEX,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                CompositeState.builder()
                        .setShaderState(POSITION_COLOR_TEX_SHADER)
                        .setTextureState(new TextureStateShard(texture, false, false))
                        .setTransparencyState(ADDITIVE_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setLightmapState(NO_LIGHTMAP)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(false));
    }
}
