package com.rinko1231.peyroscythe.utils.abyssmud;

import com.rinko1231.peyroscythe.spellentity.eldritch.AbyssMudEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public final class AbyssMudLink {
    // 玩家 persistentData 根键
    public static final String TAG_ABYSS_MUDS = "peyroscythe:abyss_muds"; // CompoundTag
    private AbyssMudLink() {}

    private static String dimKey(ResourceKey<Level> dim) {
        return dim.location().toString(); // 例: "minecraft:overworld"
    }

    public static Optional<UUID> getMudUUID(Player player, ResourceKey<Level> dim) {
        CompoundTag root = player.getPersistentData().getCompound(TAG_ABYSS_MUDS);
        String key = dimKey(dim);
        if (root.hasUUID(key)) return Optional.of(root.getUUID(key));
        return Optional.empty();
    }

    public static void putMudUUID(Player player, ResourceKey<Level> dim, UUID uuid) {
        CompoundTag root = player.getPersistentData().getCompound(TAG_ABYSS_MUDS);
        root.putUUID(dimKey(dim), uuid);
        player.getPersistentData().put(TAG_ABYSS_MUDS, root);
    }

    public static void removeMudRecord(Player player, ResourceKey<Level> dim) {
        CompoundTag root = player.getPersistentData().getCompound(TAG_ABYSS_MUDS);
        root.remove(dimKey(dim));
        player.getPersistentData().put(TAG_ABYSS_MUDS, root);
    }

    /** 仅在同维度服务器侧调用 */
    public static void discardMudIfPresent(ServerLevel level, UUID uuid) {
        Entity e = level.getEntity(uuid);
        if (e instanceof AbyssMudEntity mud) {
            mud.discard();
        }
    }
}
