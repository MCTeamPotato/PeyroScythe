package com.rinko1231.peyroscythe.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class PeyroScytheConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;


    public static ForgeConfigSpec.IntValue frostHellDegree;
    public static ForgeConfigSpec.DoubleValue frostHellBasicRadius;
    public static ForgeConfigSpec.BooleanValue frostHellAllowLooting;
    public static ForgeConfigSpec.BooleanValue glacierAllowLooting;
    public static ForgeConfigSpec.DoubleValue glacierScaleGrowthPerLevel;
    public static ForgeConfigSpec.DoubleValue glacierScaleMax;
    public static ForgeConfigSpec.BooleanValue glacierFallShowParticles;

    static {


        BUILDER.push("Peyro Scythe Config");


        frostHellDegree = BUILDER
                .defineInRange("frostHellDegree", 90 ,1, 360);
        frostHellBasicRadius = BUILDER
                .defineInRange("frostHellBasicRadius", 5.0F ,0, 100);
        frostHellAllowLooting = BUILDER
                .define("Frost Hell can be found in loot chests",false);
        glacierAllowLooting = BUILDER
                .define("Glacier Fall can be found in loot chests",true);
        glacierScaleGrowthPerLevel = BUILDER
                .defineInRange("glacierScaleGrowthPerLevel", 1.0F ,0, 100);
        glacierScaleMax = BUILDER
                .defineInRange("Max Scale of glacier", 10F ,0, 114514);
        glacierFallShowParticles = BUILDER
                .define("Spawn Particles when the Glacier falls on ground",true);


        BUILDER.pop();

        SPEC = BUILDER.build();
    }
    public static void setup() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "PeyroScytheConfig.toml");
    }


}