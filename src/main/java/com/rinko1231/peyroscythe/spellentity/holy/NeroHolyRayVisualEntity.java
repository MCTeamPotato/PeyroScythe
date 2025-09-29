package com.rinko1231.peyroscythe.spellentity.holy;

import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import static com.rinko1231.peyroscythe.init.EntityRegistry.NERO_HOLY_RAY_VISUAL_ENTITY;


public class NeroHolyRayVisualEntity extends Entity implements IEntityWithComplexSpawn {
    public static final int lifetime = 15;
    public float distance;

    public NeroHolyRayVisualEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public NeroHolyRayVisualEntity(Level level, Vec3 start, Vec3 end, LivingEntity owner) {
        super((EntityType) NERO_HOLY_RAY_VISUAL_ENTITY.get(), level);
        this.setPos(start.subtract((double)0.0F, (double)0.75F, (double)0.0F));
        this.distance = (float)start.distanceTo(end);
        this.setRot(owner.getYRot(), owner.getXRot());
    }

    public void tick() {
        if (++this.tickCount > 15) {
            this.discard();
        }

    }

    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

    public boolean shouldBeSaved() {
        return false;
    }

    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
    }

    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt((int)(this.distance * 10.0F));
    }

    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        this.distance = (float)additionalData.readInt() / 10.0F;
    }
}
