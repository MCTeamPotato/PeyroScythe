package com.rinko1231.peyroscythe.utils.mundus;

import com.mojang.datafixers.util.Pair;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.init.TagsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.levelgen.structure.Structure;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class TeleportHelper {

    /**
     * 将玩家传送到最近的一个“村庄”结构中心附近。
     */
    public static void tpToNearestVillage(ServerPlayer player, int searchRadius) {
        ServerLevel level = player.serverLevel();
        BlockPos start = player.blockPosition();

        // 获取村庄结构集合
       HolderSet<Structure> villages = level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getTag(TagsRegistry.MUNDUS_VILLAGE)
                .orElse(null);



        if (villages == null || villages.size()==0) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("message.peyroscythe.mundus.failed.empty"),
                    false
            );
            return;
        }

        // 搜索最近村庄
        Pair<BlockPos, Holder<Structure>> result = level.getChunkSource()
                .getGenerator()
                .findNearestMapStructure(level, villages, start, searchRadius, false);

        if (result == null) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("message.peyroscythe.mundus.failed.not_found"),
                    false
            );
            return;
        }

        BlockPos villagePos = result.getFirst();

        // 从海平面开始往上查找可落点
        BlockPos target = findSafeLanding(level, villagePos);

        // fallback：如果完全没找到安全点，就硬放在村庄坐标的上方
        if (target == null) {
            target = villagePos.above(64); // 默认抬高 64 格
        }

        player.addEffect(new MobEffectInstance(
                MobEffects.CONFUSION, 200, 4, false, true)); // 3秒
        if (PeyroScytheConfig.mundusFlashbanged.get())
           player.addEffect(new MobEffectInstance(
                MobEffectRegistry.FLASHBANGED.get(), 120, 0, false, true));

        double destinationX =target.getX() + 0.5;
        double destinationY =target.getY();
        double destinationZ =target.getZ() + 0.5;
        int delay = 60; // 3秒，单位tick
        player.getServer().execute(() -> {
            player.getServer().tell(new TickTask((int) (player.level().getGameTime() + delay), () -> {

                player.teleportTo(level,
                        destinationX, destinationY, destinationZ,
                        player.getYRot(), player.getXRot());

            }));
        });

        player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.peyroscythe.mundus.success"), false);
    }

    public static void tpToNearestVillage(ServerPlayer player) {
        tpToNearestVillage(player, 128);
    }

    public static void tpToNearestMundusSupport(ServerPlayer player, int searchRadius) {
        ServerLevel level = player.serverLevel();
        BlockPos start = player.blockPosition();

        // 获取村庄结构集合
        HolderSet<Structure> mundusSupport = level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getTag(TagsRegistry.MUNDUS_SUPPORT)
                .orElse(null);



        if (mundusSupport == null || mundusSupport.size()==0) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("message.peyroscythe.mundus.failed.empty"),
                    false
            );
            return;
        }

        // 搜索最近村庄
        Pair<BlockPos, Holder<Structure>> result = level.getChunkSource()
                .getGenerator()
                .findNearestMapStructure(level, mundusSupport, start, searchRadius, false);

        if (result == null) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("message.peyroscythe.mundus.failed.not_found"),
                    false
            );
            return;
        }

        BlockPos villagePos = result.getFirst();

        // 从海平面开始往上查找可落点
        BlockPos target = findSafeLanding(level, villagePos);

        // fallback：如果完全没找到安全点，就硬放在村庄坐标的上方
        if (target == null) {
            target = villagePos.above(64); // 默认抬高 64 格
        }
        player.addEffect(new MobEffectInstance(
                MobEffects.CONFUSION, 200, 4, false, true)); // 3秒
        if (PeyroScytheConfig.mundusFlashbanged.get())
            player.addEffect(new MobEffectInstance(
                    MobEffectRegistry.FLASHBANGED.get(), 120, 0, false, true));

        double destinationX =target.getX() + 0.5;
        double destinationY =target.getY();
        double destinationZ =target.getZ() + 0.5;
        int delay = 60; // 3秒，单位tick
        player.getServer().execute(() -> {
            player.getServer().tell(new TickTask((int) (player.level().getGameTime() + delay), () -> {

                player.teleportTo(level,
                        destinationX, destinationY, destinationZ,
                        player.getYRot(), player.getXRot());

            }));
        });

        player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.peyroscythe.mundus.success"), false);
    }

    public static void tpToNearestMundusSupport(ServerPlayer player) {
        tpToNearestMundusSupport(player, 128);
    }

    /**
     * 从海平面开始往上查找可落点。
     * 规则：方块是 solid，头顶两格空气；否则向上查找。
     * 如果第一个点危险，就在XZ周围找安全点。
     */
    private static BlockPos findSafeLanding(ServerLevel level, BlockPos center) {
        int seaLevel = level.getSeaLevel();
        int maxY = level.getMaxBuildHeight();

        // A点：从海平面往上
        for (int y = seaLevel; y < maxY; y++) {
            BlockPos candidate = new BlockPos(center.getX(), y, center.getZ());
            if (isSafeSpot(level, candidate)) {
                return candidate.above(); // 落点放在可站立方块上方
            }
        }

        // 如果直线没找到，就在周围 5x5 搜索
        int radius = 5;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int y = seaLevel; y < maxY; y++) {
                    BlockPos candidate = new BlockPos(center.getX() + dx, y, center.getZ() + dz);
                    if (isSafeSpot(level, candidate)) {
                        return candidate.above();
                    }
                }
            }
        }

        return null; // fallback 由调用者处理
    }

    /**
     * 判断某点是否安全：
     * - 当前方块是 solid
     * - 上面两格是空气
     * - 当前方块不是危险方块（岩浆、火、仙人掌）
     */
    private static boolean isSafeSpot(ServerLevel level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();

        // 脚下要能站立
        if (!block.defaultBlockState().isSolid()) return false;

        // 避免危险方块
        if (block == Blocks.LAVA || block == Blocks.FIRE || block == Blocks.CACTUS) return false;

        // 头顶要空气
        return level.isEmptyBlock(pos.above()) && level.isEmptyBlock(pos.above(2));
    }
}
