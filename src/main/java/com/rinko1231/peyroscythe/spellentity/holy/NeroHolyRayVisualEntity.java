package com.rinko1231.peyroscythe.spellentity.holy;

import com.rinko1231.peyroscythe.init.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public class NeroHolyRayVisualEntity extends Entity implements IEntityAdditionalSpawnData {
    public static final int lifetime = 15;
    public float distance;

    public NeroHolyRayVisualEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public NeroHolyRayVisualEntity(Level level, Vec3 start, Vec3 end, LivingEntity owner) {
        super(EntityRegistry.NERO_HOLY_RAY_VISUAL_ENTITY.get(), level);
        this.setPos(start.subtract((double)0.0F, (double)0.75F, (double)0.0F));
        this.distance = (float)start.distanceTo(end);
        this.setRot(owner.getYRot(), owner.getXRot());
    }

    public void tick() {
        if (++this.tickCount > lifetime) {
            this.discard();
        }

    }

    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

    public boolean shouldBeSaved() {
        return false;
    }

    protected void defineSynchedData() {
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt((int)(this.distance * 10.0F));
    }

    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.distance = (float)additionalData.readInt() / 10.0F;
    }
}
