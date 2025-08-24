package com.rinko1231.peyroscythe.renderer.holy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.init.BlockRegistry;
import com.rinko1231.peyroscythe.spellentity.holy.MundusEntity;
import com.rinko1231.peyroscythe.utils.mundus.MundusRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix4f;




public class MundusRenderer extends EntityRenderer<MundusEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION =
            new ModelLayerLocation(PeyroScythe.id( "mundus_rune_ball_model"), "main");
    private static final float HALF_SQRT_3 = (float)(Math.sqrt((double)3.0F) / (double)2.0F);

    private static final ResourceLocation CENTER_TEXTURE = PeyroScythe.id("textures/entity/mundus/mundus.png");
    private static final ResourceLocation BEAM_TEXTURE   = PeyroScythe.id("textures/entity/mundus/mundus_beam.png");
    private static final ResourceLocation[] SWIRL_TEXTURES = new ResourceLocation[]{
            PeyroScythe.id("textures/entity/mundus/mundus_0.png"),
            PeyroScythe.id("textures/entity/mundus/mundus_1.png"),
            PeyroScythe.id("textures/entity/mundus/mundus_2.png"),
            PeyroScythe.id("textures/entity/mundus/mundus_3.png"),
            PeyroScythe.id("textures/entity/mundus/mundus_4.png")
    };

    private final ModelPart orb;

    public MundusRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        ModelPart root = ctx.bakeLayer(MODEL_LAYER_LOCATION);
        this.orb = root.getChild("orb");
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("orb",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4, -4, -4, 8, 8, 8),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 8, 8);
    }

    @Override
    public void render(MundusEntity entity, float yaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        //移到中心
        poseStack.translate(0.0, entity.getBoundingBox().getYsize() * 0.5, 0.0);
        {
            poseStack.pushPose();

            float s = entity.getBbWidth() * 0.5F;
            poseStack.scale(s*0.5f, s*0.5f, s*0.5f);


            // ✅ 绕 Y 轴旋转
            float rotation = (entity.tickCount + partialTicks) * 18.0F; // 每 tick 旋转 2 度，可调
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

            // ✅ 把方块对齐到实体中心
            poseStack.translate(-0.5, -0.5, -0.5);

            Minecraft mc = Minecraft.getInstance();
            BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
            BlockState state = BlockRegistry.MUNDUS_BLOCK.get().defaultBlockState();

            dispatcher.renderSingleBlock(
                    state,
                    poseStack,
                    buffer,
                    packedLight,
                    OverlayTexture.NO_OVERLAY
            );

            poseStack.popPose();
        }

        // 光晕扇形光芒（替换原本能量喷射）
        {
            poseStack.pushPose();

            float t = entity.tickCount + partialTicks;
            float scale = entity.getBbWidth() * 0.35F;

            // billboard：让光芒永远朝向相机
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

            VertexConsumer vc = buffer.getBuffer(MundusRenderTypes.getMundusShine());

            // 光芒数量
            int rays = 6;
            for (int i = 0; i < rays; i++) {
                poseStack.pushPose();

                // 每条光芒的旋转角度（随时间自转）
                float angle = (t * 0.5F + (360F / rays) * i);
                poseStack.mulPose(Axis.ZP.rotationDegrees(angle));

                float length = (3F + (float)Math.sin(t * 0.1F + i)) * scale;
                float width  = (0.8F - 0.2F * (float)Math.cos(t * 0.13F + i)) * scale;

                PoseStack.Pose last = poseStack.last();
                Matrix4f m4 = last.pose();
                Matrix3f m3 = last.normal();

                // 中心点（金白色，高 alpha）
                vc.vertex(m4, 0F, 0F, 0F)
                        .color(255, 240, 180, 220)
                        .uv(0.5F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880)
                        .normal(m3, 0F, 1F, 0F).endVertex();

                // 左角点（渐隐）
                vc.vertex(m4, -HALF_SQRT_3 * width, length, 0F)
                        .color(255, 210, 100, 0)
                        .uv(0F, 1F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880)
                        .normal(m3, 0F, -1F, 0F).endVertex();

                // 右角点（渐隐）
                vc.vertex(m4, HALF_SQRT_3 * width, length, 0F)
                        .color(255, 210, 100, 0)
                        .uv(1F, 1F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880)
                        .normal(m3, 0F, -1F, 0F).endVertex();

                // 再补一个左角闭合
                vc.vertex(m4, -HALF_SQRT_3 * width, length, 0F)
                        .color(255, 210, 100, 0)
                        .uv(0F, 1F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880)
                        .normal(m3, 0F, -1F, 0F).endVertex();

                poseStack.popPose();
            }

            poseStack.popPose();
        }

        {
            poseStack.pushPose();

            float entityScale = entity.getBbWidth() * 0.018F;
            poseStack.scale(0.5F * entityScale, 0.5F * entityScale, 0.5F * entityScale);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            poseStack.translate(5.0F, 0.0F, 0.0F);

            // 环尺寸
            float t = (entity.tickCount + partialTicks) * 0.1F;


            poseStack.popPose();
        }

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(MundusEntity entity) {
        return CENTER_TEXTURE;
    }

    private ResourceLocation getSwirlTexture(MundusEntity e, int offset) {
        int frame = (e.tickCount + offset) % SWIRL_TEXTURES.length;
        return SWIRL_TEXTURES[frame];
    }

}

//旋转符文壳
//太丑了，不要了.jpg
        /*
        {
            float entityScale = entity.getBbWidth() * 0.200F;
            poseStack.pushPose();
            poseStack.scale(0.5F * entityScale, 0.5F * entityScale, 0.5F * entityScale);

            float t = entity.tickCount + partialTicks;


            final float base0 = 2.0F, base1 = 1.8F, base2 = 1.6F;
            final float breathe = 0.07F * Mth.sin(t * 0.15F);

            float[] layers = new float[]{base0 + breathe, base1 - breathe * 0.5F, base2 - breathe};

            for (int i = 0; i < 3; i++) {
                poseStack.pushPose();


                float r = Mth.clamp(0.25F * (1 + i), 0.0F, 1.0F);
                float g = Mth.clamp(0.80F * (1 + i), 0.0F, 1.0F);
                float b = Mth.clamp(1.00F * (1 + i), 0.0F, 1.0F);

                float f = t + i * 777;
                float swirlX = Mth.cos(0.065F * f) * 180.0F;
                float swirlY = Mth.sin(0.065F * f) * 180.0F;
                float swirlZ = Mth.cos(0.065F * f + 5464.0F) * 180.0F;

                poseStack.mulPose(Axis.XP.rotationDegrees(swirlX * (i % 2 == 0 ? 1 : -1)));
                poseStack.mulPose(Axis.YP.rotationDegrees(swirlY * (i % 2 == 0 ? 1 : -1)));
                poseStack.mulPose(Axis.ZP.rotationDegrees(swirlZ * (i % 2 == 0 ? 1 : -1)));

                float scale = layers[i];
                poseStack.scale(scale, scale, scale);

                ResourceLocation tex = getSwirlTexture(entity, i * i);
                VertexConsumer shell = buffer.getBuffer(MagicArrowRenderer.CustomRenderType.magic(tex));
                this.orb.render(poseStack, shell, 15728880, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);

                poseStack.popPose();
            }

            poseStack.popPose();
        }
*/

