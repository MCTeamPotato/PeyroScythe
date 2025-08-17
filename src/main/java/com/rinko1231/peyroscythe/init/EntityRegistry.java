package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.spellentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "peyroscythe");

    public static final RegistryObject<EntityType<IceQuakeAoe>> ICE_QUAKE_AOE =
            ENTITIES.register("ice_quake",
                    () -> EntityType.Builder.<IceQuakeAoe>of(IceQuakeAoe::new, MobCategory.MISC)
                            .sized(4.0F, 0.8F)
                            .setTrackingRange(64)
                            .build(new ResourceLocation("peyroscythe",
                                    "ice_quake").toString())
            );
    // 冰川术冰块弹体注册
    public static final RegistryObject<EntityType<GlacierIceBlockProjectile>> GLACIER_ICE_BLOCK_PROJECTILE =
            ENTITIES.register("glacier_ice_block_projectile",
                    () -> EntityType.Builder.<GlacierIceBlockProjectile>of(GlacierIceBlockProjectile::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .setTrackingRange(64)
                            .setUpdateInterval(1)
                            .build(new ResourceLocation("peyroscythe",
                                    "glacier_ice_block_projectile").toString())
            );
    public static final RegistryObject<EntityType<DeathSmokeProjectile>> DEATH_SMOKE_PROJECTILE =
            ENTITIES.register("death_smoke",
                    () -> EntityType.Builder.<DeathSmokeProjectile>of(DeathSmokeProjectile::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .clientTrackingRange(64)
                            .build((new ResourceLocation("peyroscythe",
                                    "death_smoke")).toString()));

    public static final RegistryObject<EntityType<CrimsonMoon>> CRIMSON_MOON =
            ENTITIES.register("crimson_moon",
                    () -> EntityType.Builder.<CrimsonMoon>of(CrimsonMoon::new, MobCategory.MISC)
                            .sized(11.0F, 11.0F)
                            .clientTrackingRange(64)
                            .build((new ResourceLocation("peyroscythe",
                                    "crimson_moon")).toString()));
    public static final RegistryObject<EntityType<IcyCometEntity>> ICY_COMET =
            ENTITIES.register("icy_comet",
                    () -> EntityType.Builder.<IcyCometEntity>of(IcyCometEntity::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .clientTrackingRange(64)
                            .build(PeyroScythe.id("icy_comet").toString()));
    public static final RegistryObject<EntityType<IceTombEntity>> ICE_TOMB =
            ENTITIES.register("ice_tomb", () -> EntityType.Builder.<IceTombEntity>of(IceTombEntity::new, MobCategory.MISC)
                    .sized(1.0f, 2.2f)
                    .clientTrackingRange(64)
                    .build(PeyroScythe.id("ice_tomb").toString()));
    public static final RegistryObject<EntityType<FrostFogEntity>> FROST_FOG =
            ENTITIES.register("frost_fog", () -> EntityType.Builder.<FrostFogEntity>of(FrostFogEntity::new, MobCategory.MISC)
                    .sized(4.0F, 1.2F)
                    .clientTrackingRange(64)
                    .build(PeyroScythe.id("frost_fog").toString()));




    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}