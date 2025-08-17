package com.rinko1231.peyroscythe.event;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class MoonFrenzyEvent {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity attackerEntity)) return;

        if (!attackerEntity.getPersistentData().contains("moonFrenzyMarks", Tag.TAG_LIST)) return;

        ListTag marks = attackerEntity.getPersistentData().getList("moonFrenzyMarks", Tag.TAG_COMPOUND);
        for (Tag t : marks) {
            CompoundTag tag = (CompoundTag) t;
            UUID playerId = tag.getUUID("playerUUID");
            int spellLevel = tag.getInt("spellLevel");

            ServerPlayer player = (ServerPlayer) attackerEntity.level().getPlayerByUUID(playerId);
            if (player != null && player.isAlive()) {
                float healAmount = event.getAmount() * (PeyroScytheConfig.MoonFrenzyDamageHealBasicRatio.get().floatValue() + PeyroScytheConfig.MoonFrenzyDamageHealRatioGrowthPerLevel.get().floatValue() * spellLevel); // 可配置
                player.heal(healAmount);
            }
        }
    }

}
