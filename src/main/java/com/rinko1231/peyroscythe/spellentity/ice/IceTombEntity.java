package com.rinko1231.peyroscythe.spellentity.ice;


import com.rinko1231.peyroscythe.api.PreventDismountNew;
import com.rinko1231.peyroscythe.init.EntityRegistry;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.UUID;

public class IceTombEntity extends Entity implements PreventDismountNew, AntiMagicSusceptible {
    @Nullable
    private LivingEntity cachedOwner;
    @Nullable
    private UUID ownerUUID;
    /**
     * evil tombs hurt, versus heal
     */
    private boolean evil;
    private float health = 1;
    private int lifetime = -1;

    private float healing;

    public IceTombEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public IceTombEntity(Level level, LivingEntity owner) {
        super(EntityRegistry.ICE_TOMB.get(), level);
        setOwner(owner);
    }
    protected boolean isImmobile() {
        return true;
    }
    public double getPassengersRidingOffset() {
        return (double)0.0F;
    }
    public boolean showVehicleHealth() {
        return false;
    }
    @Override
    public boolean skipAttackInteraction(Entity entity) {
        return isPassengerOfSameVehicle(entity);
    }
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.singleton(ItemStack.EMPTY);
    }


    public boolean isPushable() {
        return false;
    }

    @Override
    protected void defineSynchedData() {

    }
    public ItemStack getItemBySlot(EquipmentSlot pSlot) {
        return ItemStack.EMPTY;
    }

    public void setEvil() {
        this.evil = true;
    }

    public void setOwner(@Nullable LivingEntity owner) {
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
            this.cachedOwner = owner;
        }
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    public void setHealing(float healing) {
        this.healing = healing;
    }

    @Override
    public boolean hasIndirectPassenger(Entity pEntity) {
        // this flag seems to primarily control whether the "press [] to dismount" message occurs
        // make it so that we only get that message if we can dismount
        return evil;
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel serverlevel) {
            Entity entity  = serverlevel.getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.cachedOwner = (LivingEntity)entity;
                return this.cachedOwner;
            }
        } else {
            return null;
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        if (evil) {
            getPassengers().forEach(this::doNegativeEffects);
        } else {
            if (tickCount % 20 == 0) {
                getPassengers().forEach(this::doPositiveEffects);
            }
        }
        this.applyGravity();
        this.move(MoverType.SELF, getDeltaMovement());
        if (onGround()) {
            this.setDeltaMovement(getDeltaMovement().scale(0.7));
        } else {
            this.setDeltaMovement(getDeltaMovement().multiply(0.95, 1, 0.95));
        }
        if (lifetime >= 0 && tickCount > lifetime || this.getOwner() != null && this.getOwner().isDeadOrDying() || !this.isVehicle()) {
            destroyTomb();
        }
    }
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && health > 0) {
            if (!isInvulnerableTo(source) && (source.getEntity() == null || !isPassengerOfSameVehicle(source.getEntity()))) {
                health -= amount;
                if (health <= 0) {
                    die(source, amount);
                }
                return true;
            }
        }
        return super.hurt(source, amount);
    }

    protected double getDefaultGravity() {
        return LivingEntity.DEFAULT_BASE_GRAVITY;
    }
    public void applyGravity() {
        double d0 = this.getGravity();
        if (d0 != 0.0) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -d0, 0.0));
        }
    }
    public final double getGravity() {
        return this.isNoGravity() ? 0.0 : this.getDefaultGravity();
    }



    public void doPositiveEffects(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            MinecraftForge.EVENT_BUS.post(new SpellHealEvent(livingEntity, livingEntity, this.healing, SchoolRegistry.ICE.get()));
            livingEntity.heal(this.healing);
        }
    }

    public void doNegativeEffects(Entity entity) {
        entity.setTicksFrozen(Math.min(entity.getTicksRequiredToFreeze() * 3, entity.getTicksFrozen() + 10));
    }



    public void die(DamageSource damageSource, float amount) {
        var entities = getPassengers();
        destroyTomb();
        if (evil) {
            entities.forEach(entity -> entity.hurt(damageSource, amount * 2));
        }
    }

    @Override
    public void kill() {
        destroyTomb();
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        refreshDimensions();
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        destroyTomb();
    }

    public void destroyTomb() {
        if (!this.level().isClientSide) {
            this.ejectPassengers();
            this.playSound(SoundEvents.GLASS_BREAK, 2, 1);
            MagicManager.spawnParticles(this.level(), ParticleHelper.SNOW_DUST, getX(), getY() + 1, getZ(), 50, 0.2, 0.2, 0.2, 0.2, false);
            MagicManager.spawnParticles(this.level(), ParticleHelper.SNOWFLAKE, getX(), getY() + 1, getZ(), 50, 0.2, 0.2, 0.2, 0.2, false);
            this.discard();
        }
    }



    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction p_19958_) {
        passenger.setPos(this.getX(), this.getY(), this.getZ());
    }


    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.ownerUUID != null) {
            compound.putUUID("Owner", this.ownerUUID);
        }
        compound.putInt("age", tickCount);
        compound.putInt("lifetime", lifetime);
        compound.putBoolean("evil", this.evil);
        compound.putFloat("health", this.health);
        compound.putFloat("healing", this.healing);
    }

    @Override
    public boolean dismountsUnderwater() {
        return false;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
            this.cachedOwner = null;
        }
        this.tickCount = compound.getInt("age");
        this.lifetime = compound.getInt("lifetime");
        this.evil = compound.getBoolean("evil");
        this.health = compound.getFloat("health");
        this.healing = compound.getFloat("healing");
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        var passengers = getPassengers();
        float hScale = 1f;
        float vScale = 1f;
        if (!passengers.isEmpty() && passengers.get(0) instanceof LivingEntity livingEntity) {
            hScale = livingEntity.getBbWidth() + .4f;//* 1.66f; // ratio of our default hitbox to the players default hitbox
            vScale = (livingEntity.getBbHeight() + .2f) / 2;//* 0.555f;  // ratio of our default hitbox to the players default hitbox
            vScale = (vScale + hScale) * .5f; // average fixed-scale to desired scale. no change for humanoids, but will stretch for more cuboid entities
        }
        return super.getDimensions(pPose).scale(hScale * .9f * 1.1f, vScale * .9f * 1.1f);
    }

    @Override
    public boolean canCollideWith(@NotNull Entity pEntity) {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void push(@NotNull Entity pEntity) {

    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        destroyTomb();
    }

    public boolean isEvil()
    {
        return this.evil;
    }
    @Override
    public boolean canEntityDismount(Entity rider) {
        if (this.evil) return false;
        return rider.getUUID().equals(this.ownerUUID);
    }
}