package com.rinko1231.peyroscythe.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class PeyroScytheConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue frostHellAllowLooting;
    public static ForgeConfigSpec.IntValue frostHellDegree;
    public static ForgeConfigSpec.DoubleValue frostHellBasicRadius;


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

    public static ForgeConfigSpec.BooleanValue crimsonMoonAllowLooting;
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

    public static ForgeConfigSpec.BooleanValue sinfireEmbraceAllowLooting;
    public static ForgeConfigSpec.IntValue sinfireEmbraceDurationBasicSeconds;
    public static ForgeConfigSpec.IntValue sinfireEmbraceDurationGrowthPerLevel;
    public static ForgeConfigSpec.DoubleValue sinfireEmbraceHealBasicRatio;
    public static ForgeConfigSpec.DoubleValue sinfireEmbraceHealRatioGrowthPerLevel;
    public static ForgeConfigSpec.DoubleValue sinfireEmbraceEffectFirePowerBoostBasicRatio;
    public static ForgeConfigSpec.DoubleValue sinfireEmbraceEffectFirePowerBoostRatioGrowthPerLevel;
    public static ForgeConfigSpec.DoubleValue sinfireEmbraceEffectArmorLossRatio;

    public static ForgeConfigSpec.BooleanValue frozenWorldAllowLooting;
    public static ForgeConfigSpec.BooleanValue frozenWorldFreezesWorld;
    public static ForgeConfigSpec.DoubleValue frozenWorldMaxHealthDamageRatioPerLevel;
    public static ForgeConfigSpec.IntValue frozenWorldFrozenEffectDurationToEntityInInnerCircle;
    public static ForgeConfigSpec.IntValue frozenWorldIceFogDuration;
    public static ForgeConfigSpec.DoubleValue frozenWorldInnerCircleRatio;
    public static ForgeConfigSpec.DoubleValue frostFogInnerCircleRatio;

    public static ForgeConfigSpec.BooleanValue icyCometRainAllowLooting;
    public static ForgeConfigSpec.IntValue icyCometRainMaxCountPerTick;
    public static ForgeConfigSpec.DoubleValue icyCometRainMaxRadius;
    public static ForgeConfigSpec.DoubleValue icyCometRainDamageRatioOfSpellPower;

    public static ForgeConfigSpec.DoubleValue iceJudgementATK;
    public static ForgeConfigSpec.DoubleValue iceJudgementSPD;
    public static ForgeConfigSpec.DoubleValue captainGregScytheATK;
    public static ForgeConfigSpec.DoubleValue captainGregScytheSPD;
    public static ForgeConfigSpec.DoubleValue captainGregLanceATK;
    public static ForgeConfigSpec.DoubleValue captainGregLanceSPD;

    public static ForgeConfigSpec.BooleanValue entityDropKnightMedal;
    public static ForgeConfigSpec.BooleanValue entityDropWintermoonHeart;
    public static ForgeConfigSpec.DoubleValue entityDropCuriosItemPossibility;
    public static ForgeConfigSpec.DoubleValue entityDropDonutPossibility;

    static {


        BUILDER.comment("Peyro Scythe Config");

        BUILDER.push("Frost Hell Spell");

        frostHellAllowLooting = BUILDER
                .comment("Whether Frost Hell spell can be obtained from loot chests")
                .define("frostHellAllowLooting",false);
        frostHellDegree = BUILDER
                .comment("Arc degree of Frost Hell attack")
                .defineInRange("frostHellDegree", 90 ,1, 360);
        frostHellBasicRadius = BUILDER
                .comment("Basic radius of Frost Hell in blocks")
                .defineInRange("frostHellBasicRadius", 5.0F ,0, 100);

        BUILDER.pop();

        BUILDER.push("Glacier Fall Spell");

        glacierAllowLooting = BUILDER
                .comment("Whether Glacier Fall can be obtained from loot chests")
                .define("glacierAllowLooting",true);
        glacierScaleGrowthPerLevel = BUILDER
                .comment("Scale growth per spell level for Glacier")
                .defineInRange("glacierScaleGrowthPerLevel", 1.0F ,0, 100);
        glacierScaleMax = BUILDER
                .comment("Maximum scale of Glacier")
                .defineInRange("glacierScaleMax", 10F ,0, 114514);
        glacierFallShowParticles = BUILDER
                .comment("Whether to spawn particles when Glacier hits the ground")
                .define("Spawn Particles when the Glacier falls on ground",true);

        BUILDER.pop();

        BUILDER.push("Death Smoke Spell");

        deathSmokeAllowLooting = BUILDER
                .comment("Whether Death Smoke can be obtained from loot chests")
                .define("deathSmokeAllowLooting",true);
        deathSmokeBasicKillThreshold = BUILDER
                .comment("Basic health percentage threshold to instantly kill")
                .defineInRange("deathSmokeBasicKillThreshold", 0.05 ,0, 1);
        deathSmokeKillThresholdGrowthPerLevel = BUILDER
                .comment("Kill threshold growth per spell level")
                .defineInRange("deathSmokeKillThresholdGrowthPerLevel", 0.01F ,0, 1);
        deathSmokeErosionDuration = BUILDER
                .comment("Duration of erosion effect in ticks")
                .defineInRange("deathSmokeErosionDuration", 80 ,1, Integer.MAX_VALUE);

        deathSmokeHealthToHealthTransferRatePerLevel = BUILDER
                .comment("Health to health transfer rate per level")
                .defineInRange("deathSmokeHealthToHealthTransferRatePerLevel", 0.33 ,0, Integer.MAX_VALUE);
        deathSmokeHealthToManaTransferRatePerLevel = BUILDER
                .comment("Health to mana transfer rate per level")
                .defineInRange("deathSmokeHealthToManaTransferRatePerLevel", 0.66 ,0, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("Crimson Moon Spell");

        crimsonMoonAllowLooting = BUILDER
                .comment("Whether Crimson Moon can be obtained from loot chests")
                .define("Crimson Moon can be found in loot chests",true);

        crimsonMoonDurationBasicTicks = BUILDER
                .comment("Base duration of Crimson Moon in ticks")
                .defineInRange("crimsonMoonDurationBasicTicks", 640 ,0, Integer.MAX_VALUE);
        crimsonMoonDurationGrowthPerLevel = BUILDER
                .comment("Duration growth per spell level in ticks")
                .defineInRange("crimsonMoonDurationGrowthPerLevel", 160 ,0,  Integer.MAX_VALUE);
        MoonFrenzyDamageHealBasicRatio = BUILDER
                .comment("Basic heal ratio from damage dealt from Moon Frenzy Marks")
                .defineInRange("MoonFrenzyDamageHealBasicRatio", 0.1F ,0, Integer.MAX_VALUE);
        MoonFrenzyDamageHealRatioGrowthPerLevel = BUILDER
                .comment("Heal ratio growth per spell level from Moon Frenzy Marks")
                .defineInRange("MoonFrenzyDamageHealRatioGrowthPerLevel", 0.05 ,0, Integer.MAX_VALUE);

        MoonFrenzyAttackDamageBoostBasicRatio = BUILDER
                .comment("Base attack damage boost ratio of Moon Frenzy Effect")
                .defineInRange("MoonFrenzyAttackDamageBoostBasicRatio", 0.1F ,0, Integer.MAX_VALUE);
        MoonFrenzyAttackDamageBoostRatioGrowthPerLevel = BUILDER
                .comment("Attack damage boost growth per level of Moon Frenzy Effect")
                .defineInRange("MoonFrenzyAttackDamageBoostRatioGrowthPerLevel", 0.05 ,0, Integer.MAX_VALUE);

        MoonFrenzyAttackSpeedBoostBasicRatio = BUILDER
                .comment("Base attack speed boost ratio of Moon Frenzy Effect")
                .defineInRange("MoonFrenzyAttackSpeedBoostBasicRatio", 0.1F ,0, Integer.MAX_VALUE);
        MoonFrenzyAttackSpeedBoostRatioGrowthPerLevel = BUILDER
                .comment("Attack speed boost growth per level of Moon Frenzy Effect")
                .defineInRange("MoonFrenzyAttackSpeedBoostRatioGrowthPerLevel", 0.05 ,0, Integer.MAX_VALUE);

        MoonFrenzyBloodPowerBoostBasicRatio = BUILDER
                .comment("Base Blood Spell Power boost ratio of Moon Frenzy Effect")
                .defineInRange("MoonFrenzyBloodPowerBoostBasicRatio", 0.1F ,0, Integer.MAX_VALUE);
        MoonFrenzyBloodPowerBoostRatioGrowthPerLevel = BUILDER
                .comment("Blood Spell Power boost growth per level of Moon Frenzy Effect")
                .defineInRange("MoonFrenzyBloodPowerBoostRatioGrowthPerLevel", 0.05 ,0, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("Sinfire Embrace Spell");

        sinfireEmbraceAllowLooting = BUILDER
                .comment("Whether Sinfire Embrace can be obtained from loot chests")
                .define("sinfireEmbraceAllowLooting",true);
        sinfireEmbraceDurationBasicSeconds = BUILDER
                .comment("Base duration of Sinfire Embrace in seconds")
                .defineInRange("sinfireEmbraceDurationBasicSeconds", 8 ,0, Integer.MAX_VALUE);
        sinfireEmbraceDurationGrowthPerLevel = BUILDER
                .comment("Sinfire Embrace Duration growth per spell level in seconds")
                .defineInRange("sinfireEmbraceDurationGrowthPerLevel", 4,0, Integer.MAX_VALUE);
        sinfireEmbraceHealBasicRatio = BUILDER
                .comment("Base heal ratio from Sinfire Embrace Effect")
                .defineInRange("sinfireEmbraceHealBasicRatio", 0.1 ,0, Integer.MAX_VALUE);
        sinfireEmbraceHealRatioGrowthPerLevel = BUILDER
                .comment("Heal ratio growth per level from Sinfire Embrace Effect")
                .defineInRange("sinfireEmbraceHealRatioGrowthPerLevel", 0.05,0, Integer.MAX_VALUE);
        sinfireEmbraceEffectFirePowerBoostBasicRatio = BUILDER
                .comment("Base Fire Spell Power boost ratio from Sinfire Embrace Effect")
                .defineInRange("sinfireEmbraceEffectFirePowerBoostBasicRatio", 0.1 ,0, Integer.MAX_VALUE);
        sinfireEmbraceEffectFirePowerBoostRatioGrowthPerLevel = BUILDER
                .comment("Fire Spell Power boost growth per level")
                .defineInRange("sinfireEmbraceEffectFirePowerBoostRatioGrowthPerLevel", 0.05,0, Integer.MAX_VALUE);
        sinfireEmbraceEffectArmorLossRatio = BUILDER
                .comment("Armor loss ratio (negative means loss)")
                .defineInRange("sinfireEmbraceEffectArmorLossRatio", -0.6,-1, 0);

        BUILDER.pop();

        BUILDER.push("The Frozen World Spell");

        frozenWorldAllowLooting = BUILDER
                .comment("Whether The Frozen World can be obtained from loot chests")
                .define("frozenWorldAllowLooting",true);
        frozenWorldFreezesWorld = BUILDER
                .comment("Whether The Frozen World can freeze the world (generating snow, extinguishing fire, turning water and lava into frosted ice and obsidian)")
                .define("frozenWorldFreezesWorld",true);
        frozenWorldMaxHealthDamageRatioPerLevel = BUILDER
                .comment("Max Health Damage from The Frozen World per Level")
                .defineInRange("frozenWorldMaxHealthDamageRatioPerLevel", 0.08f ,0, Integer.MAX_VALUE);
        frozenWorldFrozenEffectDurationToEntityInInnerCircle = BUILDER
                .comment("Duration (in ticks) of the frozen effect applied to entities in the inner circle of The Frozen World")
                .defineInRange("frozenWorldFrozenEffectDurationToEntityInInnerCircle", 200, 0, Integer.MAX_VALUE);
        frozenWorldIceFogDuration = BUILDER
                .comment("Duration (in ticks) of the Ice Fog spawned by The Frozen World spell")
                .defineInRange("frozenWorldIceFogDuration", 160, 1, Integer.MAX_VALUE);
        frozenWorldInnerCircleRatio = BUILDER
                .comment("The ratio of the Frozen World's inner circle radius " +
                        "(instant freeze & water/lava freezing area). " +
                        "Range: 0.1 - 1.0")
                .defineInRange("frozenWorldInnerCircleRatio", 0.50D, 0.1D, 1.0D);
        frostFogInnerCircleRatio = BUILDER
                .comment("The ratio of the Frozen World's inner circle radius " +
                        "(stronger frozen effect).d " +
                        "Range: 0.0 - 1.0")
                .defineInRange("frozenWorldInnerCircleRatio", 0.30D, 0.0D, 1.0D);

        BUILDER.pop();


        BUILDER.push("Icy Comet Rain Spell");

        icyCometRainAllowLooting = BUILDER
                .comment("Whether Ice Comet Rain can be obtained from loot chests")
                .define("iceCometRainAllowLooting",true);

        icyCometRainMaxCountPerTick = BUILDER
                .comment("Max Count of Ice Comets Per Tick")
                .defineInRange("iceCometRainMaxCountPerTick", 12, 2, 100);

        icyCometRainMaxRadius = BUILDER
                .comment("Max Radius of Ice Comet Rain Area")
                .defineInRange("iceCometRainMaxRadius",12.0F,4.0F,114.0F);

        icyCometRainDamageRatioOfSpellPower = BUILDER
                .comment("Damage = Icy Comet Rain Spell Power * this")
                .defineInRange("icyCometRainDamageRatioOfSpellPower", 0.4f, 0.0f, Integer.MAX_VALUE);


        BUILDER.pop();

        BUILDER.push("Weapons");

       iceJudgementATK = BUILDER
                .defineInRange("iceJudgementATK", 9.0F ,0, Integer.MAX_VALUE);
       iceJudgementSPD = BUILDER
                .defineInRange("iceJudgementSPD", -2.0F ,-10, 0);

       captainGregScytheATK = BUILDER
                .defineInRange("captainGregScytheATK", 7.0F ,0, Integer.MAX_VALUE);
        captainGregScytheSPD = BUILDER
                .defineInRange("captainGregScytheSPD", -2.0F ,-10, 0);

       captainGregLanceATK = BUILDER
                .defineInRange("captainGregLanceATK", 5.0F ,0, Integer.MAX_VALUE);
       captainGregLanceSPD = BUILDER
                .defineInRange("captainGregLanceSPD", -1.33F ,-10, 0);




        BUILDER.pop();

        BUILDER.push("MISC");

        entityDropKnightMedal = BUILDER
                .comment("Whether Pontifical Knight Medal can drop from entities")
                .define("entityDropKnightMedal",true);
        entityDropWintermoonHeart = BUILDER
                .comment("Whether Wintermoon Heart can drop from entities")
                .define("entityDropWintermoonHeart",true);
        entityDropCuriosItemPossibility = BUILDER
                .comment("Chance of dropping Knight Medal or Wintermoon Heart without killing with Holy or Ice Spell")
                .defineInRange("entityDropCuriosItemPossibility", 0.33F ,0, 1);
        entityDropDonutPossibility = BUILDER
                .comment("Chance of dropping Captain Greg Donut with certain Weapons")
                .defineInRange("entityDropCuriosItemPossibility", 0.01F ,0, 1);


        SPEC = BUILDER.build();
    }
    public static void setup() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "PeyroScytheConfig.toml");
    }


}