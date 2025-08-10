package com.rinko1231.peyroscythe;


import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PeyroScythe.MOD_ID)
public class PeyroScythe {
    public static final String MOD_ID = "peyroscythe";

    public PeyroScythe() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        PeyroScytheConfig.setup();
        itemRegistry.ITEMS.register(modEventBus);
        TabInit.TABS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        EntityRegistry.register(modEventBus);
        NewSpellRegistry.register(modEventBus);
    }


}
