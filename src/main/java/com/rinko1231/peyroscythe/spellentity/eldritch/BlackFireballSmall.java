package com.rinko1231.peyroscythe.spellentity.eldritch;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;

import com.rinko1231.peyroscythe.init.TagsRegistry;
import com.rinko1231.peyroscythe.network.spell.ClientboundBlackFireballExplosionParticles;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;


import static com.rinko1231.peyroscythe.init.EntityRegistry.BLACK_FIREBALL_SMALL;
import static com.rinko1231.peyroscythe.init.PeyroParticleRegistry.BLACK_EMBERS;
import static java.lang.Math.max;

public class BlackFireballSmall extends BlackFireball{

    public BlackFireballSmall(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        //this.setNoGravity(true);
    }

    public BlackFireballSmall(Level pLevel, LivingEntity pShooter) {
        this(BLACK_FIREBALL_SMALL.get(), pLevel);
        this.setOwner(pShooter);
    }



    private static final EntityDataAccessor<Integer> SPELL_LEVEL =
            SynchedEntityData.defineId(BlackFireballSmall.class, EntityDataSerializers.INT);

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
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder)  {
        super.defineSynchedData(pBuilder);
        pBuilder.define(SPELL_LEVEL, 1);
    }




    public void shoot(Vec3 rotation) {
        this.setDeltaMovement(rotation.scale((double)this.getSpeed()));
    }

    public void trailParticles() {
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() - vec3.x;
        double d1 = this.getY() - vec3.y;
        double d2 = this.getZ() - vec3.z;
        int count = Mth.clamp((int)(vec3.lengthSqr() * (double)4.0F), 1, 2);

        for(int i = 0; i < count; ++i) {
            Vec3 random = Utils.getRandomVec3((double)0.15F);
            float f = (float)i / (float)count;
            double x = Mth.lerp((double)f, d0, this.getX());
            double y = Mth.lerp((double)f, d1, this.getY());
            double z = Mth.lerp((double)f, d2, this.getZ());
            //this.level().addParticle(ParticleTypes.LARGE_SMOKE, x - random.x, y + (double)0.5F - random.y, z - random.z, random.x * (double)0.5F, random.y * (double)0.5F, random.z * (double)0.5F);
            this.level().addParticle(BLACK_EMBERS.get(), x - random.x, y + (double)0.5F - random.y, z - random.z, random.x * (double)0.5F, random.y * (double)0.5F, random.z * (double)0.5F);
        }

    }


    public float getSpeed() {
        return 0.15F;
    }

    public Optional<Holder<SoundEvent>>  getImpactSound() {
        return Optional.of(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.FIREWORK_ROCKET_BLAST));
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
                        DamageSources.applyDamage(entity, damage+living.getMaxHealth()*0.01f, ((AbstractSpell) NewSpellRegistry.BLACK_FIREBALL.get()).getDamageSource(this, this.getOwner()));
                        //living.addEffect()
                        //如果并非免疫
                        if (!living.getType().is(TagsRegistry.DEATH_SMOKE_IMMUNE) && !living.hasEffect(io.redspace.ironsspellbooks.registries.MobEffectRegistry.ABYSSAL_SHROUD)&& !living.hasEffect(io.redspace.ironsspellbooks.registries.MobEffectRegistry.HEARTSTOP))
                        {
                            MobEffectInstance existing = living.getEffect(MobEffectRegistry.DEATH_SMOKE_EROSION);
                            int amp = 0;

                            if (existing != null) {
                                amp = existing.getAmplifier() + 2; // 叠加等级
                            }
                            int newAmp = max(amp, 3);
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


            //PeyroMessages.sendToPlayersTrackingEntity(new ClientboundBlackFireballExplosionParticles(new Vec3(this.getX(), this.getY() + (double)0.15F, this.getZ()), this.getExplosionRadius()), this);
            this.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.0F, (0.66F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.5F);
            this.discard();
        }

    }
}
