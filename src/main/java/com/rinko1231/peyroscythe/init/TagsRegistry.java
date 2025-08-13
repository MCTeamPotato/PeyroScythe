package com.rinko1231.peyroscythe.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;

import static com.rinko1231.peyroscythe.PeyroScythe.MODID;

public class TagsRegistry {

    public static final TagKey<EntityType<?>> DEATH_SMOKE_IMMUNE;


    public TagsRegistry() {
    }

    private static TagKey<DamageType> create(String tag) {
        return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, tag));
    }

    static {
        DEATH_SMOKE_IMMUNE = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(MODID, "death_smoke_immune"));
   }
}
