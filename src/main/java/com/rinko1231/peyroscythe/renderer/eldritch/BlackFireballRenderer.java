package com.rinko1231.peyroscythe.renderer.eldritch;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rinko1231.peyroscythe.PeyroScythe;
import net.minecraft.client.model.geom.ModelLayerLocation;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public class BlackFireballRenderer  extends EntityRenderer<Projectile> {
        public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
                PeyroScythe.id("black_fireball_model"), "main");
        private static final ResourceLocation BASE_TEXTURE =
                PeyroScythe.id("textures/entity/black_fireball/ash_core.png");
        private static final ResourceLocation[] FIRE_TEXTURES = new ResourceLocation[]{
                PeyroScythe.id("textures/entity/black_fireball/black_fire_0.png"),
                PeyroScythe.id("textures/entity/black_fireball/black_fire_1.png"),
                PeyroScythe.id("textures/entity/black_fireball/black_fire_2.png"),
                PeyroScythe.id("textures/entity/black_fireball/black_fire_3.png"),
                PeyroScythe.id("textures/entity/black_fireball/black_fire_4.png"),
                PeyroScythe.id("textures/entity/black_fireball/black_fire_5.png"),
                PeyroScythe.id("textures/entity/black_fireball/black_fire_6.png"),
                PeyroScythe.id("textures/entity/black_fireball/black_fire_7.png")};
        protected final ModelPart body;
        protected final ModelPart outline;
        protected final float scale;

    public BlackFireballRenderer(EntityRendererProvider.Context context, float scale) {
        super(context);
        ModelPart modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.body = modelpart.getChild("body");
        this.outline = modelpart.getChild("outline");
        this.scale = scale;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("outline", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 16.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 48, 24);
    }

    public void render(Projectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate((double)0.0F, entity.getBoundingBox().getYsize() * (double)0.5F, (double)0.0F);
        poseStack.scale(this.scale, this.scale, this.scale);
        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * (double)(180F / (float)Math.PI)) - 90.0F);
        float yRot = -((float)(Mth.atan2(motion.z, motion.x) * (double)(180F / (float)Math.PI)) + 90.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
        this.body.render(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY);
        float f = (float)entity.tickCount + partialTicks;
        consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.getFireTextureLocation(entity)));
        poseStack.scale(1.15F, 1.15F, 1.15F);
        this.outline.render(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    public ResourceLocation getTextureLocation(Projectile entity) {
        return BASE_TEXTURE;
    }

    public ResourceLocation getFireTextureLocation(Projectile entity) {
        int frame = entity.tickCount % FIRE_TEXTURES.length;
        return FIRE_TEXTURES[frame];
    }
}
