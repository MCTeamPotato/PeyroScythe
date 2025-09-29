package com.rinko1231.peyroscythe.spellentity.eldritch;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;

import com.rinko1231.peyroscythe.init.TagsRegistry;
import com.rinko1231.peyroscythe.network.spell.ClientboundBlackFireballExplosionParticles;
import com.rinko1231.peyroscythe.utils.MyUtils;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.network.particles.FieryExplosionParticlesPacket;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.network.PacketDistributor;


import java.util.List;
import java.util.Optional;

import static com.rinko1231.peyroscythe.init.EntityRegistry.*;
import static com.rinko1231.peyroscythe.init.PeyroParticleRegistry.BLACK_EMBERS;
import static java.lang.Math.max;

public class BlackFireball extends AbstractMagicProjectile {
    public BlackFireball(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        //this.setNoGravity(true);
    }

    public BlackFireball(Level pLevel, LivingEntity pShooter) {
        this(BLACK_FIREBALL.get(), pLevel);
        this.setOwner(pShooter);
    }

    private static final EntityDataAccessor<Integer> SPELL_LEVEL =
            SynchedEntityData.defineId(BlackFireball.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SPLIT_TIMES =
            SynchedEntityData.defineId(BlackFireball.class, EntityDataSerializers.INT);

    public void setSpellLevel(int s) {
        this.entityData.set(SPELL_LEVEL, s);
    }

    public int getSpellLevel() {
        return this.entityData.get(SPELL_LEVEL);
    }

    public void setSplitTimes(int s) {
        this.entityData.set(SPLIT_TIMES, s);
    }

    public int getSplitTimes() {
        return this.entityData.get(SPLIT_TIMES);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SpellLevel", getSpellLevel());
        tag.putInt("SplitTimes", getSplitTimes());
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("SpellLevel")) {
            setSpellLevel(tag.getInt("SpellLevel"));
        }
        if (tag.contains("SplitTimes")) {
            setSplitTimes(tag.getInt("SplitTimes"));
        }
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder)  {
        super.defineSynchedData(pBuilder);
        pBuilder.define(SPELL_LEVEL, 1);
        pBuilder.define(SPLIT_TIMES, 4);
    }

    public void trailParticles() {
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() - vec3.x;
        double d1 = this.getY() - vec3.y;
        double d2 = this.getZ() - vec3.z;
        int count = Mth.clamp((int)(vec3.lengthSqr() * (double)4.0F), 1, 4);

        for(int i = 0; i < count; ++i) {
            Vec3 random = Utils.getRandomVec3((double)0.25F);
            float f = (float)i / (float)count;
            double x = Mth.lerp((double)f, d0, this.getX());
            double y = Mth.lerp((double)f, d1, this.getY());
            double z = Mth.lerp((double)f, d2, this.getZ());
            this.level().addParticle(ParticleTypes.SMOKE, x - random.x, y + (double)0.5F - random.y, z - random.z, random.x * (double)0.5F, random.y * (double)0.5F, random.z * (double)0.5F);
            this.level().addParticle(BLACK_EMBERS.get(), x - random.x, y + (double)0.5F - random.y, z - random.z, random.x * (double)0.5F, random.y * (double)0.5F, random.z * (double)0.5F);
        }

    }

    public void impactParticles(double x, double y, double z) {
    }

    public float getSpeed() {
        return 1.00F;
    }

    public Optional<Holder<SoundEvent>>  getImpactSound() {
        return Optional.of(SoundEvents.GENERIC_EXPLODE);
    }

