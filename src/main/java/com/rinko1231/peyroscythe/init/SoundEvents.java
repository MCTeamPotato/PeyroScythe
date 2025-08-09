package com.rinko1231.peyroscythe.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundEvents {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "peyroscythe");


    public static final RegistryObject<SoundEvent> RAISE_HELL_PREPARE = SOUND_EVENTS.register("raise_hell_prepare", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("peyroscythe", "spell.raise_hell.prepare")));

    public static final RegistryObject<SoundEvent> FIRE_ERUPTION_SLAM = SOUND_EVENTS.register("fire_eruption_slam", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("peyroscythe", "entity.fire_eruption.slam")));
}
