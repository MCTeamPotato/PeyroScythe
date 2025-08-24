package com.rinko1231.peyroscythe.renderer.holy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.spellentity.holy.HolyLanceProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class HolyLanceRenderer extends EntityRenderer<HolyLanceProjectile> {
    public static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            PeyroScythe.id("textures/entity/holy_lance/holy_lance_1.png"),
            PeyroScythe.id("textures/entity/holy_lance/holy_lance_2.png")
            //IronsSpellbooks.id("textures/entity/lightning_lance/lightning_lance_3.png"),
            //IronsSpellbooks.id("textures/entity/lightning_lance/lightning_lance_4.png"),
            //IronsSpellbooks.id("textures/entity/lightning_lance/lightning_lance_5.png"),
            //IronsSpellbooks.id("textures/entity/lightning_lance/lightning_lance_6.png"),
            //IronsSpellbooks.id("textures/entity/lightning_lance/lightning_lance_7.png")
    };

    public HolyLanceRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(HolyLanceProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * (double)(180F / (float)Math.PI)) - 90.0F);
        float yRot = -((float)(Mth.atan2(motion.z, motion.x) * (double)(180F / (float)Math.PI)) + 90.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        renderModel(poseStack, bufferSource, entity.getAge());
        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    public static void renderModel(PoseStack poseStack, MultiBufferSource bufferSource, int animOffset) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.energySwirl(getTextureLocation(animOffset), 0.0F, 0.0F));
        float halfWidth = 2.0F;
        float halfHeight = 1.0F;
        float angleCorrection = 55.0F;
        poseStack.mulPose(Axis.XP.rotationDegrees(angleCorrection));
        consumer.vertex(poseMatrix, 0.0F, -halfWidth, -halfHeight).color(255, 255, 255, 255).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(poseMatrix, 0.0F, halfWidth, -halfHeight).color(255, 255, 255, 255).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(poseMatrix, 0.0F, halfWidth, halfHeight).color(255, 255, 255, 255).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(poseMatrix, 0.0F, -halfWidth, halfHeight).color(255, 255, 255, 255).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
        poseStack.mulPose(Axis.XP.rotationDegrees(-angleCorrection));
        poseStack.mulPose(Axis.YP.rotationDegrees(-angleCorrection));
        consumer.vertex(poseMatrix, -halfWidth, 0.0F, -halfHeight).color(255, 255, 255, 255).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(poseMatrix, halfWidth, 0.0F, -halfHeight).color(255, 255, 255, 255).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(poseMatrix, halfWidth, 0.0F, halfHeight).color(255, 255, 255, 255).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(poseMatrix, -halfWidth, 0.0F, halfHeight).color(255, 255, 255, 255).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
        poseStack.mulPose(Axis.YP.rotationDegrees(angleCorrection));
    }

    public ResourceLocation getTextureLocation(HolyLanceProjectile entity) {
        return getTextureLocation(entity.getAge());
    }

    public static ResourceLocation getTextureLocation(int offset) {
        float ticksPerFrame = 1.0F;
        return TEXTURES[(int)((float)offset / ticksPerFrame) % TEXTURES.length];
    }
}
