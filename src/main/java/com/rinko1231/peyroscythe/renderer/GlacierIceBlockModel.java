package com.rinko1231.peyroscythe.renderer;

import com.rinko1231.peyroscythe.spell.GlacierIceBlockProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

// GlacierIceBlockModel.java
public class GlacierIceBlockModel extends GeoModel<GlacierIceBlockProjectile> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("irons_spellbooks", "textures/entity/ice_block.png");
    private static final ResourceLocation MODEL =
            new ResourceLocation("irons_spellbooks", "geo/ice_block_projectile.geo.json");
    private static final ResourceLocation ANIMS =
            new ResourceLocation("irons_spellbooks", "animations/ice_block_animations.json");

    @Override public ResourceLocation getTextureResource(GlacierIceBlockProjectile animatable) { return TEXTURE; }
    @Override public ResourceLocation getModelResource(GlacierIceBlockProjectile animatable)   { return MODEL; }
    @Override public ResourceLocation getAnimationResource(GlacierIceBlockProjectile animatable){ return ANIMS; }
}
