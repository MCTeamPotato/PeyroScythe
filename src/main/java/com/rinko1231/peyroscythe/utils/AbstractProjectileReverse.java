package com.rinko1231.peyroscythe.utils;

import io.redspace.ironsspellbooks.api.entity.NoKnockbackProjectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractProjectileReverse extends Projectile implements NoKnockbackProjectile {
    protected static final int FAILSAFE_EXPIRE_TIME = 400;
    protected int age;
    protected float damage;
    protected boolean dealDamageActive;


    public AbstractProjectileReverse(EntityType<? extends AbstractProjectileReverse> entityType, Level level, LivingEntity entity) {
        this(entityType, level);
        this.setOwner(entity);
    }

    public AbstractProjectileReverse(EntityType<? extends AbstractProjectileReverse> entityType, Level level) {
        super(entityType, level);
        this.dealDamageActive = true;
        this.noPhysics = true;
        this.blocksBuilding = false;
    }


    public boolean isOnFire() {
        return false;
    }

    public abstract void spawnParticles();

    public boolean shouldBeSaved() {
        return false;
    }

    protected abstract void onHitEntity(EntityHitResult var1);

    public void setId(int id) {
        super.setId(id);

    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        super.tick();
        if (++this.age > FAILSAFE_EXPIRE_TIME) {
            this.discard();
            return;
        }

        Entity owner = this.getOwner();
        if (owner != null) {
            // 玩家视线方向
            Vec3 look = owner.getLookAngle().normalize();

            // 反方向 * 距离 → 背后偏移
            double distanceBehind = 1.0; // 可以调，比如 1 格背后
            Vec3 offset = look.scale(-distanceBehind);

            // 玩家眼睛位置
            Vec3 ownerEyePos = owner.getEyePosition(1.0F);

            // 最终位置 = 眼睛位置 + 背后偏移 + 下移微调
            Vec3 finalPos = ownerEyePos.add(offset).add(0, -2.0, 0);

            this.setPos(finalPos);

            // 保持旋转和主人一致
            this.setXRot(owner.getXRot());
            this.setYRot(owner.getYRot());
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
    }


    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("Damage", this.damage);
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.damage = pCompound.getFloat("Damage");
    }
}
