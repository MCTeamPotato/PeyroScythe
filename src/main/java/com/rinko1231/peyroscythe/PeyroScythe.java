package com.rinko1231.peyroscythe;


import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.effect.MoonFrenzyEffect;
import com.rinko1231.peyroscythe.event.MoonFrenzyEvent;
import com.rinko1231.peyroscythe.event.SinfireEmbraceEvent;
import com.rinko1231.peyroscythe.init.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Mod(PeyroScythe.MOD_ID)
public class PeyroScythe {
    public static final String MOD_ID = "peyroscythe";
    public static final String MODID = "peyroscythe"; //下划线很烦

    public PeyroScythe() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        PeyroScytheConfig.setup();
        itemRegistry.ITEMS.register(modEventBus);
        TabInit.TABS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        EntityRegistry.register(modEventBus);
        NewSpellRegistry.register(modEventBus);
        MobEffectRegistry.MOB_EFFECT_DEFERRED_REGISTER.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new MoonFrenzyEvent());
        MinecraftForge.EVENT_BUS.register(new SinfireEmbraceEvent());
    }
    public static ResourceLocation id(@NotNull String path) {
        return new ResourceLocation(MOD_ID, path);
    }

/*
    @SubscribeEvent
    public void onPlayerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();

        // 检查 PersistentData 是否有 moonFrenzyMarks
        if (player.getPersistentData().contains("moonFrenzyMarks", Tag.TAG_LIST)) {
            ListTag marks = player.getPersistentData().getList("moonFrenzyMarks", Tag.TAG_COMPOUND);
            player.sendSystemMessage(Component.literal("§a[MoonFrenzy Debug] Found " + marks.size() + " marks:"));

            for (int i = 0; i < marks.size(); i++) {
                CompoundTag tag = marks.getCompound(i);
                UUID uuid = tag.getUUID("playerUUID");
                int spellLevel = tag.getInt("spellLevel");

                // 试着找这个 UUID 对应的玩家
                ServerPlayer markedPlayer = player.server.getPlayerList().getPlayer(uuid);
                String name = markedPlayer != null ? markedPlayer.getName().getString() : "Offline/Unknown";

                player.sendSystemMessage(Component.literal(" §eMark " + (i + 1) + ": " + uuid + " (" + name + "), spellLevel=" + spellLevel));
            }
        } else {
            player.sendSystemMessage(Component.literal("§c[MoonFrenzy Debug] No moonFrenzyMarks found on you."));
        }
    }
*/
}
