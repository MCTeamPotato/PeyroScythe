package com.rinko1231.peyroscythe.spellentity;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.VisualFallingBlockEntity;
import io.redspace.ironsspellbooks.entity.spells.ice_block.IceBlockProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GlacierIceBlockProjectile extends IceBlockProjectile {

    int airTime;
    private List<Entity> victims;
    private final AnimatableInstanceCache cache;

    private static final float BASE_WIDTH  = 1.0F;
    private static final float BASE_HEIGHT = 1.0F;
    private static final float BASE_IMPACT_RADIUS = 3.5F;
    private static final EntityDataAccessor<Float> DATA_SCALE =
            SynchedEntityData.defineId(GlacierIceBlockProjectile.class, EntityDataSerializers.FLOAT);
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SCALE, 1.0F);
    }

    public void setScaleFactor(float s) {
        this.entityData.set(DATA_SCALE, s);
        refreshAndRealignAABB();
    }

    public float getScaleFactor() {
        return this.entityData.get(DATA_SCALE);
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("ScaleFactor", getScaleFactor());
    }
    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_SCALE.equals(key)) {
            refreshAndRealignAABB();
        }
    }


    private void refreshAndRealignAABB() {

        this.refreshDimensions();

        //在当前位置生成匹配的盒子
        EntityDimensions dims = this.getDimensions(this.getPose());
        AABB box = dims.makeBoundingBox(this.getX(), this.getY(), this.getZ());
        this.setBoundingBox(box);


        this.reapplyPosition();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ScaleFactor")) {
            setScaleFactor(tag.getFloat("ScaleFactor"));
            this.refreshDimensions(); // ← 再保险
        }
    }
    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        float s = getScaleFactor();
        return EntityDimensions.scalable(BASE_WIDTH * s *1.5f , BASE_HEIGHT * s*1.5f);
    }

    public GlacierIceBlockProjectile(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.victims = new ArrayList<>();
        this.setNoGravity(true);
        this.refreshDimensions();
    }
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
    // 强制使用“巨大版”
    public GlacierIceBlockProjectile(Level level, LivingEntity owner, @Nullable LivingEntity target) {
        this(com.rinko1231.peyroscythe.init.EntityRegistry.GLACIER_ICE_BLOCK_PROJECTILE.get(), level);
        this.setOwner(owner);
        if (target != null) this.setTarget(target);
        this.setNoGravity(true);
        this.refreshDimensions();
    }


    public void tick() {
        this.firstTick = false;
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

        if (!this.level().isClientSide) {



            if (this.airTime <= 0) {
                if (this.onGround()) {
                    this.doImpactDamage();
                    this.playSound(SoundRegistry.ICE_BLOCK_IMPACT.get(), 2.5F, 0.8F + this.random.nextFloat() * 0.4F);
                    if (PeyroScytheConfig.glacierFallShowParticles.get())
                    {
                        this.spawnImpactExplosionFX(this.getScaleFactor());
                        //震出冰环
                        spawnIceConcentric(this.level(), this.position(), 1.2F * (this.getScaleFactor()), (this.getScaleFactor()));
                    }
                    this.impactParticles(this.getX(), this.getY(), this.getZ());
                    this.discard();
                } else {
                    this.level().getEntities(this, this.getBoundingBox().inflate(0.35)).forEach(this::doFallingDamage);
                }
            }

            if (this.airTime-- > 0) {
                boolean tooHigh = false;
                this.setDeltaMovement(this.getDeltaMovement().multiply((double)0.95F, (double)0.75F, (double)0.95F));
                if (this.getTarget() != null) {
                    Entity target = this.getTarget();
                    Vec3 diff = target.position().subtract(this.position());
                    if (diff.horizontalDistanceSqr() > (double)1.0F) {
                        this.setDeltaMovement(this.getDeltaMovement().add(diff.multiply((double)1.0F, (double)0.0F, (double)1.0F).normalize().scale((double)0.025F)));
                    }

                    if (this.getY() - target.getY() > (double)3.5F) {
                        tooHigh = true;
                    }
                } else if (this.airTime % 3 == 0) {
                    HitResult ground = Utils.raycastForBlock(this.level(), this.position(), this.position().subtract((double)0.0F, (double)3.5F, (double)0.0F), ClipContext.Fluid.ANY);
                    if (ground.getType() == HitResult.Type.MISS) {
                        tooHigh = true;
                    } else if (Math.abs(this.position().y - ground.getLocation().y) < (double)4.0F) {
                    }
                }

                if (tooHigh) {
                    this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, -0.005, (double)0.0F));
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, 0.01, (double)0.0F));
                }

                if (this.airTime == 0) {
                    this.setDeltaMovement((double)0.0F, (double)0.5F, (double)0.0F);
                }
            } else {
                this.setDeltaMovement((double)0.0F, this.getDeltaMovement().y - 0.15, (double)0.0F);
            }
        } else {
            this.trailParticles();
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void spawnImpactExplosionFX(float scale) {
        if (!(this.level() instanceof ServerLevel sl)) return;

        // 半径与等级/体型挂钩：建议与伤害半径一致
        final float baseR = 1.5F;
        final double R = baseR * scale;

        final double x0 = this.getX();
        final double z0 = this.getZ();

        // 采样数量 ~ 面积；密度可微调，上限防炸机
        final double density = 1.1;           // 每格²多少个点（1.0~1.5观感较好）
        int N = (int)Math.min(120, Math.max(12, density * R * R));

        final double GOLDEN_ANGLE = Math.PI * (3 - Math.sqrt(5));

        for (int i = 0; i < N; i++) {
            // Vogel 螺旋：半径按 sqrt(t) 均匀扩张，角度按黄金角旋进
            double t = (i + 0.5) / N;
            double a = i * GOLDEN_ANGLE;
            double r = Math.sqrt(t) * R;
            double cx = x0 + Math.cos(a) * r;
            double cz = z0 + Math.sin(a) * r;

            // 贴地：高度图更快，也可以换成向下射线
            int gx = Mth.floor(cx);
            int gz = Mth.floor(cz);
            int gy = sl.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gx, gz);
            double cy = gy + 0.1;

            // 小爆点（主体）
            sl.sendParticles(ParticleTypes.EXPLOSION, cx, cy, cz, 1, 0, 0, 0, 0);

            // 偶尔大
            if ((i % Math.max(6, (int)(10 - scale * 2))) == 0) {
                sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, cx, cy, cz, 1, 0, 0, 0, 0);
            }

            // 气浪+霜雾（向外推一点速度，随半径衰减一点）
            double dirx = (cx - x0);
            double dirz = (cz - z0);
            double len = Math.max(0.0001, Math.hypot(dirx, dirz));
            double vx = (dirx / len) * (0.25 + 0.15 * scale);
            double vz = (dirz / len) * (0.25 + 0.15 * scale);

            sl.sendParticles(ParticleTypes.CLOUD, cx, cy + 0.05, cz, 1, 0, 0, 0, 0);
            sl.sendParticles(ParticleTypes.POOF,  cx, cy + 0.05, cz, 1, vx, 0.01, vz, 0.0);

            // 冰尘点缀
            if (this.random.nextFloat() < 0.6F) {
                sl.sendParticles(ParticleTypes.SNOWFLAKE, cx, cy + 0.2, cz, 1, 0, 0.02, 0, 0.0);
            }
        }
        

        // 中心再补一次主爆
        sl.sendParticles(ParticleTypes.EXPLOSION, x0, this.getY() + 0.1, z0, 1, 0, 0, 0, 0);
        sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x0, this.getY() + 0.1, z0, 1, 0, 0, 0, 0);
        sl.levelEvent(2001, this.blockPosition().below(), Block.getId(Blocks.ICE.defaultBlockState()));
    }

    public static void spawnIceConcentric(Level level, Vec3 center, float outerRadius, float scale) {
        if (level.isClientSide) return;

        // 环数随体型增长：2~5环
        int rings = Mth.clamp(2 + Math.round(scale), 2, 5);

        // 周向密度（每米多少块），体型越大越密，但做上限保护
        float densityPerMeter = Mth.clamp(0.9f + 0.5f * scale, 0.8f, 1.7f);

        // 上抛强度：体型越大略强
        float baseImpulse = 0.12f + 0.05f * scale;

        // ——同心环——
        for (int j = 1; j <= rings; j++) {
            // 均匀分布在 (0.2R, R]，避免中心太挤：t ∈ (0.2, 1]
            float t = Mth.lerp(j / (float)(rings + 1), 0.2f, 1.0f);
            float r = outerRadius * t;

            // 圆周点数 ≈ 2πr * density
            int count = Mth.clamp((int)(2 * Math.PI * r * densityPerMeter), 6, 256);
            double golden = Math.PI * (3 - Math.sqrt(5));

            for (int i = 0; i < count; i++) {
                double a = i * golden + j * 0.37; // 每环错相
                // 半径添加轻微随机抖动
                float jitter = 0.15f * (0.5f - level.random.nextFloat());
                double rr = Math.max(0.2, r + jitter);
                double cx = center.x + Math.cos(a) * rr;
                double cz = center.z + Math.sin(a) * rr;

                BlockPos ground = findGround(level, new BlockPos(Mth.floor(cx), Mth.floor(center.y), Mth.floor(cz)), 8);
                if (ground == null) continue;

                BlockPos spawnAt = ground.above();
                if (!level.getBlockState(spawnAt).isAir()) continue;
                float impulse = baseImpulse * (0.95f + level.random.nextFloat() * 0.15f);

                // 一块或两块叠放（偶尔）
                createFalling(level, spawnAt, pickIceState(level.random), impulse);
                if (level.random.nextFloat() < 0.20f) {
                    createFalling(level, spawnAt.above(), pickIceState(level.random), impulse * 0.9f);
                }
            }
        }

        // 内区稀疏填充（非外边缘）
        int interiorN = Mth.clamp((int)(outerRadius * outerRadius * 0.35f), 8, 120);
        double golden = Math.PI * (3 - Math.sqrt(5));
        for (int k = 0; k < interiorN; k++) {
            double t = (k + 0.5) / interiorN;
            double r = Math.sqrt(t) * outerRadius * 0.85; // 只到 0.85R，留给外环展示
            double a = k * golden + 1.23;
            double cx = center.x + Math.cos(a) * r;
            double cz = center.z + Math.sin(a) * r;

            BlockPos ground = findGround(level, new BlockPos(Mth.floor(cx), Mth.floor(center.y), Mth.floor(cz)), 8);
            if (ground == null) continue;

            BlockPos spawnAt = ground.above();
            if (!level.getBlockState(spawnAt).isAir()) continue;

            float impulse = (0.9f + level.random.nextFloat() * 0.2f) * (0.10f + 0.04f * scale);
            createFalling(level, spawnAt, pickIceState(level.random), impulse);
        }
    }

    private static BlockPos findGround(Level level, BlockPos start, int maxDown) {
        BlockPos p = start;
        for (int i = 0; i < maxDown; i++) {
            if (!level.getBlockState(p).isAir()) {
                // 要求顶部可承托（避免树叶等）
                if (level.getBlockState(p).isFaceSturdy(level, p, Direction.UP)) return p;
            }
            p = p.below();
        }
        return null;
    }

    private static BlockState pickIceState(RandomSource r) {
        float f = r.nextFloat();
        if (f < 0.12f) return Blocks.BLUE_ICE.defaultBlockState();
        if (f < 0.55f) return Blocks.PACKED_ICE.defaultBlockState();
        return Blocks.ICE.defaultBlockState();
    }

    private static void createFalling(Level level, BlockPos pos, BlockState state, float impulse) {
        VisualFallingBlockEntity e = new VisualFallingBlockEntity(level, pos.getX(), pos.getY(), pos.getZ(), state, 10);
        e.setDeltaMovement(0.0, impulse, 0.0);
        level.addFreshEntity(e);
    }


    private void doFallingDamage(Entity target) {
        if (!this.level().isClientSide) {

            if (this.canHitEntity(target) && !this.victims.contains(target)) {
                boolean flag = DamageSources.applyDamage(target, this.getDamage() / 2.0F, NewSpellRegistry.GLACIER_SPELL.get().getDamageSource(this, this.getOwner()));

                if (flag) {
                    this.victims.add(target);
                    target.invulnerableTime = 0;
                }

            }
        }
    }


    private void doImpactDamage() {
        float s = getScaleFactor();
        float explosionRadius = 1.5F*s;
        this.level().getEntities(this, this.getBoundingBox().inflate((double)explosionRadius)).forEach((entity) -> {
            if (this.canHitEntity(entity)) {
                double distance = entity.distanceToSqr(this.position());
                if (distance < (double)(explosionRadius * explosionRadius)) {
                    double p = (double)1.0F - Math.pow(Math.sqrt(distance) / (double)explosionRadius, (double)3.0F);
                    float damage = (float)((double)this.damage * p);
                    DamageSources.applyDamage(entity, damage, ((AbstractSpell)NewSpellRegistry.GLACIER_SPELL.get()).getDamageSource(this, this.getOwner()));
                }
            }

        });
    }



    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity hit = result.getEntity();
        if (this.canHitEntity(hit)) {
            // 命中单体伤害
            DamageSources.applyDamage(hit, this.getDamage(),
                    ((AbstractSpell) NewSpellRegistry.GLACIER_SPELL.get()).getDamageSource(this, this.getOwner()));
        }
        // 命中后立即爆裂范围伤害
        this.doImpactDamageLikeParent();
        this.playSound(SoundRegistry.ICE_BLOCK_IMPACT.get(), 2.5F, 0.8F + this.random.nextFloat() * 0.4F);
        this.impactParticles(this.getX(), this.getY(), this.getZ());
        this.discard();
    }


    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        // 命中地面直接爆裂
        this.doImpactDamageLikeParent();
        this.playSound(SoundRegistry.ICE_BLOCK_IMPACT.get(), 2.5F, 0.8F + this.random.nextFloat() * 0.4F);
        this.impactParticles(this.getX(), this.getY(), this.getZ());
        this.discard();
    }


    private void doImpactDamageLikeParent() {
        float explosionRadius = BASE_IMPACT_RADIUS;
        double r2 = explosionRadius * explosionRadius;

        this.level().getEntities(this, this.getBoundingBox().inflate(explosionRadius)).forEach(entity -> {
            if (this.canHitEntity(entity)) {
                double d2 = entity.distanceToSqr(this.position());
                if (d2 < r2) {
                    double p = 1.0 - Math.pow(Math.sqrt(d2) / explosionRadius, 3.0);
                    float damage = (float)(this.getDamage() * p);
                    DamageSources.applyDamage(entity, damage,
                            ((AbstractSpell) NewSpellRegistry.GLACIER_SPELL.get()).getDamageSource(this, this.getOwner()));
                }
            }
        });
    }


}
