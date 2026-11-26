package com.rinko1231.peyroscythe.utils.mundus;

import com.mojang.datafixers.util.Pair;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.init.PeyroMessages;
import com.rinko1231.peyroscythe.init.TagsRegistry;
import com.rinko1231.peyroscythe.network.spell.ClientboundFlashPacket;
import com.rinko1231.peyroscythe.spellentity.holy.MundusEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * "世界"（Mundus）的传送流程。
 * <p>
 * 流程：检索最近的村庄结构 -> 找安全落点 -> 向玩家发送白屏闪光 ->
 * 在屏幕最白时执行传送 -> 目的地施加反胃（眩晕）并播放音效。
 * 整个过程中玩家看不到地点突变。
 */
public final class TeleportHelper {

    private static final int FADE_IN_TICKS = 14;
    private static final int HOLD_TICKS = 8;
    private static final int FADE_OUT_TICKS = 45;

    private static final int WHITE = 0xFFFFFDF5; // 微暖的白，不是死白

    private TeleportHelper() {
    }

    /**
     * 把玩家传送到最近的村庄结构。
     *
     * @param orb 触发传送的"世界"小球，可为 null（此时仅传送、不消耗小球）
     * @return 是否成功找到目的地并发起传送
     */
    public static boolean tpToNearestVillage(ServerPlayer player, int searchRadius, @Nullable MundusEntity orb) {
        ServerLevel level = player.serverLevel();
        BlockPos start = player.blockPosition();

        HolderSet<Structure> villages = level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getTag(TagsRegistry.MUNDUS_VILLAGE)
                .orElse(null);

        if (villages == null || villages.size() == 0) {
            player.displayClientMessage(Component.translatable("message.peyroscythe.mundus.failed.empty"), false);
            return false;
        }

        Pair<BlockPos, Holder<Structure>> result = level.getChunkSource()
                .getGenerator()
                .findNearestMapStructure(level, villages, start, searchRadius, false);

        if (result == null) {
            player.displayClientMessage(Component.translatable("message.peyroscythe.mundus.failed.not_found"), false);
            return false;
        }

        BlockPos villagePos = result.getFirst();
        BlockPos target = findSafeLanding(level, villagePos);
        if (target == null) {
            target = villagePos.above(64);
        }

        final boolean flash = PeyroScytheConfig.mundusFlashbanged.get();
        if (flash) {
            PeyroMessages.sendToPlayer(
                    new ClientboundFlashPacket(FADE_IN_TICKS, HOLD_TICKS, FADE_OUT_TICKS, WHITE), player);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.2F, 1.6F);
        player.displayClientMessage(Component.translatable("message.peyroscythe.mundus.success"), false);

        // 消耗小球：在原地炸开一圈圣光
        if (orb != null && !orb.isRemoved()) {
            Vec3 p = orb.position();
            level.sendParticles(ParticleTypes.END_ROD, p.x, p.y + 0.3D, p.z, 48, 0.7D, 0.7D, 0.7D, 0.02D);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y + 0.3D, p.z, 24, 0.5D, 0.5D, 0.5D, 0.06D);
            orb.discard();
        }
        player.removeEffect(MobEffectRegistry.VIATOR_MUNDI.get());

        final double tx = target.getX() + 0.5D;
        final double ty = target.getY();
        final double tz = target.getZ() + 0.5D;
        final int delay = flash ? FADE_IN_TICKS + HOLD_TICKS : 0;

        MinecraftServer server = player.server;
        server.tell(new TickTask(server.getTickCount() + delay, () -> {
            if (player.hasDisconnected() || player.isRemoved()) {
                return;
            }

            player.teleportTo(level, tx, ty, tz, player.getYRot(), player.getXRot());

            // 到达后的眩晕：原版反胃自带画面扭曲，同时关掉药水粒子避免出戏
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false, true));

            level.playSound(null, tx, ty, tz, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 1.0F, 1.1F);
            level.sendParticles(ParticleTypes.PORTAL, tx, ty + 1.0D, tz, 40, 0.5D, 0.8D, 0.5D, 0.15D);
        }));

        return true;
    }

    /**
     * 以结构中心为基准，从中心向外一圈圈找"脚下实心、头顶两格空气、非危险方块"的落点。
     */
    private static BlockPos findSafeLanding(ServerLevel level, BlockPos center) {
        for (int radius = 0; radius <= 8; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }
                    BlockPos feet = level.getHeightmapPos(
                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                            center.offset(dx, 0, dz));
                    if (isSafeStanding(level, feet)) {
                        return feet;
                    }
                }
            }
        }
        return null;
    }

    private static boolean isSafeStanding(ServerLevel level, BlockPos feet) {
        if (!level.isEmptyBlock(feet) || !level.isEmptyBlock(feet.above())) {
            return false;
        }

        BlockPos groundPos = feet.below();
        BlockState ground = level.getBlockState(groundPos);

        if (ground.isAir() || !ground.isSolid()) {
            return false;
        }
        if (!ground.getFluidState().isEmpty()) {
            return false;
        }
        return !ground.is(Blocks.LAVA)
                && !ground.is(Blocks.FIRE)
                && !ground.is(Blocks.SOUL_FIRE)
                && !ground.is(Blocks.MAGMA_BLOCK)
                && !ground.is(Blocks.CACTUS)
                && !ground.is(Blocks.POWDER_SNOW)
                && !ground.is(Blocks.CAMPFIRE)
                && !ground.is(Blocks.SOUL_CAMPFIRE);
    }
}
