package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.spell.*;
import com.rinko1231.peyroscythe.spell.eldritch.*;
import com.rinko1231.peyroscythe.spell.ender.MadeInHeavenSpell;
import com.rinko1231.peyroscythe.spell.ender.MadeInHeavenRealSpell;
import com.rinko1231.peyroscythe.spell.fire.SinfireEmbraceSpell;
import com.rinko1231.peyroscythe.spell.fire.SummonBlazeSpell;
import com.rinko1231.peyroscythe.spell.holy.HolyLanceSpell;
import com.rinko1231.peyroscythe.spell.holy.HolyBellSpell;

import com.rinko1231.peyroscythe.spell.holy.NeroHolyRaySpell;
import com.rinko1231.peyroscythe.spell.ice.FrostHellSpell;
import com.rinko1231.peyroscythe.spell.ice.GlacierSpell;
import com.rinko1231.peyroscythe.spell.ice.IcyCometRainSpell;
import com.rinko1231.peyroscythe.spell.ice.TheFrozenWorldSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class NewSpellRegistry {
    public static final DeferredRegister<AbstractSpell> SPELLS =
            DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, "peyroscythe");

    public static final Supplier<AbstractSpell> FROST_HELL_SPELL =
            SPELLS.register("frost_hell", FrostHellSpell::new);
    public static final Supplier<AbstractSpell> GLACIER_SPELL =
            SPELLS.register("glacier", GlacierSpell::new);
    public static final Supplier<AbstractSpell> DEATH_SMOKE_SPELL =
            SPELLS.register("death_smoke", DeathSmokeSpell::new);
    public static final Supplier<AbstractSpell> CRIMSON_MOON_SPELL =
            SPELLS.register("crimson_moon", CrimsonMoonSpell::new);
    public static final Supplier<AbstractSpell> HOLY_BELL_SPELL =
            SPELLS.register("holy_bell", HolyBellSpell::new);
    public static final Supplier<AbstractSpell> SINFIRE_EMBRACE_SPELL =
            SPELLS.register("sinfire_embrace", SinfireEmbraceSpell::new);
    //public static final Supplier<AbstractSpell> ICE_TOMB_SPELL = SPELLS.register("ice_tomb", IceTombSpell::new);
    public static final Supplier<AbstractSpell> FROZEN_WORLD_SPELL =
            SPELLS.register("the_frozen_world", TheFrozenWorldSpell::new);
    public static final Supplier<AbstractSpell> ICY_COMET_RAIN =
            SPELLS.register("icy_comet_rain", IcyCometRainSpell::new);
    public static final Supplier<AbstractSpell> NERO_HOLY_RAY =
            SPELLS.register("nero_holy_ray", NeroHolyRaySpell::new);
    //public static final Supplier<AbstractSpell> MUNDUS = SPELLS.register("mundus", MundusSpell::new);
    public static final Supplier<AbstractSpell> HOLY_LANCE =
            SPELLS.register("holy_lance", HolyLanceSpell::new);
    //public static final Supplier<AbstractSpell> RYAN_TEMPEST_EDGE = SPELLS.register("ryan_tempest_edge",RyanTempestEdgeSpell::new);
    public static final Supplier<AbstractSpell> CHAOS_CRADLE =
            SPELLS.register("chaos_cradle", ChaosCradleSpell::new);
    public static final Supplier<AbstractSpell> SUMMON_BLAZE =
            SPELLS.register("summon_blaze", SummonBlazeSpell::new);

    public static final Supplier<AbstractSpell> ILLUSION_FEAR =
            SPELLS.register("illusion_fear", IllusionFearSpell::new);
    public static final Supplier<AbstractSpell> ILLUSION_CRAZY =
            SPELLS.register("illusion_crazy", IllusionCrazySpell::new);

    public static final Supplier<AbstractSpell> YOUNG_FOR_YOU =
            SPELLS.register("young_for_you", YoungForYouSpell::new);

    public static final Supplier<AbstractSpell> BLACK_FLAME_WINGS =
            SPELLS.register("black_flame_wings", BlackFlameWingsSpell::new);
    public static final Supplier<AbstractSpell> BLACK_FIREBALL =
            SPELLS.register("black_fireball",BlackFireballSpell::new);


    public static final Supplier<AbstractSpell> MADE_IN_HEAVEN =
            SPELLS.register("made_in_heaven", MadeInHeavenSpell::new);
    public static final Supplier<AbstractSpell> MADE_IN_HEAVEN_REAL =
            SPELLS.register("made_in_heaven_real", MadeInHeavenRealSpell::new);
    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }
}
