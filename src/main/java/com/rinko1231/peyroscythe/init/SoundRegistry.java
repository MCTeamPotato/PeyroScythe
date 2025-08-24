package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.PeyroScythe;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.rinko1231.peyroscythe.PeyroScythe.MODID;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static final RegistryObject<SoundEvent> RECORD_CLAIR_DE_LUNE =
            SOUND_EVENTS.register("records.clair_de_lune",
                    () -> SoundEvent.createVariableRangeEvent(PeyroScythe.id("records.clair_de_lune")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
