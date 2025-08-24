package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

import static com.rinko1231.peyroscythe.PeyroScythe.MODID;

public class TagsRegistry {

    public static final TagKey<EntityType<?>> DEATH_SMOKE_IMMUNE;
    public static final TagKey<EntityType<?>> CURIOS_DROP;
    public static final TagKey<EntityType<?>> MUST_FREEZE_BOSS;
    public static final TagKey<Structure> MUNDUS_SUPPORT;
    public static final TagKey<Structure> MUNDUS_VILLAGE;

    public static final TagKey<Block> TEMPEST_EDGE_DESTROYABLE;

    public static final TagKey<Block> MADE_IN_HEAVEN_BLACKLIST;


    public TagsRegistry() {
    }

    private static TagKey<DamageType> create(String tag) {
        return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, tag));
    }

    static {
        CURIOS_DROP = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(MODID, "curios_drop"));
        DEATH_SMOKE_IMMUNE = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(MODID, "death_smoke_immune"));
        MUST_FREEZE_BOSS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(MODID, "must_freeze_boss"));
        MUNDUS_SUPPORT = TagKey.create(Registries.STRUCTURE, PeyroScythe.id("mundus_support"));
        MUNDUS_VILLAGE = TagKey.create(Registries.STRUCTURE, PeyroScythe.id("mundus_village"));
        TEMPEST_EDGE_DESTROYABLE = TagKey.create(Registries.BLOCK, PeyroScythe.id("tempest_edge_destroyable"));
   MADE_IN_HEAVEN_BLACKLIST = TagKey.create(Registries.BLOCK, PeyroScythe.id("made_in_heaven_blacklist"));
    }
}
