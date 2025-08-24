package com.rinko1231.peyroscythe.init;


import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.network.spell.ClientboundBloodSiphonReverseParticles;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class MyMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    public MyMessages() {
    }

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder.named(PeyroScythe.id("messages")).networkProtocolVersion(() -> "1.0").clientAcceptedVersions((s) -> true).serverAcceptedVersions((s) -> true).simpleChannel();
        INSTANCE = net;
        net.messageBuilder(ClientboundBloodSiphonReverseParticles.class,
                id(),
                NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundBloodSiphonReverseParticles::new)
                .encoder(ClientboundBloodSiphonReverseParticles::toBytes)
                .consumerMainThread(ClientboundBloodSiphonReverseParticles::handle)
                .add();
 }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToAllPlayers(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendToPlayersTrackingEntity(MSG message, Entity entity) {
        sendToPlayersTrackingEntity(message, entity, false);
    }

    public static <MSG> void sendToPlayersTrackingEntity(MSG message, Entity entity, boolean sendToSource) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
        if (sendToSource && entity instanceof ServerPlayer serverPlayer) {
            sendToPlayer(message, serverPlayer);
        }

    }
}
