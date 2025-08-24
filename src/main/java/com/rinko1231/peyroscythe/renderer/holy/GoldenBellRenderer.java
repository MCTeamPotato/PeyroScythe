package com.rinko1231.peyroscythe.renderer.holy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.spellentity.holy.GoldenBellEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoldenBellRenderer extends EntityRenderer<GoldenBellEntity> {

    public static final Material BELL_MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/bell/bell_body"));

    public static final ModelLayerLocation MODEL_LAYER_LOCATION =
            new ModelLayerLocation(PeyroScythe.id("golden_bell"), "main");
    private static final String BELL_BODY = "bell_body";
    private final ModelPart bellBody;

    // 动画参数
    private static final int PERIOD_TICKS = 30;        // 1.5s 一次
    private static final int ACTIVE_WINDOW = 10;       // 摇动时长 10 tick，其余 20 tick 静止
    private static final float MAX_SWING = 0.35f;      // 最大摆幅（弧度），可微调

    public GoldenBellRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);

        ModelPart root = ctx.bakeLayer(ModelLayers.BELL);
        this.bellBody = root.getChild(BELL_BODY);
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition bell = root.addOrReplaceChild("bell_body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F),
                PartPose.offset(8.0F, 12.0F, 8.0F));
        bell.addOrReplaceChild("bell_base",
                CubeListBuilder.create().texOffs(0, 13).addBox(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F),
                PartPose.offset(-8.0F, -12.0F, -8.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void render(GoldenBellEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        //缩放
        float entityScale = entity.getBbWidth() * 0.5f;
        float scale = 0.55f * entityScale;
        poseStack.scale(scale, scale, scale);


        poseStack.translate(-0.5F, (entity.getBbHeight() / (scale*2)), -0.5F);

        //动画
        float t = (entity.tickCount + partialTicks) % PERIOD_TICKS;
        float swingX = 0f;
        float swingZ = 0f;

        if (t < ACTIVE_WINDOW) {

            float phase = t / ACTIVE_WINDOW; // [0,1)
            float envelope = (float) Math.sin(Math.PI * phase); // 0→1→0
            float decay = 1.0f - 0.35f * phase; // 轻微衰减（可选）
            float amp = MAX_SWING * envelope * decay;

            //沿 X 轴的单向摆动
            swingX = amp;
        }

        this.bellBody.xRot = swingX;
        this.bellBody.zRot = swingZ;

        //半透明
        VertexConsumer vc = BELL_MATERIAL.buffer(buffer, RenderType::entityTranslucent);

        // 如果你所用的映射版本支持带颜色的重载：
        // ModelPart#render(PoseStack, VertexConsumer, int, int, float r, float g, float b, float a)
        float alpha = 0.6f;
        float r = 1.0f, g = 1.0f, b = 1.0f;
        this.bellBody.render(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, alpha);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }


    @Override
    public ResourceLocation getTextureLocation(GoldenBellEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
