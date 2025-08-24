package com.rinko1231.peyroscythe.spellentity.holy;

import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MundusEntity extends Projectile implements AntiMagicSusceptible {
    private static final EntityDataAccessor<Float> DATA_RADIUS;
    List<Entity> trackingEntities;

    private static final EntityDataAccessor<Integer> SPELL_LEVEL =
            SynchedEntityData.defineId(MundusEntity.class, EntityDataSerializers.INT);

    public MundusEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.trackingEntities = new ArrayList<>();
    }

    public MundusEntity(Level pLevel, LivingEntity owner) {
        this((EntityType) EntityRegistry.MUNDUS.get(), pLevel);
        this.setOwner(owner);
    }

    public void onAntiMagic(MagicData playerMagicData) {
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }


    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, this.getRadius() * 2.0F);
    }

    protected void defineSynchedData() {

        this.getEntityData().define(DATA_RADIUS, 1.0F);
        this.entityData.define(SPELL_LEVEL, 1);
    }
    public void setSpellLevel(int s) {
        this.entityData.set(SPELL_LEVEL, s);
    }

    public int getSpellLevel() {
        return this.entityData.get(SPELL_LEVEL);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_RADIUS.equals(pKey)) {
            this.refreshDimensions();
            if (this.getRadius() < 0.1F) {
                this.discard();
            }
        }
        super.onSyncedDataUpdated(pKey);
    }

    public void setRadius(float pRadius) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Math.min(pRadius, 3.0F));
        }

    }

    public float getRadius() {
        return (Float)this.getEntityData().get(DATA_RADIUS);
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("Radius", this.getRadius());
        pCompound.putInt("Age", this.tickCount);

        pCompound.putInt("SpellLevel", getSpellLevel());
        super.addAdditionalSaveData(pCompound);
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.tickCount = pCompound.getInt("Age");

        if (pCompound.getInt("Radius") > 0) {
            this.setRadius(pCompound.getFloat("Radius"));
        }
        if (pCompound.contains("SpellLevel")) {
            setSpellLevel(pCompound.getInt("SpellLevel"));
        }
        super.readAdditionalSaveData(pCompound);
    }

    public void tick() {
        super.tick();
        int update = Math.max((int)(this.getRadius() / 2.0F), 2);
        if (this.tickCount % update == 0) {
            this.updateTrackingEntities();
        }

        AABB bb = this.getBoundingBox();
        float radius = (float)bb.getXsize();
        boolean hitTick = this.tickCount % 10 == 0;

        for (Entity entity : this.trackingEntities) {
            if (entity instanceof LivingEntity living
                  /*  && entity != this.getOwner()*/
                   /* && !DamageSources.isFriendlyFireBetween(this.getOwner(), entity)*/) {
//一起疯狂
                Vec3 center = bb.getCenter();
                float distance = (float) center.distanceTo(entity.position());
                if (!(distance > radius)) {

                    // 确认施加效果（持续40 tick = 2秒，可调整），等级 = spellLevel - 1
                    MobEffectInstance effect = new MobEffectInstance(
                            MobEffectRegistry.VIATOR_MUNDI.get(),
                            20,
                            this.getSpellLevel() - 1,
                            false,
                            false,
                            false
                    );
                    living.addEffect(effect);
                }
            }
        }


        if (!this.level().isClientSide) {
            if (this.tickCount > 300 )  {
                this.discard();
                this.playSound((SoundEvent) SoundRegistry.HOLY_CAST.get(), this.getRadius() / 2.0F, 1.0F);
                //MagicManager.spawnParticles(this.level(), ParticleHelper.UNSTABLE_ENDER, this.getX(), this.getY() + (double)this.getRadius(), this.getZ(), 200, (double)1.0F, (double)1.0F, (double)1.0F, (double)1.0F, true);
            } else if ((this.tickCount) % 20 == 0) {
                //MagicManager.spawnParticles(this.level(), ParticleHelper.WISP, this.getX(), this.getY() , this.getZ(), 100, (double)1.0F, (double)1.0F, (double)1.0F, (double)1.0F, true);
                //this.playSound((SoundEvent)SoundRegistry.BLACK_HOLE_LOOP.get(), this.getRadius() / 3.0F, 1.0F);
            }
        }

    }

    private void updateTrackingEntities() {
        this.trackingEntities = this.level().getEntities(this, this.getBoundingBox().inflate((double)1.0F));
    }

    public boolean displayFireAnimation() {
        return false;
    }

    static {
        DATA_RADIUS = SynchedEntityData.defineId(MundusEntity.class, EntityDataSerializers.FLOAT);
    }


}

