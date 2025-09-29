package com.rinko1231.peyroscythe.network.spell;

import com.rinko1231.peyroscythe.PeyroScythe;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientboundBloodSiphonReverseParticles implements CustomPacketPayload {
    private final Vec3 pos1;
    private final Vec3 pos2;
    public static final CustomPacketPayload.Type<ClientboundBloodSiphonReverseParticles> TYPE = new CustomPacketPayload.Type(PeyroScythe.id("blood_siphon_reverse_particles"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBloodSiphonReverseParticles> STREAM_CODEC = CustomPacketPayload.codec(ClientboundBloodSiphonReverseParticles::write, ClientboundBloodSiphonReverseParticles::new);

    public ClientboundBloodSiphonReverseParticles(Vec3 pos1, Vec3 pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public ClientboundBloodSiphonReverseParticles(FriendlyByteBuf buf) {
        this.pos1 = buf.readVec3();
        this.pos2 = buf.readVec3();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVec3(this.pos1);
        buf.writeVec3(this.pos2);
    }

    public static void handle(ClientboundBloodSiphonReverseParticles packet, IPayloadContext context) {
        context.enqueueWork(() -> handleClientboundBloodSiphonReverseParticles(packet.pos1, packet.pos2));
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
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
