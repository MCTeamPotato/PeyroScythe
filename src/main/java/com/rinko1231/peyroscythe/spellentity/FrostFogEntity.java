package com.rinko1231.peyroscythe.spellentity;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class FrostFogEntity extends AoeEntity {
    public FrostFogEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.reapplicationDelay = 1;
    }

    public FrostFogEntity(Level level) {
        this((EntityType) EntityRegistry.FROST_FOG.get(), level);
    }

    @Override
    public void setDuration(int duration) {
        super.setDuration(duration);
    }

    @Override
    public void setRadius(float pRadius) {
        super.setRadius(pRadius);
    }

    public void applyEffect(LivingEntity target) {
        if (target == this.getOwner()) return;
        if (DamageSources.isFriendlyFireBetween(this.getOwner(), target)) return;
        DamageSources.ignoreNextKnockback(target);

        target.setTicksFrozen(Math.min(target.getTicksFrozen() + 8, target.getTicksRequiredToFreeze() * 3));
        target.addEffect(new MobEffectInstance(MobEffectRegistry.FROZEN.get(), 40, 1, false, true, true));

    }

    public void applyEffectPlus(LivingEntity target) {
        if (target == this.getOwner()) return;
        if (DamageSources.isFriendlyFireBetween(this.getOwner(), target)) return;
        DamageSources.ignoreNextKnockback(target);

        target.setTicksFrozen(Math.min(target.getTicksFrozen() + 9, target.getTicksRequiredToFreeze() * 3));
        target.addEffect(new MobEffectInstance(MobEffectRegistry.FROZEN.get(), 40, 4, false, true, true));

    }

    @Override
    public void tick() {
        double radius = this.getRadius();
        double innerRadius = radius * PeyroScytheConfig.frostFogInnerCircleRatio.get();

        // AABB 只在 XZ 平面扩展半径，高度固定上下各 2 格
        AABB box = new AABB(
                this.getX() - radius, this.getY() - 2, this.getZ() - radius,
                this.getX() + radius, this.getY() + 2, this.getZ() + radius
        );

        List<LivingEntity> targets = this.level().getEntitiesOfClass(
                LivingEntity.class,
                box,
                e -> e.isAlive() && !e.isSpectator()
        );

        for (LivingEntity target : targets) {
            // 判定实体是否在水平圆内（不再检查 Y 方向）
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            if (innerRadius == 0) { //没有内圈
                if (dx * dx + dz * dz <= radius * radius) { //全部内圈逻辑
                    this.applyEffect(target);
                }
            } else { //有内圈之分
                if (dx * dx + dz * dz <= radius * radius && dx * dx + dz * dz > innerRadius * innerRadius) {//外圈
                    this.applyEffect(target);
                }
                if (dx * dx + dz * dz <= innerRadius * innerRadius && dx * dx + dz * dz >= 0) {//内圈
                    this.applyEffectPlus(target);
                }
            }
        }

        super.tick();
    }


    public float getParticleCount() {
        return 0.2F * this.getRadius();
    }

    protected float particleYOffset() {
        return 0.25F;
    }

    protected float getParticleSpeedModifier() {
        return 1.4F;
    }

    public Optional<ParticleOptions> getParticle() {
        return Optional.empty();
    }

    public void ambientParticles() {
        if (this.level().isClientSide) {
            this.ambientParticles(ParticleHelper.SNOWFLAKE);
            this.ambientParticles(ParticleHelper.SNOW_DUST);
        }
    }

    public void ambientParticles(ParticleOptions particle) {
        float f = this.getParticleCount();
        f = Mth.clamp(f * this.getRadius(), f / 4.0F, f * 10.0F);

        for (int i = 0; (float) i < f; ++i) {
            if (f - (float) i < 1.0F && this.random.nextFloat() > f - (float) i) {
                return;
            }

            float r = this.getRadius();
            Vec3 pos;
            if (this.isCircular()) {
                float distance = r * (1.0F - this.random.nextFloat() * this.random.nextFloat());
                float theta = this.random.nextFloat() * 6.282F;
                pos = new Vec3((double) (distance * Mth.cos(theta)), (double) 0.2F, (double) (distance * Mth.sin(theta)));
            } else {
                pos = new Vec3(Utils.getRandomScaled((double) (r * 0.85F)), (double) 0.2F, Utils.getRandomScaled((double) (r * 0.85F)));
            }

            Vec3 motion = (new Vec3(Utils.getRandomScaled(0.03F), this.random.nextDouble() * (double) 0.01F, Utils.getRandomScaled((double) 0.03F))).scale((double) this.getParticleSpeedModifier());
            Vec3 vec3 = new Vec3(this.getX() + pos.x, this.getY() + pos.y + (double) 1.0F, this.getZ() + pos.z);
            vec3 = Utils.moveToRelativeGroundLevel(this.level(), vec3, 1, 2);
            this.level().addParticle(particle, vec3.x, vec3.y + (double) this.particleYOffset(), vec3.z, motion.x, motion.y, motion.z);
        }

    }


}
