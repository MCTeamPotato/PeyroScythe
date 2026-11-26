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

/**
 * "世界"（Mundus）的投影：一颗悬浮、旋转、发光的圣物小球。
 * <p>
 * 它本身不做传送，只负责：
 * 1. 标记施法位置（玩家必须靠近它才能念咒）；
 * 2. 给领域内的生物挂上"旅者"标记；
 * 3. 播放光环粒子/音效，存活 15 秒后消散。
 */
public class MundusEntity extends Projectile implements AntiMagicSusceptible {

    private static final int LIFETIME_TICKS = 300;

    private static final EntityDataAccessor<Float> DATA_RADIUS =
            SynchedEntityData.defineId(MundusEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> SPELL_LEVEL =
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
        this.getEntityData().define(SPELL_LEVEL, 1);
    }

    public void setSpellLevel(int level) {
        this.entityData.set(SPELL_LEVEL, level);
    }

    public int getSpellLevel() {
        return this.entityData.get(SPELL_LEVEL);
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
        super.addAdditionalSaveData(tag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.tickCount = tag.getInt("Age");
        if (tag.getInt("Radius") > 0) {
            this.setRadius(tag.getFloat("Radius"));
        }
        if (tag.contains("SpellLevel")) {
            this.setSpellLevel(tag.getInt("SpellLevel"));
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

        // 领域内的生物获得"旅者"标记（无粒子、无图标，仅用于氛围/兼容旧逻辑）
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

        if (this.tickCount > LIFETIME_TICKS) {
            this.discard();
            this.playSound(SoundRegistry.HOLY_CAST.get(), this.getRadius() / 2.0F, 1.0F);
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    this.getX(), this.getY() + this.getRadius() * 0.5D, this.getZ(),
                    40, 0.6D, 0.6D, 0.6D, 0.02D);
            return;
        }

        // 缓慢绕圈的金色光点
        if (this.tickCount % 4 == 0) {
            double angle = this.tickCount * 0.35D;
            double r = this.getRadius() * 0.9D;
            double y = this.getY() + this.getRadius() * 0.45D
                    + Math.sin(this.tickCount * 0.15D) * 0.25D;
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
