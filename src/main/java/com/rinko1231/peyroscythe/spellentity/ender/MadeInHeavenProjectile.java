package com.rinko1231.peyroscythe.spellentity.ender;

import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.utils.AbstractProjectileReverse;
import com.rinko1231.peyroscythe.utils.TimeFlowAccelerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;


public class MadeInHeavenProjectile extends AbstractProjectileReverse {

    private static final EntityDataAccessor<Integer> SPELL_LEVEL =
            SynchedEntityData.defineId(MadeInHeavenProjectile.class, EntityDataSerializers.INT);

    public MadeInHeavenProjectile(EntityType<? extends AbstractProjectileReverse> entityType, Level level) {
        super(entityType, level);
    }

    public MadeInHeavenProjectile(Level level, LivingEntity entity) {
        super(EntityRegistry.MADE_IN_HEAVEN_PROJECTILE.get(), level, entity);
        this.setOwner(entity);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPELL_LEVEL, 1);
    }

    public void setSpellLevel(int s) {
        this.entityData.set(SPELL_LEVEL, s);
    }

    public int getSpellLevel() {
        return this.entityData.get(SPELL_LEVEL);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SpellLevel", getSpellLevel());
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("SpellLevel")) {
            setSpellLevel(tag.getInt("SpellLevel"));
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

    public void tick() {
        super.tick();
        if (this.getOwner()!=null && !this.level().isClientSide)
        {
            int factor = this.getFactor(this.getSpellLevel()); // 举例：20倍昼夜加速
        TimeFlowAccelerator.declare((ServerLevel) this.level(), this.getOwner().getUUID(), factor);

        }
    }


    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
    }
}