package com.rinko1231.peyroscythe.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rinko1231.peyroscythe.spellentity.IceTombEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;


public class IceTombRenderer extends EntityRenderer<IceTombEntity> {

    // 使用原版普通冰材质
    private static final ResourceLocation VANILLA_ICE = new ResourceLocation("minecraft", "textures/block/ice.png");

    private final IceTombModel model;

    public IceTombRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new IceTombModel(ctx.bakeLayer(IceTombModel.LAYER_LOCATION));
    }

    @Override
    public void render(IceTombEntity entity, float yaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();

        // 跟随实体朝向
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));

        // 按实体实际 AABB 相对注册尺寸进行缩放（看起来“把一块冰拉伸”）
        float xScale = entity.getBbWidth() / entity.getType().getDimensions().width;
        float yScale = entity.getBbHeight() / entity.getType().getDimensions().height;
        poseStack.scale(xScale, -yScale, -xScale);

        // 下移一点让模型落在脚下（与 1.21.1 源实现一致）
        poseStack.translate(0.0, -1.501, 0.0);

        // 动画（当前空实现）
        this.model.setupAnim(entity, partialTicks, 0.0F, 0.0F, entity.getYRot(), entity.getXRot());

        // 只渲染一次，使用普通 translucent，贴原版冰纹理
        VertexConsumer vc = buffers.getBuffer(RenderType.entityTranslucent(VANILLA_ICE));
        this.model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(IceTombEntity entity) {
        return VANILLA_ICE;
    }
}
