package com.rinko1231.peyroscythe.network.spell;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;

import java.util.function.Supplier;

import static com.rinko1231.peyroscythe.init.PeyroParticleRegistry.*;

public class ClientboundBlackFireballExplosionParticles {
        private final Vec3 pos1;
        private final float radius;

        public ClientboundBlackFireballExplosionParticles(Vec3 pos1, float radius) {
            this.pos1 = pos1;
            this.radius = radius;
        }

        public ClientboundBlackFireballExplosionParticles(FriendlyByteBuf buf) {
            this.pos1 = this.readVec3(buf);
            this.radius = buf.readFloat();
        }

        public void toBytes(FriendlyByteBuf buf) {
            this.writeVec3(this.pos1, buf);
            buf.writeFloat(this.radius);
        }

        public Vec3 readVec3(FriendlyByteBuf buf) {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            return new Vec3(x, y, z);
        }

        public void writeVec3(Vec3 vec3, FriendlyByteBuf buf) {
            buf.writeDouble(vec3.x);
            buf.writeDouble(vec3.y);
            buf.writeDouble(vec3.z);
        }

        public boolean handle(Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context ctx = (NetworkEvent.Context)supplier.get();
            ctx.enqueueWork(() -> this.handleClientboundFieryExplosion(this.pos1, this.radius));
            return true;
        }


    public void handleClientboundFieryExplosion(Vec3 pos, float radius) {
        MinecraftInstanceHelper.ifPlayerPresent((player) -> {
            Level level = player.level();
            double x = pos.x;
            double y = pos.y;
            double z = pos.z;
            level.addParticle(new BlastwaveParticleOptions(new Vector3f(1.0F, 0.6F, 0.3F), radius + 1.0F), x, y, z, (double)0.0F, (double)0.0F, (double)0.0F);
            int c = (int)(6.28 * (double)radius) * 3;
            float step = 360.0F / (float)c * ((float)Math.PI / 180F);
            float speed = 0.06F + 0.01F * radius;

            for(int i = 0; i < c; ++i) {
                Vec3 vec3 = (new Vec3((double) Mth.cos(step * (float)i), (double)0.0F, (double)Mth.sin(step * (float)i))).scale((double)speed);
                Vec3 posOffset = Utils.getRandomVec3((double)0.5F).add(vec3.scale((double)10.0F));
                vec3 = vec3.add(Utils.getRandomVec3(0.01));
                level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x + posOffset.x, y + posOffset.y, z + posOffset.z, vec3.x, vec3.y, vec3.z);
            }

            int cloudDensity = 50 + (int)(25.0F * radius);

            for(int i = 0; i < cloudDensity; ++i) {
                Vec3 posOffset = Utils.getRandomVec3((double)1.0F).scale((double)(radius * 0.4F));
                Vec3 motion = posOffset.normalize().scale((double)(speed * 0.5F));
                posOffset = posOffset.add(motion.scale(Utils.getRandomScaled((double)1.0F)));
                motion = motion.add(Utils.getRandomVec3(0.02));
                level.addParticle(ParticleTypes.LARGE_SMOKE, x + posOffset.x, y + posOffset.y, z + posOffset.z, motion.x, motion.y, motion.z);
            }

            for(int i = 0; i < cloudDensity; i += 2) {
                Vec3 posOffset = Utils.getRandomVec3((double)1.0F).scale((double)(radius * 0.4F));
                Vec3 motion = posOffset.normalize().scale((double)(speed * 0.5F));
                motion = motion.add(Utils.getRandomVec3((double)0.25F));
                level.addParticle(BLACK_EMBERS.get(), true, x + posOffset.x, y + posOffset.y, z + posOffset.z, motion.x, motion.y, motion.z);
                level.addParticle(BLACK_FLAME.get(), x + posOffset.x * (double)0.5F, y + posOffset.y * (double)0.5F, z + posOffset.z * (double)0.5F, motion.x, motion.y, motion.z);
            }

            for(int i = 0; i < cloudDensity; i += 2) {
                Vec3 posOffset = Utils.getRandomVec3((double)radius).scale((double)0.2F);
                Vec3 motion = posOffset.normalize().scale(0.6);
                motion = motion.add(Utils.getRandomVec3(0.18));
                level.addParticle(BLACK_FLAME_LONG.get(), x + posOffset.x * (double)0.5F, y + posOffset.y * (double)0.5F, z + posOffset.z * (double)0.5F, motion.x, motion.y, motion.z);
            }

        });
    }



}
