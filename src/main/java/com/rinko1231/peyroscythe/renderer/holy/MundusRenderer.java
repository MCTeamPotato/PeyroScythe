package com.rinko1231.peyroscythe.renderer.holy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.spellentity.holy.MundusEntity;
import com.rinko1231.peyroscythe.utils.mundus.MundusRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * "世界"投影的渲染器。
 * 结构：外发光 -> 旋转符文环 -> 核心 -> 环绕光点，另有一个水平旋转的光环增加立体感。
 * 全部是朝向相机的 billboard + 加色混合；贴图都是 16x16 像素画，放大后保留 Minecraft 的颗粒感。
 */
public class MundusRenderer extends EntityRenderer<MundusEntity> {

    private static final ResourceLocation CORE_TEXTURE =
            PeyroScythe.id("textures/entity/mundus/orb_core.png");
    private static final ResourceLocation GLOW_TEXTURE =
            PeyroScythe.id("textures/entity/mundus/orb_glow.png");
    private static final ResourceLocation RING_TEXTURE =
            PeyroScythe.id("textures/entity/mundus/rune_ring.png");
    private static final ResourceLocation SPARK_TEXTURE =
            PeyroScythe.id("textures/entity/mundus/spark.png");

    // RenderType 只创建一次，避免每帧生成新的缓冲对象
    private static final net.minecraft.client.renderer.RenderType CORE_TYPE =
            MundusRenderTypes.getMundusGlow(CORE_TEXTURE);
    private static final net.minecraft.client.renderer.RenderType GLOW_TYPE =
            MundusRenderTypes.getMundusGlow(GLOW_TEXTURE);
    private static final net.minecraft.client.renderer.RenderType RING_TYPE =
            MundusRenderTypes.getMundusGlow(RING_TEXTURE);
    private static final net.minecraft.client.renderer.RenderType SPARK_TYPE =
            MundusRenderTypes.getMundusGlow(SPARK_TEXTURE);

    public MundusRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MundusEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        float time = entity.tickCount + partialTick;
        float radius = Math.max(entity.getRadius(), 0.5F);
        float scale = radius * 0.55F * (1.0F + 0.05F * Mth.sin(time * 0.12F));
        float bob = Mth.sin(time * 0.06F) * 0.08F;

        poseStack.pushPose();
        poseStack.translate(0.0D, entity.getBbHeight() * 0.5D + bob, 0.0D);

        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        // 外发光
        VertexConsumer glow = buffer.getBuffer(GLOW_TYPE);
        drawQuad(glow, poseStack.last().pose(), scale * 2.6F, 1.0F, 0.95F, 0.80F, 0.85F);

        // 旋转的符文环
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(time * 0.7F));
        VertexConsumer ring = buffer.getBuffer(RING_TYPE);
        drawQuad(ring, poseStack.last().pose(), scale * 1.9F, 1.0F, 0.90F, 0.55F, 0.90F);
        poseStack.popPose();

        // 高亮核心
        VertexConsumer core = buffer.getBuffer(CORE_TYPE);
        drawQuad(core, poseStack.last().pose(), scale * 0.95F, 1.0F, 1.0F, 1.0F, 1.0F);

        // 环绕光点数量 = 剩余使用次数，直观显示还能传送几次
        int uses = Math.max(0, entity.getUses());
        int maxUses = Math.max(1, entity.getMaxUses());
        VertexConsumer spark = buffer.getBuffer(SPARK_TYPE);
        for (int i = 0; i < uses; i++) {
            double angle = Math.toRadians(time * 12.0D + i * (360.0D / maxUses));
            float x = (float) Math.cos(angle) * scale * 1.6F;
            float y = (float) Math.sin(angle) * scale * 0.9F;
            poseStack.pushPose();
            poseStack.translate(x, y, 0.02F);
            drawQuad(spark, poseStack.last().pose(), scale * 0.35F, 1.0F, 0.92F, 0.60F, 0.90F);
            poseStack.popPose();
        }
        poseStack.popPose();

        // 水平方向缓慢旋转的光环，让球看起来更立体
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 0.5F));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        VertexConsumer flatRing = buffer.getBuffer(RING_TYPE);
        drawQuad(flatRing, poseStack.last().pose(), scale * 1.6F, 1.0F, 0.85F, 0.45F, 0.50F);
        poseStack.popPose();

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private static void drawQuad(VertexConsumer consumer, Matrix4f matrix,
                                 float half, float r, float g, float b, float a) {
        consumer.vertex(matrix, -half, -half, 0.0F).color(r, g, b, a).uv(0.0F, 1.0F).endVertex();
        consumer.vertex(matrix, -half, half, 0.0F).color(r, g, b, a).uv(0.0F, 0.0F).endVertex();
        consumer.vertex(matrix, half, half, 0.0F).color(r, g, b, a).uv(1.0F, 0.0F).endVertex();
        consumer.vertex(matrix, half, -half, 0.0F).color(r, g, b, a).uv(1.0F, 1.0F).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(MundusEntity entity) {
        return CORE_TEXTURE;
    }
}
