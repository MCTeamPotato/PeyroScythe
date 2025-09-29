package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.network.spell.ClientboundBlackFireballExplosionParticles;
import com.rinko1231.peyroscythe.network.spell.ClientboundBloodSiphonReverseParticles;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.rinko1231.peyroscythe.PeyroScythe.MODID;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = MODID)

public class PayloadRegister {

        @SubscribeEvent
        public static void register(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar payloadRegistrar = event.registrar(MODID).versioned("1.0.0").optional();
            //PARTICLES
            payloadRegistrar.playToClient(ClientboundBlackFireballExplosionParticles.TYPE, ClientboundBlackFireballExplosionParticles.STREAM_CODEC, ClientboundBlackFireballExplosionParticles::handle);
            payloadRegistrar.playToClient(ClientboundBloodSiphonReverseParticles.TYPE, ClientboundBloodSiphonReverseParticles.STREAM_CODEC, ClientboundBloodSiphonReverseParticles::handle);

  }
    }