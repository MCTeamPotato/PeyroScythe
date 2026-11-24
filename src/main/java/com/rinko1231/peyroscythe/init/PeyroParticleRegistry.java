package com.rinko1231.peyroscythe.init;

import com.mojang.serialization.Codec;
import io.redspace.ironsspellbooks.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.rinko1231.peyroscythe.PeyroScythe.MODID;

public class PeyroParticleRegistry {

        public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES;

        public static final RegistryObject<SimpleParticleType> BLACK_FLAME_LONG;
        public static final RegistryObject<SimpleParticleType> BLACK_FLAME;
        public static final RegistryObject<SimpleParticleType> BLACK_EMBERS;


        public PeyroParticleRegistry() {
        }

        public static void register(IEventBus eventBus) {
            PARTICLE_TYPES.register(eventBus);
        }

        static {
            PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
            BLACK_FLAME_LONG = PARTICLE_TYPES.register("black_flame_long", () -> new SimpleParticleType(false));
            BLACK_FLAME = PARTICLE_TYPES.register("black_flame", () -> new SimpleParticleType(false));
            BLACK_EMBERS = PARTICLE_TYPES.register("black_embers", () -> new SimpleParticleType(false));


        }
    }
