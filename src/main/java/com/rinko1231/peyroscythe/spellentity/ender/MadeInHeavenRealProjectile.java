package com.rinko1231.peyroscythe.spellentity.ender;

import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.utils.AbstractProjectileReverse;
import com.rinko1231.peyroscythe.utils.TimeFlowAccelerator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunk.RebindableTickingBlockEntityWrapper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;

import static com.rinko1231.peyroscythe.init.TagsRegistry.MADE_IN_HEAVEN_BLACKLIST;


public class MadeInHeavenRealProjectile extends AbstractProjectileReverse {

    private static final EntityDataAccessor<Integer> SPELL_LEVEL =
            SynchedEntityData.defineId(MadeInHeavenRealProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SPEEDUP_RADIUS =
            SynchedEntityData.defineId(MadeInHeavenRealProjectile.class, EntityDataSerializers.FLOAT);

    public MadeInHeavenRealProjectile(EntityType<? extends AbstractProjectileReverse> entityType, Level level) {
        super(entityType, level);
    }

    public MadeInHeavenRealProjectile(Level level, LivingEntity entity) {
        super(EntityRegistry.MADE_IN_HEAVEN_PROJECTILE.get(), level, entity);
        this.setOwner(entity);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPELL_LEVEL, 1);
        this.entityData.define(SPEEDUP_RADIUS,3.0f);
    }

    public void setSpellLevel(int s) {
        this.entityData.set(SPELL_LEVEL, s);
    }

    public int getSpellLevel() {
        return this.entityData.get(SPELL_LEVEL);
    }

    public void setSpeedUpRadius(float s) {
        this.entityData.set(SPEEDUP_RADIUS, s);
    }
    public void setSpeedUpRadius(double s) {
        this.entityData.set(SPEEDUP_RADIUS, (float)s);
    }

    public float getSpeedUpRadius() {
        return this.entityData.get(SPEEDUP_RADIUS);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SpellLevel", getSpellLevel());
        tag.putFloat("SpeedupRadius", getSpeedUpRadius());
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("SpellLevel")) {
            setSpellLevel(tag.getInt("SpellLevel"));
        }
        if (tag.contains("SpeedupRadius")) {
            setSpeedUpRadius(tag.getFloat("SpeedupRadius"));
        }
    }


    @Override
    public void spawnParticles() {

    }

    public int getFactor(int spellLevel) {
        spellLevel = Mth.clamp(spellLevel, 1, 3);
        return LEVEL_TO_FACTOR[spellLevel];
    }
    // —— 你可以把这个倍率表挪到 config —— //
    private static final int[] LEVEL_TO_FACTOR = {1, 20, 40, 120};


    @Override
    public void tick() {
        super.tick();
        if (this.getOwner() != null && !this.level().isClientSide) {
            int factor = this.getFactor(this.getSpellLevel()); // 举例：20倍昼夜加速
            ServerLevel serverLevel = (ServerLevel) this.level();

            TimeFlowAccelerator.declare(serverLevel, this.getOwner().getUUID(), factor);

            double radius = this.getSpeedUpRadius();
            AABB box = new AABB(
                    this.getX() - radius, this.getY() - radius, this.getZ() - radius,
                    this.getX() + radius, this.getY() + radius, this.getZ() + radius
            );

            speedUpBlockEntities(serverLevel, factor, box);
            speedUpRandomTicks(serverLevel, factor, box);
        }
    }

    private void speedUpBlockEntities(Level level, int bonusTicks, AABB box) {
        if (!(level instanceof ServerLevel serverLevel) || box == null || bonusTicks <= 0) return;

        // 遍历盒子内所有方块位置
        final int minX = Mth.floor(box.minX), minY = Mth.floor(box.minY), minZ = Mth.floor(box.minZ);
        final int maxX = Mth.floor(box.maxX), maxY = Mth.floor(box.maxY), maxZ = Mth.floor(box.maxZ);

        for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            // chunk/边界安全检查
            if (!serverLevel.isLoaded(pos)) continue;

            BlockState state = serverLevel.getBlockState(pos);
            // 黑名单：跳过
            if (state.is(MADE_IN_HEAVEN_BLACKLIST)) continue;

            BlockEntity be = serverLevel.getBlockEntity(pos);
            if (be == null || be.isRemoved()) continue;

            if (!level.shouldTickBlocksAt(ChunkPos.asLong(pos))) continue;

            LevelChunk chunk = serverLevel.getChunkAt(pos);
            // Forge/Mojmap：访问正在 tick 的 BE 包装器
            RebindableTickingBlockEntityWrapper wrapper = chunk.tickersInLevel.get(pos);
            if (wrapper == null || wrapper.isRemoved()) continue;

            // 优先用 BoundTickingBlockEntity 走更快的路径
            if (wrapper.ticker instanceof LevelChunk.BoundTickingBlockEntity tickingBE) {
                if (chunk.isTicking(pos) && be.getType().isValid(state)) {
                    for (int i = 0; i < bonusTicks; i++) {
                        tickingBE.ticker.tick(serverLevel, pos.immutable(), state, be);
                    }
                }
            } else {
                // 兜底：直接多次调用 wrapper.tick()
                for (int i = 0; i < bonusTicks; i++) {
                    wrapper.tick();
                }
            }
        }
    }

    /**
     * 额外 randomTick 范围内的方块（跳过黑名单）
     */
    private void speedUpRandomTicks(Level level, int bonusTicks, AABB box) {
        if (!(level instanceof ServerLevel serverLevel) || box == null || bonusTicks <= 0) return;

        final int minX = Mth.floor(box.minX), minY = Mth.floor(box.minY), minZ = Mth.floor(box.minZ);
        final int maxX = Mth.floor(box.maxX), maxY = Mth.floor(box.maxY), maxZ = Mth.floor(box.maxZ);

        for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (!serverLevel.isLoaded(pos)) continue;

            BlockState state = serverLevel.getBlockState(pos);
            // 黑名单：跳过
            if (state.is(MADE_IN_HEAVEN_BLACKLIST)) continue;

            if (state.isRandomlyTicking()) {
                // 直接多次触发 randomTick
                for (int i = 0; i < bonusTicks; i++) {
                    state.randomTick(serverLevel, pos.immutable(), serverLevel.random);
                }
            }
        }
    }


    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
    }
}