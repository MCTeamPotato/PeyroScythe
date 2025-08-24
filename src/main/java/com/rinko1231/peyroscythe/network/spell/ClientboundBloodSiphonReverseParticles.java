package com.rinko1231.peyroscythe.network.spell;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundBloodSiphonReverseParticles {
    private Vec3 pos1;
    private Vec3 pos2;

    public ClientboundBloodSiphonReverseParticles(Vec3 pos1, Vec3 pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public ClientboundBloodSiphonReverseParticles(FriendlyByteBuf buf) {
        this.pos1 = this.readVec3(buf);
        this.pos2 = this.readVec3(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        this.writeVec3(this.pos1, buf);
        this.writeVec3(this.pos2, buf);
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
        ctx.enqueueWork(() -> handleClientboundBloodSiphonReverseParticles(this.pos1, this.pos2));
        return true;
    }

    public static void handleClientboundBloodSiphonReverseParticles(Vec3 pos1, Vec3 pos2) {
        if (Minecraft.getInstance().player != null) {
            Level level = Minecraft.getInstance().player.level();
            Vec3 direction = pos1.subtract(pos2).scale((double)0.1F);

            for(int i = 0; i < 40; ++i) {
                Vec3 scaledDirection = direction.scale((double)1.0F + Utils.getRandomScaled(0.35));
                Vec3 random = new Vec3(Utils.getRandomScaled((double)0.08F), Utils.getRandomScaled((double)0.08F), Utils.getRandomScaled((double)0.08F));
                level.addParticle(ParticleHelper.BLOOD, pos1.x, pos1.y, pos1.z, scaledDirection.x + random.x, scaledDirection.y + random.y, scaledDirection.z + random.z);
            }

        }
    }
}
