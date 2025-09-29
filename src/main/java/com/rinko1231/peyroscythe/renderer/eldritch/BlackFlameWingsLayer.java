package com.rinko1231.peyroscythe.renderer.eldritch;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rinko1231.peyroscythe.PeyroScythe;

import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlackFlameWingsLayer <T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation WINGS_LOCATION = PeyroScythe.id( "textures/entity/black_flame_wings.png");
    private final BlackFlameWingsModel<T> blackFlameWingsModel;

    public BlackFlameWingsLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
        this.blackFlameWingsModel = new BlackFlameWingsModel(Minecraft.getInstance().getEntityModels().bakeLayer(BlackFlameWingsModel.BLACK_FLAME_WINGS_LAYER));
    }

    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (!this.shouldRender(pLivingEntity)) return;

        ResourceLocation wingsTexture = this.getBlackFlameWingsTexture(pLivingEntity);

        pMatrixStack.pushPose();
        pMatrixStack.translate(0.0F, 0.0F, 0.125F);
        this.getParentModel().copyPropertiesTo(this.blackFlameWingsModel);
        this.blackFlameWingsModel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);

        // 用纯黑翅膀纹理渲染
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(wingsTexture));
        this.blackFlameWingsModel.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY);
        pMatrixStack.popPose();
    }


    public boolean shouldRender(T entity) {
        return !entity.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA) && entity.hasEffect(MobEffectRegistry.BLACK_FLAME_WINGS);
    }

    public ResourceLocation getBlackFlameWingsTexture(T entity) {
        return WINGS_LOCATION;
    }
}
