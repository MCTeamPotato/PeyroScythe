package com.rinko1231.peyroscythe.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.spells.blood.RayOfSiphoningSpell;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MySpellRenderingHelper {


    public static final ResourceLocation BEACON = IronsSpellbooks.id("textures/entity/ray/beacon_beam.png");
    public static final ResourceLocation TWISTING_GLOW = IronsSpellbooks.id("textures/entity/ray/twisting_glow.png");

        public MySpellRenderingHelper() {
        }

        public static void renderSpellHelper(SyncedSpellData spellData, LivingEntity castingMob, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
            if (((AbstractSpell) NewSpellRegistry.NERO_HOLY_RAY.get()).getSpellId().equals(spellData.getCastingSpellId())) {
                renderNeroHolyRay(castingMob, poseStack, bufferSource, partialTicks);
            }

        }


        public static void renderNeroHolyRay(LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
            poseStack.pushPose();
            poseStack.translate(0.0F, entity.getEyeHeight() * 0.8F, 0.0F);
            PoseStack.Pose pose = poseStack.last();

            Vec3 startWorld = entity.getEyePosition(partialTicks);
            Vec3 look = entity.getLookAngle().normalize();
            double maxDist = RayOfSiphoningSpell.getRange(0);
            Vec3 endWorld = startWorld.add(look.scale(maxDist));

            // 先检查方块
            BlockHitResult blockHit = entity.level().clip(
                    new ClipContext(startWorld, endWorld, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
            if (blockHit.getType() != HitResult.Type.MISS) {
                endWorld = blockHit.getLocation();
            }

            // 获取所有实体命中
            List<EntityHitResult> hits = MyUtils.raycastAllEntities(entity.level(), entity,
                    (float)maxDist, true, 0.15F);

            // 找到最远命中点
            for (EntityHitResult ehr : hits) {
                if (ehr.getType() == HitResult.Type.ENTITY) {
                    Vec3 loc = ehr.getLocation();
                    if (loc.distanceToSqr(startWorld) > startWorld.distanceToSqr(endWorld)) {
                        // 如果实体在方块前，就更新终点
                        endWorld = loc;
                    }
                }
            }

            // 换算到局部坐标
            Vec3 start = Vec3.ZERO;
            Vec3 impact = endWorld.subtract(startWorld);
            float distance = (float)entity.getEyePosition().distanceTo(impact);
            float radius = 0.12F;
            int r = 178;
            int g = 0;
            int b = 0;
            int a = 255;
            float deltaTicks = (float)entity.tickCount + partialTicks;
            float deltaUV = -deltaTicks % 10.0F;
            float max = Mth.frac(deltaUV * 0.2F - (float)Mth.floor(deltaUV * 0.1F));
            float min = -1.0F + max;
            Vec3 dir = entity.getLookAngle().normalize();
            float dx = (float)dir.x;
            float dz = (float)dir.z;
            float yRot = (float)Mth.atan2((double)dz, (double)dx) - 1.5707F;
            float dxz = Mth.sqrt(dx * dx + dz * dz);
            float dy = (float)dir.y;
            float xRot = (float)Mth.atan2((double)dy, (double)dxz);
            poseStack.mulPose(Axis.YP.rotation(-yRot));
            poseStack.mulPose(Axis.XP.rotation(-xRot));

            for(float j = 1.0F; j <= distance; j += 0.5F) {
                Vec3 wiggle = new Vec3((double)(Mth.sin(deltaTicks * 0.8F) * 0.02F), (double)(Mth.sin(deltaTicks * 0.8F + 100.0F) * 0.02F), (double)(Mth.cos(deltaTicks * 0.8F) * 0.02F));
                Vec3 end = (new Vec3((double)0.0F, (double)0.0F, (double)Math.min(j, distance))).add(wiggle);
                VertexConsumer inner = bufferSource.getBuffer(RenderType.entityTranslucent(BEACON, true));
                drawHull(start, end, radius, radius, pose, inner, 255, 250, 240, a, min, max);
                VertexConsumer outer = bufferSource.getBuffer(RenderType.entityTranslucent(TWISTING_GLOW));
                drawQuad(start, end, radius * 4.0F, 0.0F, pose, outer, 255, 215, 0, 200, min, max);
                drawQuad(start, end, 0.0F, radius * 4.0F, pose, outer, 255, 215, 0, 200, min, max);
                start = end;
            }

            poseStack.popPose();
        }

        private static void drawHull(Vec3 from, Vec3 to, float width, float height, PoseStack.Pose pose, VertexConsumer consumer, int r, int g, int b, int a, float uvMin, float uvMax) {
            drawQuad(from.subtract((double)0.0F, (double)(height * 0.5F), (double)0.0F), to.subtract((double)0.0F, (double)(height * 0.5F), (double)0.0F), width, 0.0F, pose, consumer, r, g, b, a, uvMin, uvMax);
            drawQuad(from.add((double)0.0F, (double)(height * 0.5F), (double)0.0F), to.add((double)0.0F, (double)(height * 0.5F), (double)0.0F), width, 0.0F, pose, consumer, r, g, b, a, uvMin, uvMax);
            drawQuad(from.subtract((double)(width * 0.5F), (double)0.0F, (double)0.0F), to.subtract((double)(width * 0.5F), (double)0.0F, (double)0.0F), 0.0F, height, pose, consumer, r, g, b, a, uvMin, uvMax);
            drawQuad(from.add((double)(width * 0.5F), (double)0.0F, (double)0.0F), to.add((double)(width * 0.5F), (double)0.0F, (double)0.0F), 0.0F, height, pose, consumer, r, g, b, a, uvMin, uvMax);
        }

        private static void drawQuad(Vec3 from, Vec3 to, float width, float height, PoseStack.Pose pose, VertexConsumer consumer, int r, int g, int b, int a, float uvMin, float uvMax) {
            Matrix4f poseMatrix = pose.pose();
            Matrix3f normalMatrix = pose.normal();
            float halfWidth = width * 0.5F;
            float halfHeight = height * 0.5F;
            consumer.vertex(poseMatrix, (float)from.x - halfWidth, (float)from.y - halfHeight, (float)from.z).color(r, g, b, a).uv(0.0F, uvMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(poseMatrix, (float)from.x + halfWidth, (float)from.y + halfHeight, (float)from.z).color(r, g, b, a).uv(1.0F, uvMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(poseMatrix, (float)to.x + halfWidth, (float)to.y + halfHeight, (float)to.z).color(r, g, b, a).uv(1.0F, uvMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(poseMatrix, (float)to.x - halfWidth, (float)to.y - halfHeight, (float)to.z).color(r, g, b, a).uv(0.0F, uvMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0.0F, 1.0F, 0.0F).endVertex();
        }
    }
