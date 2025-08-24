package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.spellentity.TempestEdgeProjectile;
import com.rinko1231.peyroscythe.spellentity.*;
import com.rinko1231.peyroscythe.spellentity.eldritch.AbyssMudEntity;
import com.rinko1231.peyroscythe.spellentity.eldritch.DeathSmokeProjectile;
import com.rinko1231.peyroscythe.spellentity.ender.MadeInHeavenProjectile;
import com.rinko1231.peyroscythe.spellentity.ender.MadeInHeavenRealProjectile;
import com.rinko1231.peyroscythe.spellentity.holy.GoldenBellEntity;
import com.rinko1231.peyroscythe.spellentity.holy.HolyLanceProjectile;
import com.rinko1231.peyroscythe.spellentity.holy.MundusEntity;
import com.rinko1231.peyroscythe.spellentity.holy.NeroHolyRayVisualEntity;
import com.rinko1231.peyroscythe.spellentity.ice.*;
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
    public static final RegistryObject<EntityType<GoldenBellEntity>> GOLDEN_BELL =
            ENTITIES.register("golden_bell",
                    () -> EntityType.Builder.<GoldenBellEntity>of(GoldenBellEntity::new, MobCategory.MISC)
                            .sized(11.0F, 11.0F)
                            .clientTrackingRange(64)
                            .build((new ResourceLocation("peyroscythe",
                                    "golden_bell")).toString()));
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
    public static final RegistryObject<EntityType<AbyssMudEntity>> ABYSS_MUD =
            ENTITIES.register("abyss_mud", () -> EntityType.Builder.<AbyssMudEntity>of(AbyssMudEntity::new, MobCategory.MISC)
                    .sized(4.0F, 1.2F)
                    .clientTrackingRange(64)
                    .build(PeyroScythe.id("abyss_mud").toString()));
    public static final RegistryObject<EntityType<MundusEntity>> MUNDUS =
            ENTITIES.register("mundus",
                    () -> EntityType.Builder.<MundusEntity>of(MundusEntity::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .clientTrackingRange(64)
                            .build((new ResourceLocation("peyroscythe",
                                    "mundus")).toString()));
    public static final RegistryObject<EntityType<HolyLanceProjectile>> HOLY_LANCE_PROJECTILE =
            ENTITIES.register("holy_lance",
                    () -> EntityType.Builder.<HolyLanceProjectile>of(HolyLanceProjectile::new, MobCategory.MISC)
                            .sized(1.25F, 1.25F)
                            .clientTrackingRange(64)
                            .build((PeyroScythe.id("holy_lance")).toString()));
    public static final RegistryObject<EntityType<TempestEdgeProjectile>> TEMPEST_EDGE_PROJECTILE =
            ENTITIES.register("tempest_edge",
                    () -> EntityType.Builder.<TempestEdgeProjectile>of(TempestEdgeProjectile::new, MobCategory.MISC)
                            .sized(0.5F, 2.0F)
                            .clientTrackingRange(64)
                            .build(((PeyroScythe.id( "tempest_edge")).toString())));
    public static final RegistryObject<EntityType<TempestEdgeHProjectile>> TEMPEST_EDGE_H_PROJECTILE =
            ENTITIES.register("tempest_edge_h",
                    () -> EntityType.Builder.<TempestEdgeHProjectile>of(TempestEdgeHProjectile::new, MobCategory.MISC)
                            .sized(2.0F, 0.5F)
                            .clientTrackingRange(64)
                            .build((PeyroScythe.id( "tempest_edge_h")).toString()));

    public static final RegistryObject<EntityType<NeroHolyRayVisualEntity>> NERO_HOLY_RAY_VISUAL_ENTITY =
            ENTITIES.register("nero_holy_ray",
                    () -> EntityType.Builder.<NeroHolyRayVisualEntity>of(NeroHolyRayVisualEntity::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .clientTrackingRange(64)
                            .build((PeyroScythe.id( "nero_holy_ray")).toString()));


    public static final RegistryObject<EntityType<SummonedBlaze>> SUMMONED_BLAZE =
            ENTITIES.register("summoned_blaze",
                    () -> EntityType.Builder.<SummonedBlaze>of(SummonedBlaze::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.8F)
                            .fireImmune()
                            .clientTrackingRange(8)
                            .build((PeyroScythe.id( "summoned_blaze")).toString()));

    public static final RegistryObject<EntityType<MadeInHeavenProjectile>> MADE_IN_HEAVEN_PROJECTILE =
            ENTITIES.register("made_in_heaven_projectile",
                    () -> EntityType.Builder.<MadeInHeavenProjectile>of(MadeInHeavenProjectile::new, MobCategory.MISC)
                            .sized(0.0F, 0.0F)
                            .clientTrackingRange(64)
                            .build((new ResourceLocation("peyroscythe",
                                    "made_in_heaven_projectile")).toString()));
    public static final RegistryObject<EntityType<MadeInHeavenRealProjectile>> MADE_IN_HEAVEN_REAL_PROJECTILE =
            ENTITIES.register("made_in_heaven_real_projectile",
                    () -> EntityType.Builder.<MadeInHeavenRealProjectile>of(MadeInHeavenRealProjectile::new, MobCategory.MISC)
                            .sized(0.0F, 0.0F)
                            .clientTrackingRange(64)
                            .build((new ResourceLocation("peyroscythe",
                                    "made_in_heaven_real_projectile")).toString()));


    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}