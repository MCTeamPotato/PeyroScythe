package com.rinko1231.peyroscythe.particle;

import com.rinko1231.peyroscythe.init.PeyroParticleRegistry;
import io.redspace.ironsspellbooks.particle.FireParticle;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


public class BlackFlameParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public BlackFlameParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.scale(this.random.nextFloat() * 1.75F + 1.0F);
        this.friction = (float)((double)this.friction - (double)this.random.nextFloat() * 0.1);
        this.lifetime = 10 + (int)(Math.random() * (double)25.0F);
        this.sprites = spriteSet;
        this.setSpriteFromAge(spriteSet);
        this.gravity = -0.01F;
    }

    public void tick() {
        super.tick();
        this.xd += (double)(this.random.nextFloat() / 500.0F * (float)(this.random.nextBoolean() ? 1 : -1));
        this.zd += (double)(this.random.nextFloat() / 500.0F * (float)(this.random.nextBoolean() ? 1 : -1));
        this.animateContinuously();
        if (this.random.nextFloat() <= 0.25F) {
            this.level.addParticle(PeyroParticleRegistry.BLACK_EMBERS.get(), this.x, this.y, this.z, this.xd, this.yd, this.zd);
        }

    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public int getLightColor(float p_107564_) {
        return 15728880;
    }

    private void animateContinuously() {
        if (this.age % 8 == 0) {
            this.setSprite(this.sprites.get(this.random));
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new BlackFlameParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
