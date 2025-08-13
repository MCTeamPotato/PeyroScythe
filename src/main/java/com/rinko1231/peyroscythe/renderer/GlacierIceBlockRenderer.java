package com.rinko1231.peyroscythe.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rinko1231.peyroscythe.spellentity.GlacierIceBlockProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

// 专用渲染器：只给 GlacierIceBlockProjectile 用
public class GlacierIceBlockRenderer extends GeoEntityRenderer<GlacierIceBlockProjectile> {
    public GlacierIceBlockRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GlacierIceBlockModel());
        this.shadowRadius = 1.5F; // 基础阴影；下面按比例再放大
    }

    @Override
    public void preRender(PoseStack poseStack,
                          GlacierIceBlockProjectile entity,
                          BakedGeoModel model,
                          MultiBufferSource bufferSource,
                          VertexConsumer buffer, boolean isReRender,
                          float partialTick, int packedLight, int packedOverlay,
                          float r, float g, float b, float a) {
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, r, g, b, a);

        float s = entity.getScaleFactor();
        poseStack.scale(s, s, s);
        this.shadowRadius = 1.5F * s; // 阴影也放大
    }
}
