package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.spell.GlacierSpell;
import com.rinko1231.peyroscythe.spell.RaiseHellSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.minecraftforge.eventbus.api.IEventBus;

public class NewSpellRegistry {
    public static final DeferredRegister<AbstractSpell> SPELLS =
            DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, "peyroscythe");

    public static final RegistryObject<AbstractSpell> RAISE_HELL_SPELL =
            SPELLS.register("raise_hell", RaiseHellSpell::new);
    public static final RegistryObject<AbstractSpell> GLACIER_SPELL =
            SPELLS.register("glacier", GlacierSpell::new);

    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }
}
