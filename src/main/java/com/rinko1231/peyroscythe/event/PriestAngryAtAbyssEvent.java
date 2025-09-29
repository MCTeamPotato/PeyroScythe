package com.rinko1231.peyroscythe.event;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.entity.mobs.wizards.priest.PriestEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;


import java.util.List;

public class PriestAngryAtAbyssEvent {
    @SubscribeEvent
    public void onPlayerCastSpell(SpellOnCastEvent event) {

        if (!PeyroScytheConfig.abyssAngerPriest.get()) return;
        Player player = event.getEntity();
        if (player.getAbilities().instabuild) {
            return;
        }
        String spellId = event.getSpellId();
        // 只处理特定法术
        if (!PeyroScytheConfig.spellsPriestDontLike.get().contains(spellId)) return;

        Level level = player.level();
        if (level.isClientSide) return;

        double radius = 9.0;

        // 搜索半径内牧师
        List<PriestEntity> nearbyMobs = level.getEntitiesOfClass(
                PriestEntity.class,
                player.getBoundingBox().inflate(radius),
                e -> true
        );
        boolean warned = false;
        for (PriestEntity priest : nearbyMobs) {

            // 如果已经敌对，则跳过
            if (priest.isHostileTowards(player)) continue;

            // 增加愤怒值
            priest.increaseAngerLevel(1, true);

            // 设置玩家为愤怒目标
            priest.setPersistentAngerTarget(player.getUUID());
            warned = true;
        }

        if (warned) {
            player.displayClientMessage(Component.translatable("message.peyroscythe.priest_warning"), true);
        }

    }
}
