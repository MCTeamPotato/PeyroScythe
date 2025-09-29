package com.rinko1231.peyroscythe.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;


import java.util.function.Supplier;

import static com.rinko1231.peyroscythe.PeyroScythe.MODID;

public class PeyroParticleRegistry {

        public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES;

        public static final Supplier<SimpleParticleType> BLACK_FLAME_LONG;
        public static final Supplier<SimpleParticleType> BLACK_FLAME;
        public static final Supplier<SimpleParticleType> BLACK_EMBERS;


        public PeyroParticleRegistry() {
        }

        public static void register(IEventBus eventBus) {
            PARTICLE_TYPES.register(eventBus);
        }

        static {
            PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, MODID);
            BLACK_FLAME_LONG = PARTICLE_TYPES.register("black_flame_long", () -> new SimpleParticleType(false));
            BLACK_FLAME = PARTICLE_TYPES.register("black_flame", () -> new SimpleParticleType(false));
            BLACK_EMBERS = PARTICLE_TYPES.register("black_embers", () -> new SimpleParticleType(false));


        }
    }