    protected void onHit(HitResult hitResult) {
        if (!this.level().isClientSide) {
            this.impactParticles(this.xOld, this.yOld, this.zOld);
            float explosionRadius = this.getExplosionRadius();
            float explosionRadiusSqr = explosionRadius * explosionRadius;
            List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().inflate((double)explosionRadius));
            Vec3 losPoint = Utils.raycastForBlock(this.level(), this.position(), this.position().add((double)0.0F, (double)2.0F, (double)0.0F), ClipContext.Fluid.NONE).getLocation();

            for(Entity entity : entities) {
                double distanceSqr = entity.distanceToSqr(hitResult.getLocation());
                if (distanceSqr < (double)explosionRadiusSqr && this.canHitEntity(entity) && Utils.hasLineOfSight(this.level(), losPoint, entity.getBoundingBox().getCenter(), true)) {
                    double p = (double)1.0F - distanceSqr / (double)explosionRadiusSqr;
                    float damage = (float)((double)this.damage * p);
                    if(entity instanceof LivingEntity living)
                    {
                        DamageSources.applyDamage(entity, damage+living.getMaxHealth()*0.05f, ((AbstractSpell) NewSpellRegistry.BLACK_FIREBALL.get()).getDamageSource(this, this.getOwner()));
                        //living.addEffect()
                        //如果并非免疫
                        if (!living.getType().is(TagsRegistry.DEATH_SMOKE_IMMUNE) && !living.hasEffect(io.redspace.ironsspellbooks.registries.MobEffectRegistry.ABYSSAL_SHROUD)&& !living.hasEffect(io.redspace.ironsspellbooks.registries.MobEffectRegistry.HEARTSTOP))
                        {
                            MobEffectInstance existing = living.getEffect(MobEffectRegistry.DEATH_SMOKE_EROSION);
                            int amp = 0;

                            if (existing != null) {
                                amp = existing.getAmplifier() + 3; // 叠加等级
                            }
                            int newAmp = max(amp, 5);
                            living.addEffect(new MobEffectInstance(
                                    MobEffectRegistry.DEATH_SMOKE_EROSION,
                                    PeyroScytheConfig.deathSmokeErosionDuration.get(),
                                    newAmp,
                                    false,
                                    true,
                                    true
                            ));
                        }


                    }
                    else
                        DamageSources.applyDamage(entity, damage, ((AbstractSpell) NewSpellRegistry.BLACK_FIREBALL.get()).getDamageSource(this, this.getOwner()));

                }
            }

            if ((Boolean) ServerConfigs.SPELL_GREIFING.get()) {
                Explosion explosion = new Explosion(this.level(), (Entity)null, ((AbstractSpell) NewSpellRegistry.BLACK_FIREBALL).getDamageSource(this, this.getOwner()), (ExplosionDamageCalculator)null, this.getX(), this.getY(), this.getZ(), this.getExplosionRadius() / 2.0F, true, Explosion.BlockInteraction.DESTROY, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
                if (!((ExplosionEvent.Start) NeoForge.EVENT_BUS.post(new ExplosionEvent.Start(this.level(), explosion))).isCanceled()) {
                    explosion.explode();
                    explosion.finalizeExplosion(false);
                }
            }
            PacketDistributor.sendToPlayersTrackingEntity(this, new ClientboundBlackFireballExplosionParticles(hitResult.getLocation().subtract(this.getDeltaMovement().scale((double)0.5F)), this.getExplosionRadius()), new CustomPacketPayload[0]);
            this.playSound(SoundEvents.GENERIC_EXPLODE.value(), MyUtils.maxCap(4.0F, this.getSpellLevel()+1.0f), (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F);

            if (!this.level().isClientSide) {
                double up = Math.sin(Math.toRadians(45));
                double horiz = Math.cos(Math.toRadians(45));
                double[][] dirs6 = { {1,0},{-1,0},{0.5,0.866},{-0.5,0.866},{0.5,-0.866},{-0.5,-0.866} };
                double[][] dirs4 = { {1,0},{-1,0},{0,1},{0,-1}};
                double[][] dirs;
                if(this.getSplitTimes()>5)
                    dirs = dirs6;
                else if(this.getSpellLevel()<=2)
                    dirs = dirs4;
                            else dirs = dirs6;
                if(this.getSpellLevel()>=3 )
                for (double[] d : dirs) {
                    Vec3 dir = new Vec3(d[0], 0, d[1]).normalize();
                    Vec3 motion = new Vec3(dir.x * horiz, up, dir.z * horiz).normalize().scale(0.2+0.05*this.getSpellLevel());

                    BlackFireball smallF = new BlackFireball(BLACK_FIREBALL_SMALL.get(), this.level());
                    smallF.setOwner(this.getOwner());
                    smallF.setSpellLevel( 1 );
                    if(this.getSpellLevel()>=5)
                        smallF.setSplitTimes(6);
                    smallF.setDamage(MyUtils.minCap(1,this.damage/2));
                    smallF.moveTo(this.getX(), this.getY() + 0.3, this.getZ(), 0, 0);
                    smallF.setDeltaMovement(motion);
                    this.level().addFreshEntity(smallF);
                }
                else
                    for (double[] d : dirs) {
                        Vec3 dir = new Vec3(d[0], 0, d[1]).normalize();
                        Vec3 motion = new Vec3(dir.x * horiz, up, dir.z * horiz).normalize().scale(0.2+0.05*this.getSpellLevel());

                        BlackFireballSmall small = new BlackFireballSmall(BLACK_FIREBALL_SMALL.get(), this.level());
                        small.setOwner(this.getOwner());
                        small.setSpellLevel(1);
                        small.setDamage(MyUtils.minCap(1,this.damage/2));
                        small.moveTo(this.getX(), this.getY() + 0.3, this.getZ(), 0, 0);
                        small.setDeltaMovement(motion);
                        this.level().addFreshEntity(small);
                    }
            }



            this.discard();
        }

    }
}

