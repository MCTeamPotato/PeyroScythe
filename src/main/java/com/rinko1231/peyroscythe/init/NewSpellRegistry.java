package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.spell.*;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.minecraftforge.eventbus.api.IEventBus;

public class NewSpellRegistry {
    public static final DeferredRegister<AbstractSpell> SPELLS =
            DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, "peyroscythe");

    public static final RegistryObject<AbstractSpell> FROST_HELL_SPELL =
            SPELLS.register("frost_hell", FrostHellSpell::new);
    public static final RegistryObject<AbstractSpell> GLACIER_SPELL =
            SPELLS.register("glacier", GlacierSpell::new);
    public static final RegistryObject<AbstractSpell> DEATH_SMOKE_SPELL =
            SPELLS.register("death_smoke", DeathSmokeSpell::new);
    public static final RegistryObject<AbstractSpell> CRIMSON_MOON_SPELL =
            SPELLS.register("crimson_moon", CrimsonMoonSpell::new);
    public static final RegistryObject<AbstractSpell> SINFIRE_EMBRACE_SPELL =
            SPELLS.register("sinfire_embrace", SinfireEmbraceSpell::new);
    //public static final RegistryObject<AbstractSpell> ICE_TOMB_SPELL = SPELLS.register("ice_tomb", IceTombSpell::new);
    public static final RegistryObject<AbstractSpell> FROZEN_WORLD_SPELL =
            SPELLS.register("the_frozen_world", TheFrozenWorldSpell::new);
    public static final RegistryObject<AbstractSpell> ICY_COMET_RAIN =
            SPELLS.register("icy_comet_rain", IcyCometRainSpell::new);

    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }
}
