package com.rinko1231.peyroscythe.config;


import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class PeyroScytheConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static ModConfigSpec SPEC;

    public static ModConfigSpec.BooleanValue frostHellAllowLooting;
    public static ModConfigSpec.IntValue frostHellDegree;
    public static ModConfigSpec.DoubleValue frostHellBasicRadius;


    public static ModConfigSpec.BooleanValue glacierAllowLooting;
    public static ModConfigSpec.DoubleValue glacierScaleGrowthPerLevel;
    public static ModConfigSpec.DoubleValue glacierScaleMax;
    public static ModConfigSpec.BooleanValue glacierFallShowParticles;

    public static ModConfigSpec.BooleanValue deathSmokeAllowLooting;
    public static ModConfigSpec.DoubleValue deathSmokeBasicKillThreshold;
    public static ModConfigSpec.DoubleValue deathSmokeKillThresholdGrowthPerLevel;
    public static ModConfigSpec.IntValue deathSmokeErosionDuration;
    public static ModConfigSpec.DoubleValue deathSmokeHealthToHealthTransferRatePerLevel;
    public static ModConfigSpec.DoubleValue deathSmokeHealthToManaTransferRatePerLevel;

    public static ModConfigSpec.BooleanValue crimsonMoonAllowLooting;
    public static ModConfigSpec.IntValue crimsonMoonDurationBasicTicks;
    public static ModConfigSpec.IntValue crimsonMoonDurationGrowthPerLevel;

    public static ModConfigSpec.DoubleValue MoonFrenzyDamageHealBasicRatio;
    public static ModConfigSpec.DoubleValue MoonFrenzyDamageHealRatioGrowthPerLevel;
    public static ModConfigSpec.DoubleValue MoonFrenzyAttackDamageBoostBasicRatio;
    public static ModConfigSpec.DoubleValue MoonFrenzyAttackDamageBoostRatioGrowthPerLevel;
    public static ModConfigSpec.DoubleValue MoonFrenzyAttackSpeedBoostBasicRatio;
    public static ModConfigSpec.DoubleValue MoonFrenzyAttackSpeedBoostRatioGrowthPerLevel;
    public static ModConfigSpec.DoubleValue MoonFrenzyBloodPowerBoostBasicRatio;
    public static ModConfigSpec.DoubleValue MoonFrenzyBloodPowerBoostRatioGrowthPerLevel;

    public static ModConfigSpec.BooleanValue sinfireEmbraceAllowLooting;
    public static ModConfigSpec.IntValue sinfireEmbraceDurationBasicSeconds;
    public static ModConfigSpec.IntValue sinfireEmbraceDurationGrowthPerLevel;
    public static ModConfigSpec.DoubleValue sinfireEmbraceHealBasicRatio;
    public static ModConfigSpec.DoubleValue sinfireEmbraceHealRatioGrowthPerLevel;
    public static ModConfigSpec.DoubleValue sinfireEmbraceEffectFirePowerBoostBasicRatio;
    public static ModConfigSpec.DoubleValue sinfireEmbraceEffectFirePowerBoostRatioGrowthPerLevel;
    public static ModConfigSpec.DoubleValue sinfireEmbraceEffectArmorLossRatio;

    public static ModConfigSpec.BooleanValue frozenWorldAllowLooting;
    public static ModConfigSpec.BooleanValue frozenWorldFreezesWorld;
    public static ModConfigSpec.DoubleValue frozenWorldMaxHealthDamageRatioPerLevel;
    public static ModConfigSpec.IntValue frozenWorldFrozenEffectDurationToEntityInInnerCircle;
    public static ModConfigSpec.IntValue frozenWorldIceFogDuration;
    public static ModConfigSpec.DoubleValue frozenWorldInnerCircleRatio;
    public static ModConfigSpec.DoubleValue frostFogInnerCircleRatio;

    public static ModConfigSpec.BooleanValue icyCometRainAllowLooting;
    public static ModConfigSpec.IntValue icyCometRainMaxCountPerTick;
    public static ModConfigSpec.DoubleValue icyCometRainMaxRadius;
    public static ModConfigSpec.DoubleValue icyCometRainDamageRatioOfSpellPower;

    public static ModConfigSpec.BooleanValue holyRayAllowLooting;
    public static ModConfigSpec.DoubleValue holyRayRange;
    public static ModConfigSpec.DoubleValue holyRayExtraMaxHealthDamageRatioCap;
    public static ModConfigSpec.DoubleValue holyRayExtraMaxHealthDamageCap;
    public static ModConfigSpec.DoubleValue holyRayExtraRatioDamageToUndead;


    public static ModConfigSpec.BooleanValue holyLanceAllowLooting;
    public static ModConfigSpec.DoubleValue holyLanceExtraRatioDamageToUndead;

    public static ModConfigSpec.BooleanValue holyBellAllowLooting;
    public static ModConfigSpec.IntValue holyBellDurationCap;
    public static ModConfigSpec.DoubleValue holyBellRadiusCap;

    public static ModConfigSpec.DoubleValue holyBellSuppressionMovementSpeedRatio;
    public static ModConfigSpec.DoubleValue holyBellSuppressionATKLowerRatio;
    public static ModConfigSpec.DoubleValue holyBellSuppressionSPDLowerRatio;

    public static ModConfigSpec.BooleanValue mundusAllowLooting;
    public static ModConfigSpec.BooleanValue mundusFlashbanged;

    public static ModConfigSpec.BooleanValue chaosCradleAllowLooting;
    public static ModConfigSpec.IntValue chaosCradleAbyssalMudBaseDuration;
    public static ModConfigSpec.IntValue chaosCradleAbyssalMudDurationPerLevel;

    public static ModConfigSpec.BooleanValue summonBlazeAllowLooting;
    public static ModConfigSpec.DoubleValue summonBlazeSenatorExtraHealth;

    public static ModConfigSpec.BooleanValue illusionFearAllowLooting;
    public static ModConfigSpec.BooleanValue illusionCrazyAllowLooting;
    public static ModConfigSpec.DoubleValue illusionCrazySearchRadius;

    public static ModConfigSpec.BooleanValue youngForYouAllowLooting;

    public static ModConfigSpec.BooleanValue blackFlameWingsAllowLooting;
    public static ModConfigSpec.DoubleValue blackFlameWingsEldritchPowerBoostRatio;
    public static ModConfigSpec.DoubleValue blackFlameWingsSpellResistBoostRatio;
    public static ModConfigSpec.DoubleValue blackFlameWingsArmorToughnessBoostRatio;
    public static ModConfigSpec.DoubleValue blackFlameWingsMovementSpeedBoostRatio;

    public static ModConfigSpec.BooleanValue blackFireballAllowLooting;

    public static ModConfigSpec.DoubleValue iceJudgementATK;
    public static ModConfigSpec.DoubleValue iceJudgementSPD;
    public static ModConfigSpec.DoubleValue captainGregScytheATK;
    public static ModConfigSpec.DoubleValue captainGregScytheSPD;
    public static ModConfigSpec.DoubleValue captainGregLanceATK;
    public static ModConfigSpec.DoubleValue captainGregLanceSPD;

    public static ModConfigSpec.BooleanValue entityDropKnightMedal;
    public static ModConfigSpec.BooleanValue entityDropWintermoonHeart;
    public static ModConfigSpec.DoubleValue entityDropCuriosItemPossibility;
    public static ModConfigSpec.DoubleValue entityDropDonutPossibility;

    public static ModConfigSpec.BooleanValue abyssAngerPriest;
    public static ModConfigSpec.ConfigValue<List<? extends String>> spellsPriestDontLike;

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
                .defineInRange("deathSmokeBasicKillThreshold", 0.04 ,0, 1);
        deathSmokeKillThresholdGrowthPerLevel = BUILDER
                .comment("Kill threshold growth per spell level")
                .defineInRange("deathSmokeKillThresholdGrowthPerLevel", 0.008F ,0, 1);
        deathSmokeErosionDuration = BUILDER
                .comment("Duration of erosion effect in ticks")
                .defineInRange("deathSmokeErosionDuration", 160 ,1, Integer.MAX_VALUE);

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

        BUILDER.push("Nero's Holy Ray Spell");

        holyRayAllowLooting = BUILDER
                .comment("Whether Nero's Holy Ray can be obtained from loot chests")
                .define("holyRayAllowLooting", true);


        holyRayRange = BUILDER
                .comment("The maximum range (in blocks) of Nero's Holy Ray")
                .defineInRange("holyRayRange", 32.0D, 1.0D, 128.0D);


        holyRayExtraMaxHealthDamageRatioCap = BUILDER
                .comment("The maximum ratio of target's max health that can be added as extra damage (cap)")
                .defineInRange("holyRayExtraMaxHealthDamageRatioCap", 0.3D, 0.0D, 1.0D);


        holyRayExtraMaxHealthDamageCap = BUILDER
                .comment("The maximum absolute extra damage value that Nero's Holy Ray can deal (hard cap)")
                .defineInRange("holyRayExtraMaxHealthDamageCap", 50.0D, 0.0D, Integer.MAX_VALUE);

        holyRayExtraRatioDamageToUndead = BUILDER
                .defineInRange("holyRayExtraRatioDamageToUndead", 0.1, 0.0, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("Holy Lance Spell");

        holyLanceAllowLooting = BUILDER
                .comment("Whether Holy Lance can be obtained from loot chests")
                .define("holyLanceAllowLooting", true);

        holyLanceExtraRatioDamageToUndead = BUILDER
                .defineInRange("holyLanceExtraRatioDamageToUndead", 0.1, 0.0, Integer.MAX_VALUE);


        BUILDER.pop();

        BUILDER.push("Holy Bell Spell");

        holyBellAllowLooting = BUILDER
                .comment("Whether Holy Bell can be obtained from loot chests")
                .define("holyBellAllowLooting", true);
        holyBellDurationCap = BUILDER
                .comment("Max Duration (in ticks) of the Holy Golden Bell Entity")
                .defineInRange("holyBellDurationCap", 480, 60, Integer.MAX_VALUE);
        holyBellRadiusCap = BUILDER
                .comment("Max Radius (in blocks) of the Holy Golden Bell Entity")
                .defineInRange("holyBellRadiusCap", 12.0f, 2.0f, 64.0f);

        holyBellSuppressionMovementSpeedRatio = BUILDER
                .comment("Multiplier for non-downward movement speed under Holy Bell Suppression (0.1 = 10% of original, default 0.1)")
                .defineInRange("holyBellSuppressionMovementSpeedRatio", 0.1D, 0.0D, 1.0D);
        holyBellSuppressionATKLowerRatio = BUILDER
                .comment("Attack damage reduction ratio under Holy Bell Suppression (0.2 = -20%, default 0.2)")
                .defineInRange("holyBellSuppressionATKLowerRatio", 0.2D, 0.0D, 1.0D);
        holyBellSuppressionSPDLowerRatio = BUILDER
                .comment("Attack speed reduction ratio under Holy Bell Suppression (0.2 = -20%, default 0.2)")
                .defineInRange("holyBellSuppressionSPDLowerRatio", 0.2D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER.push("The Mundus Spell");

        mundusAllowLooting = BUILDER
                .comment("Whether The Mundus can be obtained from loot chests")
                .define("mundusAllowLooting", false);

        mundusFlashbanged = BUILDER
                .comment("Whether The Mundus will Flashbanged Teleporting player")
                .define("mundusFlashbanged", true);

        BUILDER.pop();

        BUILDER.push("Chaos Cradle Spell");

        chaosCradleAllowLooting = BUILDER
                .comment("Whether Cradle of The Chaos can be obtained from loot chests")
                .define("chaosCradleAllowLooting", true);

        chaosCradleAbyssalMudBaseDuration = BUILDER
                .comment("Base duration of Abyssal Mud in ticks (b of y = a * spellLevel + b)")
                .defineInRange("chaosCradleAbyssalMudBaseDuration", 120, 20, Integer.MAX_VALUE);

        chaosCradleAbyssalMudDurationPerLevel = BUILDER
                .comment("Duration Growth per spellLevel of Abyssal Mud in ticks (a of y = a * spellLevel + b)")
                .defineInRange("chaosCradleAbyssalMudDurationPerLevel", 120, 0, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("Summon Blaze/ \"Gate of Truth Party\" Spell");

        summonBlazeAllowLooting = BUILDER
                .comment("Whether Summon Blaze can be obtained from loot chests")
                .define("summonBlazeAllowLooting",true);

        summonBlazeSenatorExtraHealth = BUILDER
                .comment("Senator's Extra Health Ratio")
                .defineInRange("summonBlazeSenatorExtraHealth", 0.8D ,0.0, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("The Abyss of Illusion Spell");

        illusionFearAllowLooting = BUILDER
                .comment("Whether Illusion: Be Fear can be obtained from loot chests")
                .define("illusionFearAllowLooting",true);

        illusionCrazyAllowLooting = BUILDER
                .comment("Whether Illusion: Be Crazy can be obtained from loot chests")
                .define("illusionCrazyAllowLooting",true);

        illusionCrazySearchRadius = BUILDER
                .defineInRange("illusionCrazySearchRadius", 8.0D ,1.0D, 128);

        BUILDER.pop();

        BUILDER.push("Young For You Spell");

        youngForYouAllowLooting = BUILDER
                .comment("Whether Young For You can be obtained from loot chests")
                .define("youngForYouAllowLooting",true);

        BUILDER.pop();

        BUILDER.push("Black Flame Wings (Fake) Spell");

        blackFlameWingsAllowLooting = BUILDER
                .comment("Whether Black Flame Wings (Fake) can be obtained from loot chests")
                .define("blackFlameWingsAllowLooting",true);
        /*
        blackFlameWingsEldritchPowerBoostRatio = BUILDER
                .comment("Eldritch Spell Power boost ratio of Black Flame Wings Effect")
                .defineInRange("blackFlameWingsEldritchPowerBoostRatio", 0.15F ,-1, Integer.MAX_VALUE);
        blackFlameWingsSpellResistBoostRatio = BUILDER
                .comment("Spell Resist boost ratio of Black Flame Wings Effect")
                .defineInRange("blackFlameWingsSpellResistBoostRatio", 0.15F ,-1, Integer.MAX_VALUE);
        blackFlameWingsArmorToughnessBoostRatio = BUILDER
                .comment("Armor Toughness boost ratio of Black Flame Wings Effect")
                .defineInRange("blackFlameWingsArmorToughnessBoostRatio", 0.3F ,-1, Integer.MAX_VALUE);
        blackFlameWingsMovementSpeedBoostRatio = BUILDER
                .comment("Movement Speed boost ratio of Black Flame Wings Effect")
                .defineInRange("blackFlameWingsMovementSpeedBoostRatio", 0.3F ,-1, Integer.MAX_VALUE);
*/
        BUILDER.pop();

        BUILDER.push("Black Fireball/ Fireball of Chaos Spell");

        blackFireballAllowLooting = BUILDER
                .comment("Whether Black Fireball/ Fireball of Chaos can be obtained from loot chests")
                .define("blackFireballAllowLooting",true);


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
                .defineInRange("entityDropDonutPossibility", 0.01F ,0, 1);


        BUILDER.pop();

        BUILDER.push("Abyss and Priest");

        abyssAngerPriest = BUILDER
                .comment("Whether certain spells (like Abyssal/Eldritch spells from this mod) anger Priests")
                .define("abyssAngerPriest",false);

        spellsPriestDontLike = BUILDER
                .comment("Spells that will anger Priests")
                .defineList("spellsPriestDontLike", List.of("peyroscythe:death_smoke", "peyroscythe:black_flame_wings", "peyroscythe:black_fireball", "peyroscythe:chaos_cradle"),
                        element -> element instanceof String);


        SPEC = BUILDER.build();
    }



}