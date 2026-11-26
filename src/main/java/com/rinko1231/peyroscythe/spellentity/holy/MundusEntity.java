package com.rinko1231.peyroscythe.spellentity.holy;

import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;


public class MundusEntity extends Projectile implements AntiMagicSusceptible {

    /**
     * 效果等级上限。Iron's 的饰品/buff 可以把法术等级顶到配置上限以上，
     * 所以"世界"的搜索半径、剩余次数、旅者等级统一在 5 级封顶，避免无限成长。
     */
    public static final int MAX_EFFECTIVE_LEVEL = 5;

    private static final int BASE_LIFETIME_TICKS = 300;
    private static final int LIFETIME_PER_EXTRA_USE = 100;

    private static final EntityDataAccessor<Float> DATA_RADIUS =
            SynchedEntityData.defineId(MundusEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_SPELL_LEVEL =
            SynchedEntityData.defineId(MundusEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_USES =
            SynchedEntityData.defineId(MundusEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_MAX_USES =
            SynchedEntityData.defineId(MundusEntity.class, EntityDataSerializers.INT);

    private List<Entity> trackingEntities = new ArrayList<>();

    public MundusEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public MundusEntity(Level level, LivingEntity owner) {
        this((EntityType<? extends Projectile>) EntityRegistry.MUNDUS.get(), level);
        this.setOwner(owner);
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
    }

    @Override
    public void refreshDimensions() {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        super.refreshDimensions();
        this.setPos(x, y, z);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, this.getRadius() * 2.0F);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_RADIUS, 1.0F);
        this.getEntityData().define(DATA_SPELL_LEVEL, 1);
        this.getEntityData().define(DATA_USES, 1);
        this.getEntityData().define(DATA_MAX_USES, 1);
    }

    public void setSpellLevel(int level) {
        int clamped = Math.min(Math.max(1, level), MAX_EFFECTIVE_LEVEL);
        this.entityData.set(DATA_SPELL_LEVEL, clamped);
        if (!this.level().isClientSide) {
            this.entityData.set(DATA_MAX_USES, clamped);
            this.entityData.set(DATA_USES, clamped);
        }
    }

    public int getSpellLevel() {
        return this.entityData.get(DATA_SPELL_LEVEL);
    }

    public int getUses() {
        return Math.min(MAX_EFFECTIVE_LEVEL, Math.max(0, this.entityData.get(DATA_USES)));
    }

    public void setUses(int uses) {
        this.entityData.set(DATA_USES, Math.min(Math.max(0, uses), MAX_EFFECTIVE_LEVEL));
    }

    public int getMaxUses() {
        return Math.min(MAX_EFFECTIVE_LEVEL, Math.max(1, this.entityData.get(DATA_MAX_USES)));
    }

    public void setMaxUses(int maxUses) {
        this.entityData.set(DATA_MAX_USES, Math.min(Math.max(1, maxUses), MAX_EFFECTIVE_LEVEL));
    }

    public boolean consumeUse() {
        if (this.level().isClientSide) {
            return false;
        }
        int left = this.getUses() - 1;
        if (left > 0) {
            this.setUses(left);
            return false;
        }
        this.setUses(0);
        this.exhaust();
        return true;
    }

    //消散
    private void exhaust() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    this.getX(), this.getY() + this.getBbHeight() * 0.3D, this.getZ(),
                    36, 0.35D, 0.35D, 0.35D, 0.03D);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    this.getX(), this.getY() + this.getBbHeight() * 0.3D, this.getZ(),
                    18, 0.25D, 0.25D, 0.25D, 0.05D);
            this.playSound(SoundRegistry.HOLY_CAST.get(), this.getRadius() / 2.0F, 1.0F);
        }
        this.discard();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DATA_RADIUS.equals(key)) {
            this.refreshDimensions();
            if (this.getRadius() < 0.1F) {
                this.discard();
            }
        }
        super.onSyncedDataUpdated(key);
    }

    public void setRadius(float radius) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Math.min(radius, 3.0F));
        }
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Radius", this.getRadius());
        tag.putInt("Age", this.tickCount);
        tag.putInt("SpellLevel", this.getSpellLevel());
        tag.putInt("Uses", this.getUses());
        tag.putInt("MaxUses", this.getMaxUses());
        super.addAdditionalSaveData(tag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.tickCount = tag.getInt("Age");
        if (tag.getInt("Radius") > 0) {
            this.setRadius(tag.getFloat("Radius"));
        }
        if (tag.contains("SpellLevel")) {
            this.entityData.set(DATA_SPELL_LEVEL,
                    Math.min(Math.max(1, tag.getInt("SpellLevel")), MAX_EFFECTIVE_LEVEL));
        }
        if (tag.contains("MaxUses")) {
            this.entityData.set(DATA_MAX_USES,
                    Math.min(Math.max(1, tag.getInt("MaxUses")), MAX_EFFECTIVE_LEVEL));
        }
        if (tag.contains("Uses")) {
            this.entityData.set(DATA_USES, Math.min(Math.max(0, tag.getInt("Uses")), MAX_EFFECTIVE_LEVEL));
        }
        super.readAdditionalSaveData(tag);
    }

    @Override
    public void tick() {
        super.tick();

        int updateInterval = Math.max((int) (this.getRadius() / 2.0F), 2);
        if (this.tickCount % updateInterval == 0) {
            this.updateTrackingEntities();
        }

        // 领域内的生物获得"旅者"标记
        AABB box = this.getBoundingBox();
        float radius = (float) box.getXsize();
        Vec3 center = box.getCenter();
        for (Entity entity : this.trackingEntities) {
            if (entity instanceof LivingEntity living
                    && center.distanceTo(entity.position()) <= radius) {
                living.addEffect(new MobEffectInstance(
                        MobEffectRegistry.VIATOR_MUNDI.get(),
                        20,
                        this.getSpellLevel() - 1,
                        false,
                        false,
                        false));
            }
        }

        if (this.level().isClientSide) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) this.level();
        int lifetime = BASE_LIFETIME_TICKS + LIFETIME_PER_EXTRA_USE * (this.getMaxUses() - 1);
        if (this.tickCount > lifetime) {
            this.exhaust();
            return;
        }

        // 缓慢绕圈的金色光点（贴合新的小尺寸视觉）
        if (this.tickCount % 4 == 0) {
            double angle = this.tickCount * 0.35D;
            double r = 0.45D;
            double y = this.getY() + this.getBbHeight() * 0.3D
                    + Math.sin(this.tickCount * 0.15D) * 0.12D;
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    this.getX() + Math.cos(angle) * r, y, this.getZ() + Math.sin(angle) * r,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        if (this.tickCount % 60 == 20) {
            this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 0.7F, 1.8F);
        }
    }

    private void updateTrackingEntities() {
        this.trackingEntities = this.level().getEntities(this, this.getBoundingBox().inflate(1.0D));
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }
}
