package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.effect.*;
import com.rinko1231.peyroscythe.effect.abyssal_grace.AbyssalGraceEffect;
import com.rinko1231.peyroscythe.effect.death_smoke.DeathSmokeErosionEffect;
import com.rinko1231.peyroscythe.effect.illusion.IllusionFearEffect;
import com.rinko1231.peyroscythe.effect.illusion.IllusionCrazyEffect;
import com.rinko1231.peyroscythe.effect.wind_order.WindOrderEffect;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


import static com.rinko1231.peyroscythe.PeyroScythe.MOD_ID;

public class MobEffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECT_DEFERRED_REGISTER;

    public static final DeferredHolder<MobEffect, MobEffect> DEATH_SMOKE_EROSION;
    public static final DeferredHolder<MobEffect, MobEffect>  MOON_FRENZY;
    public static final DeferredHolder<MobEffect, MobEffect>  SINFIRE_EMBRACE;
    //public static final DeferredHolder<MobEffect, MobEffect>  FROZEN;
    public static final DeferredHolder<MobEffect, MobEffect>  FROZEN_RESISTANCE;
    public static final DeferredHolder<MobEffect, MobEffect>  VIATOR_MUNDI;
    //public static final DeferredHolder<MobEffect, MobEffect>  FLASHBANGED;
    public static final DeferredHolder<MobEffect, MobEffect>  HOLY_BELL_SUPPRESSION;
    public static final DeferredHolder<MobEffect, MobEffect>  ABYSSAL_GRACE;
    //public static final DeferredHolder<MobEffect, MobEffect>  SUMMON_BLAZE_TIMER;
    public static final DeferredHolder<MobEffect, MobEffect>  ILLUSION_FEAR;
    public static final DeferredHolder<MobEffect, MobEffect>  ILLUSION_CRAZY;

    public static final DeferredHolder<MobEffect, MobEffect>  FOREVER_YOUNG;
    public static final DeferredHolder<MobEffect, MobEffect>  PEYRO_CHAN;

    public static final DeferredHolder<MobEffect, MobEffect>  BLACK_FLAME_WINGS;
    public static final DeferredHolder<MobEffect, MobEffect>  WIND_ORDER;


    static {
        MOB_EFFECT_DEFERRED_REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, MOD_ID);
        DEATH_SMOKE_EROSION = MOB_EFFECT_DEFERRED_REGISTER.register("death_smoke_erosion", DeathSmokeErosionEffect::new);
        MOON_FRENZY = MOB_EFFECT_DEFERRED_REGISTER.register("moon_frenzy", MoonFrenzyEffect::new);
        SINFIRE_EMBRACE = MOB_EFFECT_DEFERRED_REGISTER.register("sinfire_embrace", SinfireEmbraceEffect::new);
        //FROZEN = MOB_EFFECT_DEFERRED_REGISTER.register("frozen", FrozenEffect::new);
        FROZEN_RESISTANCE = MOB_EFFECT_DEFERRED_REGISTER.register("frozen_resistance", FrozenResistanceEffect::new);
        VIATOR_MUNDI = MOB_EFFECT_DEFERRED_REGISTER.register("viator_mundi", ViatorMundiEffect::new);
        //FLASHBANGED = MOB_EFFECT_DEFERRED_REGISTER.register("flashbanged", FlashbangedEffect::new);
        HOLY_BELL_SUPPRESSION = MOB_EFFECT_DEFERRED_REGISTER.register("holy_bell_suppression", HolyBellSuppressionEffect::new);
        ABYSSAL_GRACE = MOB_EFFECT_DEFERRED_REGISTER.register("abyssal_grace", AbyssalGraceEffect::new);
        //SUMMON_BLAZE_TIMER = MOB_EFFECT_DEFERRED_REGISTER.register("summon_blaze_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141));
        ILLUSION_FEAR = MOB_EFFECT_DEFERRED_REGISTER.register("illusion_fear", IllusionFearEffect::new);
        ILLUSION_CRAZY = MOB_EFFECT_DEFERRED_REGISTER.register("illusion_crazy", IllusionCrazyEffect::new);
        FOREVER_YOUNG = MOB_EFFECT_DEFERRED_REGISTER.register("forever_young", ForeverYoungEffect::new);
        PEYRO_CHAN = MOB_EFFECT_DEFERRED_REGISTER.register("peyro_chan", PeyroChanEffect::new);
        BLACK_FLAME_WINGS = MOB_EFFECT_DEFERRED_REGISTER.register("black_flame_wings", () -> (new BlackFlameWingsEffect(MobEffectCategory.BENEFICIAL, 12495141)));
        WIND_ORDER = MOB_EFFECT_DEFERRED_REGISTER.register("wind_order", WindOrderEffect::new);

    }

    public MobEffectRegistry() {
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECT_DEFERRED_REGISTER.register(eventBus);
    }
}