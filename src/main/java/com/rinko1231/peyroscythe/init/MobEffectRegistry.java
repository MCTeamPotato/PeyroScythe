package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.effect.DeathSmokeErosionEffect;
import com.rinko1231.peyroscythe.effect.SinfireEmbraceEffect;
import com.rinko1231.peyroscythe.effect.MoonFrenzyEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.rinko1231.peyroscythe.PeyroScythe.MOD_ID;

public class MobEffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECT_DEFERRED_REGISTER;

    public static final RegistryObject<MobEffect> DEATH_SMOKE_EROSION;
    public static final RegistryObject<MobEffect> MOON_FRENZY;
    public static final RegistryObject<MobEffect> SINFIRE_EMBRACE;


    public MobEffectRegistry() {
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECT_DEFERRED_REGISTER.register(eventBus);
    }

    static {
        MOB_EFFECT_DEFERRED_REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, MOD_ID);
        DEATH_SMOKE_EROSION = MOB_EFFECT_DEFERRED_REGISTER.register("death_smoke_erosion", DeathSmokeErosionEffect::new);
        MOON_FRENZY = MOB_EFFECT_DEFERRED_REGISTER.register("moon_frenzy", MoonFrenzyEffect::new);
        SINFIRE_EMBRACE = MOB_EFFECT_DEFERRED_REGISTER.register("sinfire_embrace", SinfireEmbraceEffect::new);

    }
}