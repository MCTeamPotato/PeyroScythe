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
    public static ForgeConfigSpec.BooleanValue deathSmokeAllowLooting;

    public static ForgeConfigSpec.DoubleValue deathSmokeBasicKillThreshold;
    public static ForgeConfigSpec.DoubleValue deathSmokeKillThresholdGrowthPerLevel;
    public static ForgeConfigSpec.IntValue deathSmokeErosionDuration;
    public static ForgeConfigSpec.DoubleValue deathSmokeHealthToHealthTransferRatePerLevel;
    public static ForgeConfigSpec.DoubleValue deathSmokeHealthToManaTransferRatePerLevel;

    public static ForgeConfigSpec.IntValue crimsonMoonDurationBasicTicks;
    public static ForgeConfigSpec.IntValue crimsonMoonDurationGrowthPerLevel;

    public static ForgeConfigSpec.DoubleValue MoonFrenzyDamageHealBasicRatio;
    public static ForgeConfigSpec.DoubleValue MoonFrenzyDamageHealRatioGrowthPerLevel;
    public static ForgeConfigSpec.DoubleValue MoonFrenzyAttackDamageBoostBasicRatio;
    public static ForgeConfigSpec.DoubleValue MoonFrenzyAttackDamageBoostRatioGrowthPerLevel;
    public static ForgeConfigSpec.DoubleValue MoonFrenzyAttackSpeedBoostBasicRatio;
    public static ForgeConfigSpec.DoubleValue MoonFrenzyAttackSpeedBoostRatioGrowthPerLevel;
    public static ForgeConfigSpec.DoubleValue MoonFrenzyBloodPowerBoostBasicRatio;
    public static ForgeConfigSpec.DoubleValue MoonFrenzyBloodPowerBoostRatioGrowthPerLevel;

    public static ForgeConfigSpec.IntValue sinfireEmbraceDurationBasicSeconds;
    public static ForgeConfigSpec.IntValue sinfireEmbraceDurationGrowthPerLevel;
    public static ForgeConfigSpec.DoubleValue sinfireEmbraceHealBasicRatio;
    public static ForgeConfigSpec.DoubleValue sinfireEmbraceHealRatioGrowthPerLevel;
    public static ForgeConfigSpec.DoubleValue sinfireEmbraceEffectFirePowerBoostBasicRatio;
    public static ForgeConfigSpec.DoubleValue sinfireEmbraceEffectFirePowerBoostRatioGrowthPerLevel;
    public static ForgeConfigSpec.DoubleValue sinfireEmbraceEffectArmorLossRatio;

    static {


        BUILDER.comment("Peyro Scythe Config");

        BUILDER.push("Frost Hell Spell");

        frostHellAllowLooting = BUILDER
                .define("Frost Hell can be found in loot chests",false);
        frostHellDegree = BUILDER
                .defineInRange("frostHellDegree", 90 ,1, 360);
        frostHellBasicRadius = BUILDER
                .defineInRange("frostHellBasicRadius", 5.0F ,0, 100);

        BUILDER.pop();

        BUILDER.push("Glacier Fall Spell");

        glacierAllowLooting = BUILDER
                .define("Glacier Fall can be found in loot chests",true);
        glacierScaleGrowthPerLevel = BUILDER
                .defineInRange("glacierScaleGrowthPerLevel", 1.0F ,0, 100);
        glacierScaleMax = BUILDER
                .defineInRange("Max Scale of glacier", 10F ,0, 114514);
        glacierFallShowParticles = BUILDER
                .define("Spawn Particles when the Glacier falls on ground",true);

        BUILDER.pop();

        BUILDER.push("Death Smoke Spell");

        deathSmokeAllowLooting = BUILDER
                .define("Death Smoke can be found in loot chests",false);
        deathSmokeBasicKillThreshold = BUILDER
                .defineInRange("deathSmokeBasicKillThreshold", 0.05 ,0, 1);
        deathSmokeKillThresholdGrowthPerLevel = BUILDER
                .defineInRange("deathSmokeKillThresholdGrowthPerLevel", 0.01F ,0, 1);
        deathSmokeErosionDuration = BUILDER
                .defineInRange("deathSmokeErosionDuration", 80 ,1, Integer.MAX_VALUE);

        deathSmokeHealthToHealthTransferRatePerLevel = BUILDER
                .defineInRange("deathSmokeHealthToHealthTransferRatePerLevel", 0.33 ,0, Integer.MAX_VALUE);
        deathSmokeHealthToManaTransferRatePerLevel = BUILDER
                .defineInRange("deathSmokeHealthToManaTransferRatePerLevel", 0.66 ,0, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("Crimson Moon Spell");

        crimsonMoonDurationBasicTicks = BUILDER
                .defineInRange("crimsonMoonDurationBasicTicks", 640 ,0, Integer.MAX_VALUE);
        crimsonMoonDurationGrowthPerLevel = BUILDER
                .defineInRange("crimsonMoonDurationGrowthPerLevel", 160 ,0,  Integer.MAX_VALUE);
        MoonFrenzyDamageHealBasicRatio = BUILDER
                .defineInRange("MoonFrenzyDamageHealBasicRatio", 0.1F ,0, Integer.MAX_VALUE);
        MoonFrenzyDamageHealRatioGrowthPerLevel = BUILDER
                .defineInRange("MoonFrenzyDamageHealRatioGrowthPerLevel", 0.05 ,0, Integer.MAX_VALUE);

        MoonFrenzyAttackDamageBoostBasicRatio = BUILDER
                .defineInRange("MoonFrenzyAttackDamageBoostBasicRatio", 0.1F ,0, Integer.MAX_VALUE);
        MoonFrenzyAttackDamageBoostRatioGrowthPerLevel = BUILDER
                .defineInRange("MoonFrenzyAttackDamageBoostRatioGrowthPerLevel", 0.05 ,0, Integer.MAX_VALUE);

        MoonFrenzyAttackSpeedBoostBasicRatio = BUILDER
                .defineInRange("MoonFrenzyAttackSpeedBoostBasicRatio", 0.1F ,0, Integer.MAX_VALUE);
        MoonFrenzyAttackSpeedBoostRatioGrowthPerLevel = BUILDER
                .defineInRange("MoonFrenzyAttackSpeedBoostRatioGrowthPerLevel", 0.05 ,0, Integer.MAX_VALUE);

        MoonFrenzyBloodPowerBoostBasicRatio = BUILDER
                .defineInRange("MoonFrenzyBloodPowerBoostBasicRatio", 0.1F ,0, Integer.MAX_VALUE);
        MoonFrenzyBloodPowerBoostRatioGrowthPerLevel = BUILDER
                .defineInRange("MoonFrenzyBloodPowerBoostRatioGrowthPerLevel", 0.05 ,0, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("Sinfire Embrace Spell");

        sinfireEmbraceDurationBasicSeconds = BUILDER
                .defineInRange("sinfireEmbraceDurationBasicSeconds", 8 ,0, Integer.MAX_VALUE);
        sinfireEmbraceDurationGrowthPerLevel = BUILDER
                .defineInRange("sinfireEmbraceDurationGrowthPerLevel", 4,0, Integer.MAX_VALUE);
        sinfireEmbraceHealBasicRatio = BUILDER
                .defineInRange("sinfireEmbraceHealBasicRatio", 0.1 ,0, Integer.MAX_VALUE);
        sinfireEmbraceHealRatioGrowthPerLevel = BUILDER
                .defineInRange("sinfireEmbraceHealRatioGrowthPerLevel", 0.05,0, Integer.MAX_VALUE);
        sinfireEmbraceEffectFirePowerBoostBasicRatio = BUILDER
                .defineInRange("sinfireEmbraceEffectFirePowerBoostBasicRatio", 0.1 ,0, Integer.MAX_VALUE);
        sinfireEmbraceEffectFirePowerBoostRatioGrowthPerLevel = BUILDER
                .defineInRange("sinfireEmbraceEffectFirePowerBoostRatioGrowthPerLevel", 0.05,0, Integer.MAX_VALUE);
        sinfireEmbraceEffectArmorLossRatio = BUILDER
                .defineInRange("sinfireEmbraceEffectArmorLossRatio", -0.6,-1, 0);


        SPEC = BUILDER.build();
    }
    public static void setup() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "PeyroScytheConfig.toml");
    }


}