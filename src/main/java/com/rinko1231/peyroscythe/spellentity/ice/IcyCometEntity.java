package com.rinko1231.peyroscythe.spellentity.ice;

import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

import static com.rinko1231.peyroscythe.init.EntityRegistry.ICY_COMET;

public class IcyCometEntity extends AbstractMagicProjectile {
        public IcyCometEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
            super(pEntityType, pLevel);
            this.setNoGravity(true);
        }

        public IcyCometEntity(Level pLevel, LivingEntity pShooter) {
            this((EntityType) ICY_COMET.get(), pLevel);
            this.setOwner(pShooter);
        }

        public void shoot(Vec3 rotation, float innaccuracy) {
            Vec3 offset = Utils.getRandomVec3((double)1.0F).normalize().scale((double)innaccuracy);
            super.shoot(rotation.add(offset));
        }

        public void trailParticles() {
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() - vec3.x;
            double d1 = this.getY() - vec3.y;
            double d2 = this.getZ() - vec3.z;

            for(int i = 0; i < 2; ++i) {
                Vec3 random = Utils.getRandomVec3(0.1);
                this.level().addParticle(ParticleTypes.SNOWFLAKE, d0 + random.x, d1 + (double)0.5F + random.y, d2 + random.z, random.x * (double)0.5F, random.y * (double)0.5F, random.z * (double)0.5F);
                this.level().addParticle(ParticleHelper.SNOWFLAKE, d0 - random.x, d1 + (double)0.5F - random.y, d2 - random.z, random.x * (double)0.5F, random.y * (double)0.5F, random.z * (double)0.5F);
            }

        }

        public void impactParticles(double x, double y, double z) {
            MagicManager.spawnParticles(this.level(), ParticleHelper.SNOW_DUST, x, y, z, 20, (double)0.0F, (double)0.0F, (double)0.0F, 0.18, false);
            MagicManager.spawnParticles(this.level(), new BlastwaveParticleOptions(((AbstractSpell) SpellRegistry.ICE_BLOCK_SPELL.get()).getSchoolType().getTargetingColor(), 1.25F), x, y, z, 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, true);
        }

        public float getSpeed() {
            return 1.85F;
        }

        protected void doImpactSound(SoundEvent sound) {
            this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 0.8F, 1.35F + Utils.random.nextFloat() * 0.3F);
        }

        public Optional<SoundEvent> getImpactSound() {
            return Optional.of(SoundEvents.GENERIC_EXPLODE);
        }

        protected void onHit(HitResult hitResult) {
            if (!this.level().isClientSide) {
                this.impactParticles(this.xOld, this.yOld, this.zOld);
                this.getImpactSound().ifPresent(this::doImpactSound);
                float explosionRadius = this.getExplosionRadius();

                for(Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate((double)explosionRadius))) {
                    double distance = entity.distanceToSqr(hitResult.getLocation());
                    if (distance < (double)(explosionRadius * explosionRadius) && this.canHitEntity(entity)) {
                        DamageSources.applyDamage(entity, this.damage, ((AbstractSpell) NewSpellRegistry.ICY_COMET_RAIN.get()).getDamageSource(this, this.getOwner()));
                        if (entity instanceof  LivingEntity target) {
                            target.setTicksFrozen(target.getTicksFrozen() + 30);
                            target.addEffect(new MobEffectInstance(MobEffectRegistry.FROZEN.get(), 60, 0, false, true, true));
                        }
                    }
                }

                this.discard();
            }

        }
    }
