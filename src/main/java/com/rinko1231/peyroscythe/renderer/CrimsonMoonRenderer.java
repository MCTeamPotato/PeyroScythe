package com.rinko1231.peyroscythe.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.spellentity.CrimsonMoon;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.black_hole.BlackHole;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class CrimsonMoonRenderer extends EntityRenderer<CrimsonMoon> {
    private static final ResourceLocation CENTER_TEXTURE = PeyroScythe.id("textures/entity/crimson_moon/crimson_moon.png");
    private static final ResourceLocation BEAM_TEXTURE = PeyroScythe.id("textures/entity/crimson_moon/moonlight_beam.png");
    private static final float HALF_SQRT_3 = (float)(Math.sqrt((double)3.0F) / (double)2.0F);

    public CrimsonMoonRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    public void render(CrimsonMoon entity, float pEntityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight) {
        poseStack.pushPose();
        poseStack.translate((double)0.0F, entity.getBoundingBox().getYsize() / (double)2.0F, (double)0.0F);
        float entityScale = entity.getBbWidth() * 0.025F;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        poseStack.scale(0.5F * entityScale, 0.5F * entityScale, 0.5F * entityScale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.translate(5.0F, 0.0F, 0.0F);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(CENTER_TEXTURE));
        consumer.addVertex(poseMatrix, 0.0F, -8.0F, -8.0F).setColor(255, 255, 255, 255).setUv(0.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0.0F, 1.0F, 0.0F) ;
        consumer.addVertex(poseMatrix, 0.0F, 8.0F, -8.0F).setColor(255, 255, 255, 255).setUv(0.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal( 0.0F, 1.0F, 0.0F) ;
        consumer.addVertex(poseMatrix, 0.0F, 8.0F, 8.0F).setColor(255, 255, 255, 255).setUv(1.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0.0F, 1.0F, 0.0F) ;
        consumer.addVertex(poseMatrix, 0.0F, -8.0F, 8.0F).setColor(255, 255, 255, 255).setUv(1.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal( 0.0F, 1.0F, 0.0F) ;
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.translate((double)0.0F, entity.getBoundingBox().getYsize() / (double)2.0F, (double)0.0F);
        float animationProgress = ((float)entity.tickCount + partialTicks) / 200.0F;
        float fadeProgress = 0.5F;
        RandomSource randomSource = RandomSource.create(432L);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.energySwirl(BEAM_TEXTURE, 0.0F, 0.0F));
        float segments = Math.min(animationProgress, 0.8F);

        for(int i = 0; (float)i < (segments + segments * segments) / 2.0F * 20.0F; ++i) {
            poseStack.mulPose(Axis.XP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(randomSource.nextFloat() * 360.0F + animationProgress * 90.0F));
            float size1 = (randomSource.nextFloat() * 4.0F + 2.0F + fadeProgress * 2.0F) * entityScale * 0.4F;
            Matrix4f matrix = poseStack.last().pose();
            Matrix3f normalMatrix2 = poseStack.last().normal();
            int alpha = (int)(255.0F * (1.0F - fadeProgress));
            drawTriangle(vertexConsumer, matrix, normalMatrix2, size1);
        }

        poseStack.popPose();
        super.render(entity, pEntityYaw, partialTicks, poseStack, bufferSource, pPackedLight);
    }

    public ResourceLocation getTextureLocation(CrimsonMoon pEntity) {
        return IcicleRenderer.TEXTURE;
    }

    private static void drawTriangle(VertexConsumer consumer, Matrix4f poseMatrix, Matrix3f normalMatrix, float size) {
        consumer.addVertex(poseMatrix, 0.0F, 0.0F, 0.0F)
                .setColor(255, 0, 255, 255)
                .setUv(0.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(0.0F, 1.0F, 0.0F)
                 ;
        consumer.addVertex(poseMatrix, 0.0F, 3.0F * size, -1.0F * size)
                .setColor(0, 0, 0, 0)
                .setUv(0.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880).setNormal(0.0F, 1.0F, 0.0F)
                 ;
        consumer.addVertex(poseMatrix, 0.0F, 3.0F * size, 1.0F * size)
                .setColor(0, 0, 0, 0).setUv(1.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880)
                .setNormal( 0.0F, 1.0F, 0.0F)
                 ;
        consumer.addVertex(poseMatrix, 0.0F, 0.0F, 0.0F)
                .setColor(255, 0, 255, 255)
                .setUv(1.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(0.0F, 1.0F, 0.0F)
                 ;
    }
}
