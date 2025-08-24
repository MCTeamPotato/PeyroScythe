package com.rinko1231.peyroscythe.spellentity.holy;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class GoldenBellEntity extends Projectile implements AntiMagicSusceptible {
    private static final EntityDataAccessor<Float> DATA_RADIUS =
            SynchedEntityData.defineId(GoldenBellEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> SPELL_LEVEL =
            SynchedEntityData.defineId(GoldenBellEntity.class, EntityDataSerializers.INT);

    private List<Entity> trackingEntities = new ArrayList<>();
    private int duration = 320;

    public GoldenBellEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public GoldenBellEntity(Level level, LivingEntity owner) {
        this((EntityType<? extends Projectile>) EntityRegistry.GOLDEN_BELL.get(), level);
        this.setOwner(owner);
    }

    // —— AntiMagic —— //
    @Override
    public void onAntiMagic(MagicData playerMagicData) {}

    // —— 生命周期/尺寸 —— //
    public void setDuration(int d) { this.duration = d; }
    public int getDuration() { return this.duration; }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_RADIUS, 5.0F);
        this.entityData.define(SPELL_LEVEL, 1);
    }

    public void setSpellLevel(int s) { this.entityData.set(SPELL_LEVEL, s); }
    public int getSpellLevel() { return this.entityData.get(SPELL_LEVEL); }

    public void setRadius(float r) {
        if (!this.level().isClientSide) {
            this.entityData.set(DATA_RADIUS, Math.min(r, PeyroScytheConfig.holyBellRadiusCap.get().floatValue()));
        }
    }
    public float getRadius() { return this.entityData.get(DATA_RADIUS); }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DATA_RADIUS.equals(key)) {
            this.refreshDimensions();
            if (this.getRadius() < 0.1F) this.discard();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public void refreshDimensions() {
        double x = this.getX(), y = this.getY(), z = this.getZ();
        super.refreshDimensions();
        this.setPos(x, y, z);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        // 立方体：边长 = 2 * radius
        return EntityDimensions.scalable(this.getRadius() * 2.0F, this.getRadius() * 2.0F);
    }

    // —— 存档 —— //
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Radius", this.getRadius());
        tag.putInt("Age", this.tickCount);
        tag.putInt("SpellLevel", getSpellLevel());
        super.addAdditionalSaveData(tag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.tickCount = tag.getInt("Age");
        if (tag.contains("Radius")) this.setRadius(tag.getFloat("Radius"));
        if (tag.contains("SpellLevel")) setSpellLevel(tag.getInt("SpellLevel"));
        super.readAdditionalSaveData(tag);
    }

    // —— Tick —— //
    @Override
    public void tick() {
        super.tick();

        // 定期刷新候选实体（用圆的外接AABB）
        int refresh = Math.max((int)(this.getRadius() / 2.0F), 2);
        if (this.tickCount % refresh == 0) {
            this.updateTrackingEntities();
        }

        // 命中判定：底面正方形的内切圆（圆柱体）
        AABB box = this.getBoundingBox();
        Vec3 center = box.getCenter();
        final double r = this.getRadius();
        final double rSq = r * r;
        final double minY = box.minY, maxY = box.maxY;

        for (Entity e : this.trackingEntities) {
            if (!(e instanceof LivingEntity living)) continue;
            if (e == this.getOwner()) continue;
            if (DamageSources.isFriendlyFireBetween(this.getOwner(), e)) continue;


            double ey = e.getBoundingBox().getCenter().y;
            if (ey < minY || ey > maxY) continue;


            double dx = e.getX() - center.x;
            double dz = e.getZ() - center.z;
            if (dx * dx + dz * dz > rSq) continue;



            living.addEffect(new MobEffectInstance(
                    MobEffectRegistry.HOLY_BELL_SUPPRESSION.get(),
                    40, // 2s
                    Math.max(0, this.getSpellLevel() - 1),
                    false,
                    true
            ));
        }

        // 声音 & 粒子（仅服务端）
        if (!this.level().isClientSide) {
            if (this.tickCount > this.getDuration())  {
                this.discard();
                this.playSound((SoundEvent) SoundRegistry.BLACK_HOLE_CAST.get(), this.getRadius() / 2.0F, 1.0F);
                // 这个 MagicManager 是服务端实现，安全
                MagicManager.spawnParticles(this.level(), ParticleHelper.UNSTABLE_ENDER,
                        this.getX(), this.getY() + this.getRadius(), this.getZ(),
                        200, 1.0, 1.0, 1.0, 1.0, true);
            } else if ((this.tickCount - 1) % 30 == 0) {
                this.playSound(SoundEvents.BELL_BLOCK, 1.5f, 0.3f);
            }
        }
    }

    //候选实体检索：用“内切圆”的外接AABB
    private void updateTrackingEntities() {
        AABB box = this.getBoundingBox();
        Vec3 c = box.getCenter();
        double r = this.getRadius();
        AABB search = new AABB(
                c.x - r, box.minY, c.z - r,
                c.x + r, box.maxY, c.z + r
        ).inflate(0.25); // 轻微外扩，避免边界误差
        this.trackingEntities = this.level().getEntities(this, search);
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }
}
