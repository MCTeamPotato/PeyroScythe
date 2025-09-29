package com.rinko1231.peyroscythe.particle;

import io.redspace.ironsspellbooks.particle.DragonFireParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


public class BlackFlameLongParticle extends TextureSheetParticle {
        private final SpriteSet sprites;

        public BlackFlameLongParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
            super(level, xCoord, yCoord, zCoord, xd, yd, zd);
            this.xd = xd;
            this.yd = yd;
            this.zd = zd;
            this.scale(this.random.nextFloat() * 1.75F + 1.0F);
            this.lifetime = 5 + (int)(Math.random() * (double)20.0F);
            this.sprites = spriteSet;
            this.setSpriteFromAge(spriteSet);
            this.gravity = -0.015F;
        }

        public void tick() {
            super.tick();
            this.xd += (double)(this.random.nextFloat() / 500.0F * (float)(this.random.nextBoolean() ? 1 : -1));
            this.yd += (double)(this.random.nextFloat() / 100.0F);
            this.zd += (double)(this.random.nextFloat() / 500.0F * (float)(this.random.nextBoolean() ? 1 : -1));
            this.setSpriteFromAge(this.sprites);
            if ((float)this.age > (float)this.lifetime * 0.5F && this.random.nextFloat() <= 0.35F) {
                this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, this.xd, this.yd, this.zd);
            }

        }

        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
        }

        public int getLightColor(float p_107564_) {
            return 240;
        }

        @OnlyIn(Dist.CLIENT)
        public static class Provider implements ParticleProvider<SimpleParticleType> {
            private final SpriteSet sprites;

            public Provider(SpriteSet spriteSet) {
                this.sprites = spriteSet;
            }

            public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
                return new BlackFlameLongParticle(level, x, y, z, this.sprites, dx, dy, dz);
            }
        }
    }
