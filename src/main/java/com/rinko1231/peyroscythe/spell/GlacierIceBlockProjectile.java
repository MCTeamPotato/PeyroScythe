package com.rinko1231.peyroscythe.spell;

import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.ice_block.IceBlockProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GlacierIceBlockProjectile extends IceBlockProjectile {

    private final List<Entity> extraVictims = new ArrayList<>(); // 本类用的“擦伤”记录
    int airTime;
    private List<Entity> victims;
    private final AnimatableInstanceCache cache;

    private static final float BASE_WIDTH  = 1.0F;
    private static final float BASE_HEIGHT = 1.0F;
    private static final float BASE_IMPACT_RADIUS = 3.5F;
    private static final EntityDataAccessor<Float> DATA_SCALE =
            SynchedEntityData.defineId(GlacierIceBlockProjectile.class, EntityDataSerializers.FLOAT);
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SCALE, 1.0F);
    }

    public void setScaleFactor(float s) {
        this.entityData.set(DATA_SCALE, s);
        this.refreshDimensions(); // ← 改了就刷新碰撞箱/眼高等
    }

    public float getScaleFactor() {
        return this.entityData.get(DATA_SCALE);
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("ScaleFactor", getScaleFactor());
    }
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_SCALE.equals(key)) {
            this.refreshDimensions();
        }
    }


    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ScaleFactor")) {
            setScaleFactor(tag.getFloat("ScaleFactor"));
            this.refreshDimensions(); // ← 再保险
        }
    }
    @Override
    public EntityDimensions getDimensions(Pose pose) {
        float s = getScaleFactor();
        return EntityDimensions.scalable(BASE_WIDTH * s, BASE_HEIGHT * s);
    }

    public GlacierIceBlockProjectile(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.victims = new ArrayList<>();
        this.setNoGravity(true);
        this.refreshDimensions(); // 构造后就更新一次
    }
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
    // 施法时用这个：强制使用“巨大版”的 EntityType，而不是父类便捷构造
    public GlacierIceBlockProjectile(Level level, LivingEntity owner, @Nullable LivingEntity target) {
        this(com.rinko1231.peyroscythe.init.EntityRegistry.GLACIER_ICE_BLOCK_PROJECTILE.get(), level);
        this.setOwner(owner);
        if (target != null) this.setTarget(target);
        this.setNoGravity(true);
        this.refreshDimensions(); // 构造后就更新一次
    }

    public void tick() {
        this.firstTick = false;
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

        if (!this.level().isClientSide) {



            if (this.airTime <= 0) {
                if (this.onGround()) {
                    this.doImpactDamage();
                    this.playSound((SoundEvent)SoundRegistry.ICE_BLOCK_IMPACT.get(), 2.5F, 0.8F + this.random.nextFloat() * 0.4F);
                    this.impactParticles(this.getX(), this.getY(), this.getZ());
                    this.discard();
                } else {
                    this.level().getEntities(this, this.getBoundingBox().inflate(0.35)).forEach(this::doFallingDamage);
                }
            }

            if (this.airTime-- > 0) {
                boolean tooHigh = false;
                this.setDeltaMovement(this.getDeltaMovement().multiply((double)0.95F, (double)0.75F, (double)0.95F));
                if (this.getTarget() != null) {
                    Entity target = this.getTarget();
                    Vec3 diff = target.position().subtract(this.position());
                    if (diff.horizontalDistanceSqr() > (double)1.0F) {
                        this.setDeltaMovement(this.getDeltaMovement().add(diff.multiply((double)1.0F, (double)0.0F, (double)1.0F).normalize().scale((double)0.025F)));
                    }

                    if (this.getY() - target.getY() > (double)3.5F) {
                        tooHigh = true;
                    }
                } else if (this.airTime % 3 == 0) {
                    HitResult ground = Utils.raycastForBlock(this.level(), this.position(), this.position().subtract((double)0.0F, (double)3.5F, (double)0.0F), ClipContext.Fluid.ANY);
                    if (ground.getType() == HitResult.Type.MISS) {
                        tooHigh = true;
                    } else if (Math.abs(this.position().y - ground.getLocation().y) < (double)4.0F) {
                    }
                }

                if (tooHigh) {
                    this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, -0.005, (double)0.0F));
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, 0.01, (double)0.0F));
                }

                if (this.airTime == 0) {
                    this.setDeltaMovement((double)0.0F, (double)0.5F, (double)0.0F);
                }
            } else {
                this.setDeltaMovement((double)0.0F, this.getDeltaMovement().y - 0.15, (double)0.0F);
            }
        } else {
            this.trailParticles();
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }



    private void doFallingDamage(Entity target) {
        if (!this.level().isClientSide) {


            if (this.canHitEntity(target) && !this.victims.contains(target)) {
                boolean flag = DamageSources.applyDamage(target, this.getDamage() / 2.0F, ((AbstractSpell)SpellRegistry.ICE_BLOCK_SPELL.get()).getDamageSource(this, this.getOwner()));

                if(this.getOwner() instanceof ServerPlayer serverPlayer && flag )
                    serverPlayer.displayClientMessage(Component.literal("测试：碰撞伤害生效"),false);

                if (flag) {
                    this.victims.add(target);
                    target.invulnerableTime = 0;
                }

            }
        }
    }


    private void doImpactDamage() {
        float s = getScaleFactor();
        float explosionRadius = 3.5F*s;
        this.level().getEntities(this, this.getBoundingBox().inflate((double)explosionRadius)).forEach((entity) -> {
            if (this.canHitEntity(entity)) {
                double distance = entity.distanceToSqr(this.position());
                if (distance < (double)(explosionRadius * explosionRadius)) {
                    double p = (double)1.0F - Math.pow(Math.sqrt(distance) / (double)explosionRadius, (double)3.0F);
                    float damage = (float)((double)this.damage * p);
                    DamageSources.applyDamage(entity, damage, ((AbstractSpell)SpellRegistry.ICE_BLOCK_SPELL.get()).getDamageSource(this, this.getOwner()));
                }
            }

        });
    }



    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity hit = result.getEntity();
        if (this.canHitEntity(hit)) {
            // 命中单体伤害
            DamageSources.applyDamage(hit, this.getDamage(),
                    ((AbstractSpell) SpellRegistry.ICE_BLOCK_SPELL.get()).getDamageSource(this, this.getOwner()));
        }
        // 命中后立即爆裂范围伤害
        this.doImpactDamageLikeParent();
        this.playSound(SoundRegistry.ICE_BLOCK_IMPACT.get(), 2.5F, 0.8F + this.random.nextFloat() * 0.4F);
        this.impactParticles(this.getX(), this.getY(), this.getZ());
        this.discard();
    }


    @Override
    protected void onHitBlock(BlockHitResult result) {
        // 命中地面也直接爆裂
        this.doImpactDamageLikeParent();
        this.playSound(SoundRegistry.ICE_BLOCK_IMPACT.get(), 2.5F, 0.8F + this.random.nextFloat() * 0.4F);
        this.impactParticles(this.getX(), this.getY(), this.getZ());
        this.discard();
    }


    // ——复刻父类私有逻辑：落地爆裂——
    private void doImpactDamageLikeParent() {
        float explosionRadius = BASE_IMPACT_RADIUS;
        double r2 = explosionRadius * explosionRadius;

        this.level().getEntities(this, this.getBoundingBox().inflate(explosionRadius)).forEach(entity -> {
            if (this.canHitEntity(entity)) {
                double d2 = entity.distanceToSqr(this.position());
                if (d2 < r2) {
                    double p = 1.0 - Math.pow(Math.sqrt(d2) / explosionRadius, 3.0);
                    float damage = (float)(this.getDamage() * p);
                    DamageSources.applyDamage(entity, damage,
                            ((AbstractSpell) NewSpellRegistry.GLACIER_SPELL.get()).getDamageSource(this, this.getOwner()));
                }
            }
        });
    }


}
