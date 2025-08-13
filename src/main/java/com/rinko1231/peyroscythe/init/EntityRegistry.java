package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.spellentity.CrimsonMoon;
import com.rinko1231.peyroscythe.spellentity.DeathSmokeProjectile;
import com.rinko1231.peyroscythe.spellentity.IceQuakeAoe;
import com.rinko1231.peyroscythe.spellentity.GlacierIceBlockProjectile;
import io.redspace.ironsspellbooks.entity.spells.black_hole.BlackHole;
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


    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}