package com.rinko1231.peyroscythe.spellentity;

import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import com.rinko1231.peyroscythe.init.TagsRegistry;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import io.redspace.ironsspellbooks.entity.spells.blood_slash.BloodSlashProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TempestEdgeHProjectile extends Projectile implements AntiMagicSusceptible {
    private static final EntityDataAccessor<Float> DATA_RADIUS;
    private static final double SPEED = (double)1.0F;
    private static final int EXPIRE_TIME = 160;
    public final int animationSeed;
    private final float maxRadius;
    public AABB oldBB;
    private int age;
    private float damage;
    public int animationTime;
    private List<Entity> victims;

    private final Set<Integer> piercedEntityIds = new HashSet<>();
    private int piercedCount = 0;
    private final int maxPierce = 8;

    public TempestEdgeHProjectile(EntityType<? extends TempestEdgeHProjectile> entityType, Level level) {
        super(entityType, level);
        this.animationSeed = Utils.random.nextInt(9999);
        this.setRadius(1.2F);
        this.maxRadius = 6.0F;
        this.oldBB = this.getBoundingBox();
        this.victims = new ArrayList();
        this.setNoGravity(true);
    }

    public TempestEdgeHProjectile(EntityType<? extends TempestEdgeHProjectile> entityType, Level levelIn, LivingEntity shooter) {
        this(entityType, levelIn);
        this.setOwner(shooter);
        this.setYRot(shooter.getYRot());
        this.setXRot(shooter.getXRot());
    }

    public TempestEdgeHProjectile(Level levelIn, LivingEntity shooter) {
        this(EntityRegistry.TEMPEST_EDGE_H_PROJECTILE.get(), levelIn, shooter);
    }

    public void shoot(Vec3 rotation) {
        this.setDeltaMovement(rotation.scale((double)1.0F));
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    protected void defineSynchedData() {
        this.getEntityData().define(DATA_RADIUS, 0.5F);
    }

    public void setRadius(float newRadius) {
        if (newRadius <= this.maxRadius && !this.level().isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Mth.clamp(newRadius, 0.0F, this.maxRadius));
        }

    }

    public float getRadius() {
        return (Float)this.getEntityData().get(DATA_RADIUS);
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public void tick() {
        super.tick();
        if (++this.age > EXPIRE_TIME) {
            this.discard();
        } else {
            this.oldBB = this.getBoundingBox();
            this.setRadius(this.getRadius() + 0.5F);
            if (!this.level().isClientSide) {
                HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
                if (hitresult.getType() == HitResult.Type.BLOCK) {
                    this.onHitBlock((BlockHitResult)hitresult);
                }

                for(Entity entity : this.level().getEntities(this, this.getBoundingBox()).stream().filter((target) -> this.canHitEntity(target) && !this.victims.contains(target)).collect(Collectors.toSet())) {
                    this.damageEntity(entity);
                    MagicManager.spawnParticles(this.level(), ParticleHelper.BLOOD, entity.getX(), entity.getY(), entity.getZ(), 50, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.5F, true);
                    if (entity instanceof ShieldPart || entity instanceof AbstractShieldEntity) {
                        this.discard();
                        return;
                    }
                }
            }

            this.setPos(this.position().add(this.getDeltaMovement()));
            //this.spawnParticles();
        }
    }

    public EntityDimensions getDimensions(Pose p_19721_) {
        this.getBoundingBox();
        return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_19729_) {
        if (DATA_RADIUS.equals(p_19729_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_19729_);
    }


    private void damageEntity(Entity entity) {
        if (!this.victims.contains(entity)) {
            if (entity.isOnFire() || entity.isFullyFrozen())
                DamageSources.applyDamage(entity, this.damage * 1.5f, (NewSpellRegistry.RYAN_TEMPEST_EDGE.get()).getDamageSource(this, this.getOwner()));
            else DamageSources.applyDamage(entity, this.damage, (NewSpellRegistry.RYAN_TEMPEST_EDGE.get()).getDamageSource(this, this.getOwner()));
            this.victims.add(entity);
        }

    }

    public void spawnParticles() {
        if (this.level().isClientSide) {
            float width = (float)this.getBoundingBox().getXsize();
            float step = 0.25F;
            float radians = ((float)Math.PI / 180F) * this.getYRot();
            float speed = 0.1F;

            for(int i = 0; (float)i < width / step; ++i) {
                double x = this.getX();
                double y = this.getY();
                double z = this.getZ();
                double offset = (double)(step * ((float)i - width / step / 2.0F));
                double rotX = offset * Math.cos((double)radians);
                double rotZ = -offset * Math.sin((double)radians);
                double dx = Math.random() * (double)speed * (double)2.0F - (double)speed;
                double dy = Math.random() * (double)speed * (double)2.0F - (double)speed;
                double dz = Math.random() * (double)speed * (double)2.0F - (double)speed;
                this.level().addParticle(ParticleHelper.BLOOD, false, x + rotX + dx, y + dy, z + rotZ + dz, dx, dy, dz);
            }
        }

    }

    protected boolean canHitEntity(Entity entity) {
        return entity != this.getOwner() && super.canHitEntity(entity);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity target = hitResult.getEntity();

        // 已经击中过就不再触发
        if (piercedEntityIds.contains(target.getId())) {
            return;
        }
        piercedEntityIds.add(target.getId());

        Entity owner = this.getOwner();
        DamageSources.applyDamage(target, this.damage,
                (NewSpellRegistry.RYAN_TEMPEST_EDGE.get()).getDamageSource(this, owner));

        // 击中特效（如果要粒子的话）
        this.spawnParticles();

        piercedCount++;
        if (piercedCount >= maxPierce) {
            this.discard();
        }
    }
    protected void onHitBlock(BlockHitResult blockHitResult) {
        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = this.level().getBlockState(pos);

        if (state.is(TagsRegistry.TEMPEST_EDGE_DESTROYABLE)) {
            // 原木或树叶 → 直接破坏
            this.level().destroyBlock(pos, true, this.getOwner());
            // true = 掉落方块战利品，false = 不掉落
            piercedCount++;
            if (piercedCount >= maxPierce) {
                this.discard();
            }
        } else {
            // 其他方块就直接消失
            this.discard();
        }
    }

    public void onAntiMagic(MagicData playerMagicData) {
        this.discard();
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("Damage", this.damage);
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.damage = pCompound.getFloat("Damage");
    }

    static {
        DATA_RADIUS = SynchedEntityData.defineId(BloodSlashProjectile.class, EntityDataSerializers.FLOAT);
    }
}
