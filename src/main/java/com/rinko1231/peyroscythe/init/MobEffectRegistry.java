package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.effect.*;
import com.rinko1231.peyroscythe.effect.abyssal_grace.AbyssalGraceEffect;
import io.redspace.ironsspellbooks.effect.SummonTimer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.rinko1231.peyroscythe.PeyroScythe.MOD_ID;

public class MobEffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECT_DEFERRED_REGISTER;

    public static final RegistryObject<MobEffect> DEATH_SMOKE_EROSION;
    public static final RegistryObject<MobEffect> MOON_FRENZY;
    public static final RegistryObject<MobEffect> SINFIRE_EMBRACE;
    public static final RegistryObject<MobEffect> FROZEN;
    public static final RegistryObject<MobEffect> FROZEN_RESISTANCE;
    public static final RegistryObject<MobEffect> VIATOR_MUNDI;
    public static final RegistryObject<MobEffect> FLASHBANGED;
    public static final RegistryObject<MobEffect> HOLY_BELL_SUPPRESSION;
    public static final RegistryObject<MobEffect> ABYSSAL_GRACE;
    public static final RegistryObject<MobEffect> SUMMON_BLAZE_TIMER;


    static {
        MOB_EFFECT_DEFERRED_REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, MOD_ID);
        DEATH_SMOKE_EROSION = MOB_EFFECT_DEFERRED_REGISTER.register("death_smoke_erosion", DeathSmokeErosionEffect::new);
        MOON_FRENZY = MOB_EFFECT_DEFERRED_REGISTER.register("moon_frenzy", MoonFrenzyEffect::new);
        SINFIRE_EMBRACE = MOB_EFFECT_DEFERRED_REGISTER.register("sinfire_embrace", SinfireEmbraceEffect::new);
        FROZEN = MOB_EFFECT_DEFERRED_REGISTER.register("frozen", FrozenEffect::new);
        FROZEN_RESISTANCE = MOB_EFFECT_DEFERRED_REGISTER.register("frozen_resistance", FrozenResistanceEffect::new);
        VIATOR_MUNDI = MOB_EFFECT_DEFERRED_REGISTER.register("viator_mundi", ViatorMundiEffect::new);
        FLASHBANGED = MOB_EFFECT_DEFERRED_REGISTER.register("flashbanged", FlashbangedEffect::new);
        HOLY_BELL_SUPPRESSION = MOB_EFFECT_DEFERRED_REGISTER.register("holy_bell_suppression", HolyBellSuppressionEffect::new);
        ABYSSAL_GRACE = MOB_EFFECT_DEFERRED_REGISTER.register("abyssal_grace", AbyssalGraceEffect::new);
        SUMMON_BLAZE_TIMER = MOB_EFFECT_DEFERRED_REGISTER.register("summon_blaze_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141));
   }

    public MobEffectRegistry() {
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECT_DEFERRED_REGISTER.register(eventBus);
    }
}