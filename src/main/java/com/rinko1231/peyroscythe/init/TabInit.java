package com.rinko1231.peyroscythe.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.rinko1231.peyroscythe.PeyroScythe.MOD_ID;


public class TabInit {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> PEYRO_SCYTHE_TAB = TABS.register(MOD_ID, () -> CreativeModeTab.builder()
            // Set name of tab to display
            .title(Component.translatable("item_group." + MOD_ID))
            // Set icon of creative tab
            .icon(() -> new ItemStack(itemRegistry.ICE_JUDGMENT.get()))
            // Add default items to tab
            .displayItems((params, output) -> {
                itemRegistry.ITEMS.getEntries().forEach(it -> output.accept(it.get()));
            })
            .build()
    );
}