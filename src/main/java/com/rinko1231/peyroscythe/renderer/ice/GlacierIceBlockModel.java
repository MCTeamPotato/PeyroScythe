package com.rinko1231.peyroscythe.renderer.ice;

import com.rinko1231.peyroscythe.spellentity.ice.GlacierIceBlockProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GlacierIceBlockModel extends GeoModel<GlacierIceBlockProjectile> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/ice_block.png");
    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/ice_block_projectile.geo.json");
    private static final ResourceLocation ANIMS =
            ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/ice_block_animations.json");

    @Override public ResourceLocation getTextureResource(GlacierIceBlockProjectile animatable) { return TEXTURE; }

    @Override public ResourceLocation getModelResource(GlacierIceBlockProjectile animatable)   { return MODEL; }
    @Override public ResourceLocation getAnimationResource(GlacierIceBlockProjectile animatable){ return ANIMS; }
}
