package com.rinko1231.peyroscythe.renderer.ice;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.spellentity.ice.IceTombEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;


public class IceTombModel extends EntityModel<IceTombEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(PeyroScythe.id("ice_tomb"), "main");

    private final ModelPart model;

    public IceTombModel(ModelPart root) {
        this.model = root.getChild("model");
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("model",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-8.0F, -36.0F, -8.0F, 16.0F, 36.0F, 16.0F, new CubeDeformation(0.0F))
                        .texOffs(40, 67).addBox(4.0F, -9.0F, 4.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 52).addBox(1.0F, -24.0F, -11.0F, 10.0F, 24.0F, 10.0F, new CubeDeformation(0.0F))
                        .texOffs(64, 0).addBox(-8.0F, -36.0F, -8.0F, 16.0F, 36.0F, 16.0F, new CubeDeformation(-0.01F))
                        .texOffs(40, 52).addBox(-10.0F, -9.0F, -10.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 86).addBox(-11.0F, -24.0F, 1.0F, 10.0F, 24.0F, 10.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(IceTombEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        // 静态模型，无需动画
    }



    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vc, int packedLight, int packedOverlay, float p_103115_, float p_103116_, float p_103117_, float p_103118_) {
        model.render(poseStack, vc, packedLight, packedOverlay);
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer vc, int packedLight, int noOverlay, int i) {
        model.render(poseStack, vc, packedLight, noOverlay);
    }
}
