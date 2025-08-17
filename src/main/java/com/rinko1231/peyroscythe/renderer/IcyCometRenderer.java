package com.rinko1231.peyroscythe.renderer;

import com.rinko1231.peyroscythe.PeyroScythe;
import io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;

public class IcyCometRenderer extends FireballRenderer {
    private static final ResourceLocation BASE_TEXTURE = new ResourceLocation("minecraft","textures/block/snow.png");

    private static final ResourceLocation[] FIRE_TEXTURES = new ResourceLocation[]{
            PeyroScythe.id("textures/entity/ice_comet/no_trail_1.png"),
            PeyroScythe.id("textures/entity/ice_comet/no_trail_2.png")};

    public IcyCometRenderer(EntityRendererProvider.Context context, float scale) {
        super(context, scale);
    }

    public ResourceLocation getTextureLocation(Projectile entity) {
        return BASE_TEXTURE;
    }

    public ResourceLocation getFireTextureLocation(Projectile entity) {
        int frame = entity.tickCount / 2 % FIRE_TEXTURES.length;
        return FIRE_TEXTURES[frame];
    }


}
