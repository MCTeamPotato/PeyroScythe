package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.spell.FireEruptionAoe;
import com.rinko1231.peyroscythe.spell.GlacierIceBlockProjectile;
import net.minecraft.core.registries.Registries;
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

    public static final RegistryObject<EntityType<FireEruptionAoe>> FIRE_ERUPTION_AOE =
            ENTITIES.register("fire_eruption",
                    () -> EntityType.Builder.<FireEruptionAoe>of(FireEruptionAoe::new, MobCategory.MISC)
                            .sized(4.0F, 0.8F)
                            .setTrackingRange(64)
                            .build(new ResourceLocation("peyroscythe", "fire_eruption").toString())
            );
    // 冰川术冰块弹体注册
    public static final RegistryObject<EntityType<GlacierIceBlockProjectile>> GLACIER_ICE_BLOCK_PROJECTILE =
            ENTITIES.register("glacier_ice_block_projectile",
                    () -> EntityType.Builder.<GlacierIceBlockProjectile>of(GlacierIceBlockProjectile::new, MobCategory.MISC)
                            .sized(9.0F, 9.0F) // 可调整到和 IceBlockProjectile 一样的尺寸
                            .setTrackingRange(64)
                            .setUpdateInterval(1) // 投射物推荐 1 tick 更新一次
                            .build(new ResourceLocation("peyroscythe", "glacier_ice_block_projectile").toString())
            );

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}