package com.rinko1231.peyroscythe.renderer.holy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;


import com.rinko1231.peyroscythe.block.MundusBlockEntity;
import com.rinko1231.peyroscythe.utils.mundus.MundusRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;



import java.util.*;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

/**
 * 发金光的“球状”Mundus 特效渲染器
 * 用法：
 *   event.registerBlockEntityRenderer(ModBlocks.MUNDUS_BE.get(), MundusBlockRenderer::new);
 * 并在 LevelRenderer 合适的时机调用 renderEntireBatch（可仿照 Ambersol 的调用点）
 */
public class MundusBlockRenderer<T extends MundusBlockEntity> implements BlockEntityRenderer<T> {

    private static final Map<BlockPos, MundusBlockEntity> ALL_ON_SCREEN = new HashMap<>();
    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);

    // —— 金色光晕颜色 —— //
    private static final int SHINE_R = 255;
    private static final int SHINE_G = 212;
    private static final int SHINE_B = 96;

    private static final int SHINE_CENTER_R = 255;
    private static final int SHINE_CENTER_G = 244;
    private static final int SHINE_CENTER_B = 210;

    public MundusBlockRenderer(BlockEntityRendererProvider.Context ctx) {}

    /** 与 Ambersol 同风格：收集后按距离排序批渲染，避免半透明乱序 */
    public static void renderEntireBatch(LevelRenderer levelRenderer, PoseStack poseStack, int renderTick, Camera camera, float partialTick) {
        if (!ALL_ON_SCREEN.isEmpty()) {
            List<BlockPos> sorted = new ArrayList<>(ALL_ON_SCREEN.keySet());
            sorted.sort((a, b) -> {
                double d1 = camera.getPosition().distanceTo(Vec3.atCenterOf(a));
                double d2 = camera.getPosition().distanceTo(Vec3.atCenterOf(b));
                return Double.compare(d2, d1); // 远到近
            });

            poseStack.pushPose();
            Vec3 cam = camera.getPosition();
            poseStack.translate(-cam.x, -cam.y, -cam.z);

            MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            for (BlockPos pos : sorted) {
                Vec3 at = Vec3.atCenterOf(pos);
                poseStack.pushPose();
                poseStack.translate(at.x, at.y, at.z);
                renderAt(ALL_ON_SCREEN.get(pos), partialTick, poseStack, buffers);
                poseStack.popPose();
            }
            poseStack.popPose();
        }
        ALL_ON_SCREEN.clear();
    }

    @Override
    public void render(T mundus, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!mundus.isRemoved()) {
            ALL_ON_SCREEN.put(mundus.getBlockPos(), mundus);
        } else {
            ALL_ON_SCREEN.remove(mundus.getBlockPos());
        }
    }

    private static void renderAt(MundusBlockEntity mundus, float partialTicks, PoseStack poseStack, MultiBufferSource buffers) {
        float time = 0F;
        float scale = 1F;

        if (Minecraft.getInstance().getCameraEntity() != null) {
            time = Minecraft.getInstance().getCameraEntity().tickCount + partialTicks;

            // —— 规模：基础 0.6 + 等级加成 + 轻微呼吸脉动 —— //
            int lvl = 3;
            float base = 0.6F + 0.12F * lvl;
            float pulse = 0.06F * (float)Math.sin(time * 0.15F);
            scale = base + pulse;

            // 也可以按距离淡出（可选）：
            // double dist = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceTo(Vec3.atCenterOf(mundus.getBlockPos()));
            // float fade = (float)Math.max(0.0, 1.0 - dist / 64.0);
            // scale *= fade;
        }

        if (scale <= 0.0F) return;

        // billboard：始终朝向摄像机
        Quaternionf cameraQ = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
        poseStack.pushPose();
        poseStack.mulPose(cameraQ);

        // 旋转/闪烁参数
        float timeRot = time * 0.4F;     // 整体旋转
        float timeFlicker = time * 0.1F; // 宽度/长度的闪烁

        VertexConsumer vc = buffers.getBuffer(MundusRenderTypes.getMundusShine());

        // 光芒数量：随等级增加
        int lights = 6;
        poseStack.mulPose(Axis.ZN.rotationDegrees(0));

        for (int i = 0; i < lights; i++) {
            float length = (3F + (float)Math.sin(timeFlicker + i * 2)) * scale;
            float width  = (1F - 0.2F * Math.abs((float)Math.cos(timeFlicker - i * Math.PI * 0.5F))) * scale;

            poseStack.pushPose();
            poseStack.mulPose(Axis.ZN.rotationDegrees(timeRot - (i / (float) lights * 360F)));

            PoseStack.Pose last = poseStack.last();
            Matrix4f m4 = last.pose();
            Matrix3f m3 = last.normal();

            // 三角扇：中心点 + 两个角点 + 再补左角闭合
            shineOriginVertex(vc, m4, m3);
            shineLeftCornerVertex(vc, m4, m3, length, width);
            shineRightCornerVertex(vc, m4, m3, length, width);
            shineLeftCornerVertex(vc, m4, m3, length, width);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private static void shineOriginVertex(VertexConsumer v, Matrix4f m4, Matrix3f m3) {
        v.vertex(m4, 0.0F, 0.0F, 0.0F)
                .color(SHINE_CENTER_R, SHINE_CENTER_G, SHINE_CENTER_B, 230)
                .uv(0.5F, 0.0F)
                .overlayCoords(NO_OVERLAY)
                .uv2(240)
                .normal(m3, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private static void shineLeftCornerVertex(VertexConsumer v, Matrix4f m4, Matrix3f m3, float length, float width) {
        v.vertex(m4, -HALF_SQRT_3 * width, length, 0.0F)
                .color(SHINE_R, SHINE_G, SHINE_B, 0)
                .uv(0.0F, 1.0F)
                .overlayCoords(NO_OVERLAY)
                .uv2(240)
                .normal(m3, 0.0F, -1.0F, 0.0F)
                .endVertex();
    }

    private static void shineRightCornerVertex(VertexConsumer v, Matrix4f m4, Matrix3f m3, float length, float width) {
        v.vertex(m4, HALF_SQRT_3 * width, length, 0.0F)
                .color(SHINE_R, SHINE_G, SHINE_B, 0)
                .uv(1.0F, 1.0F)
                .overlayCoords(NO_OVERLAY)
                .uv2(240)
                .normal(m3, 0.0F, -1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
