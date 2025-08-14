package com.rinko1231.peyroscythe.spellentity;


import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.VisualFallingBlockEntity;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class IceQuakeAoe extends AoeEntity {
    int waveAnim;
    private final double HALF_DEGREE = PeyroScytheConfig.frostHellDegree.get() * 0.5;
    public IceQuakeAoe(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.waveAnim = -1;
        this.reapplicationDelay = 25;
        this.setCircular();
    }

    public IceQuakeAoe(Level level, float radius) {
        this(EntityRegistry.ICE_QUAKE_AOE.get(), level);
        this.setRadius(radius);
    }


    public static void createTremorStack(Level level, BlockState baseState, BlockState topState, BlockPos blockPos, float impulseStrength) {


        Random random = new Random();
        float roll = random.nextFloat(); // 0.0 ~ 1.0

        float chanceA = 0.3F; // 分支 A 的概率
        float chanceB = 0.5F; // 分支 B 的概率


        if (roll < chanceA) {

            VisualFallingBlockEntity base = new VisualFallingBlockEntity(
                    level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), baseState, 10
            );
            base.setDeltaMovement(0.0, impulseStrength, 0.0);
            level.addFreshEntity(base);
        } else if (roll < chanceA + chanceB) {

            VisualFallingBlockEntity base = new VisualFallingBlockEntity(
                    level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.ICE.defaultBlockState(), 10
            );
            base.setDeltaMovement(0.0, impulseStrength, 0.0);
            level.addFreshEntity(base);
        } else {
            // 分支 C
            VisualFallingBlockEntity base = new VisualFallingBlockEntity(
                    level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.PACKED_ICE.defaultBlockState(), 10
            );
            base.setDeltaMovement(0.0, impulseStrength, 0.0);
            level.addFreshEntity(base);
        }

        if (roll < chanceA) {
        VisualFallingBlockEntity top = new VisualFallingBlockEntity(
                level, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), topState, 10
        );
        top.setDeltaMovement(0.0, impulseStrength, 0.0);
        level.addFreshEntity(top);}
    }

    public void applyEffect(LivingEntity target) {
        SpellDamageSource damageSource = ((AbstractSpell) NewSpellRegistry.FROST_HELL_SPELL.get()).getDamageSource((Entity) (this.getOwner() == null ? this : this.getOwner()));
        DamageSources.ignoreNextKnockback(target);
        if (target.hurt(damageSource, this.getDamage())) {
            target.addEffect(
                    new MobEffectInstance(
                            MobEffectRegistry.SLOWED.get(),
                            100,
                            0
                    )
            );
            target.setDeltaMovement(target.getDeltaMovement().add((double) 0.0F, 0.65, (double) 0.0F));
            target.hurtMarked = true;
        }

    }

    public float getParticleCount() {
        return 0.0F;
    }

    public void ambientParticles() {
    }

    public void tick() {
        float radius = this.getRadius();
        Level level = this.level();

        // 先获取施法者
        Entity owner = this.getOwner();
        Vec3 forward = null;
        if (owner != null) {
            // 只取水平朝向（y = 0），避免仰角影响
            forward = owner.getLookAngle().multiply(1, 0, 1).normalize();
        }

        if ((float) (this.waveAnim++) < radius) {
            if (!level.isClientSide) {

                if (this.waveAnim % 2 == 0) {
                    float volume = (float) (this.waveAnim + 8) / 16.0F;
                    this.playSound((SoundEvent) SoundRegistry.EARTHQUAKE_IMPACT.get(), volume, (float) Utils.random.nextIntBetweenInclusive(90, 110) * 0.01F);
                }

                float circumferenceMin = (float) ((this.waveAnim - 1) * 2) * 3.14F;
                float circumferenceMax = (float) ((this.waveAnim + 1) * 2) * 3.14F;
                int minBlocks = Mth.clamp((int) circumferenceMin, 0, 60);
                int maxBlocks = Mth.clamp((int) circumferenceMax, 0, 60);
                float anglePerBlockMin = 360.0F / (float) minBlocks;
                float anglePerBlockMax = 360.0F / (float) maxBlocks;

                // ========== 方块环 1 ==========
                for (int i = 0; i < minBlocks; ++i) {
                    Vec3 offset = new Vec3((float) this.waveAnim * Mth.cos(anglePerBlockMin * i * Mth.DEG_TO_RAD), 0.0F,
                            (float) this.waveAnim * Mth.sin(anglePerBlockMin * i * Mth.DEG_TO_RAD));
                    // 判断扇形条件
                    if (forward != null && !isInSector(forward, offset, HALF_DEGREE)) continue;

                    BlockPos blockPos = BlockPos.containing(Utils.moveToRelativeGroundLevel(level, this.position().add(offset), 4)).below();
                    Utils.createTremorBlock(level, blockPos, 0.1F + this.random.nextFloat() * 0.2F);
                }

                // ========== 方块环 2 ==========
                for (int i = 0; i < maxBlocks; ++i) {
                    Vec3 offset = new Vec3((float) (this.waveAnim + 1) * Mth.cos(anglePerBlockMax * i * Mth.DEG_TO_RAD), 0.0F,
                            (float) (this.waveAnim + 1) * Mth.sin(anglePerBlockMax * i * Mth.DEG_TO_RAD));
                    if (forward != null && !isInSector(forward, offset, HALF_DEGREE)) continue;

                    BlockPos blockPos = BlockPos.containing(Utils.moveToRelativeGroundLevel(level, this.position().add(offset), 4).add(0.0, 0.1, 0.0));
                    if (level.getBlockState(blockPos.below()).isFaceSturdy(level, blockPos.below(), Direction.UP)) {
                        float impulse = 0.3F + this.random.nextFloat() * 0.4F;
                        BlockState soulFire = Blocks.SOUL_FIRE.defaultBlockState();
                        if (soulFire.hasProperty(net.minecraft.world.level.block.FireBlock.AGE)) {
                            int age = this.random.nextInt(15);
                            soulFire = soulFire.setValue(net.minecraft.world.level.block.FireBlock.AGE, age);
                        }
                        createTremorStack(level, Blocks.BLUE_ICE.defaultBlockState(), soulFire, blockPos.above(), impulse);
                    }
                }

                // ========== 实体命中 ==========
                List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                        this.getBoundingBox().inflate(this.getInflation().x, this.getInflation().y, this.getInflation().z));
                int r1Sqr = this.waveAnim * this.waveAnim;
                int r2Sqr = (this.waveAnim + 1) * (this.waveAnim + 1);

                for (LivingEntity target : targets) {
                    double distanceSqr = target.distanceToSqr(this);
                    if (this.canHitEntity(target) &&
                            distanceSqr >= (double) r1Sqr &&
                            distanceSqr <= (double) r2Sqr &&
                            this.canHitTargetForGroundContext(target)) {

                        // 判断扇形
                        Vec3 toTarget = target.position().subtract(this.position()).multiply(1, 0, 1).normalize();
                        if (forward != null && angleBetween(forward, toTarget) > HALF_DEGREE) continue;

                        this.applyEffect(target);
                    }
                }
            } else {
                // 粒子部分加 sector 判断
                int particles = (int) ((float) ((this.waveAnim + 1) * 2) * 3.14F * 2.5F);
                float anglePerParticle = (3.14F * 2F) / (float) particles;

                for (int i = 0; i < particles; ++i) {
                    Vec3 trig = new Vec3(Mth.cos(anglePerParticle * i), 0.0F, Mth.sin(anglePerParticle * i));
                    if (forward != null && !isInSector(forward, trig, HALF_DEGREE)) continue;

                    float r = Mth.lerp(Utils.random.nextFloat(), (float) this.waveAnim, (float) (this.waveAnim + 1));
                    Vec3 pos = trig.scale(r).add(Utils.getRandomVec3(0.4)).add(this.position()).add(0.0, 0.5, 0.0);
                    Vec3 motion = trig.add(Utils.getRandomVec3(0.5)).scale(0.1);
                    level.addParticle(ParticleHelper.SNOW_DUST, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
                }
            }
        } else {
            this.discard();
        }
    }

    // 判断偏移是否在扇形内
    private boolean isInSector(Vec3 forward, Vec3 offset, double halfAngleDeg) {
        Vec3 dir = offset.normalize();
        return angleBetween(forward, dir) <= halfAngleDeg;
    }

    private double angleBetween(Vec3 a, Vec3 b) {
        return Math.toDegrees(Math.acos(Mth.clamp((float) a.dot(b), -1.0F, 1.0F)));
    }

    public boolean shouldBeSaved() {
        return false;
    }

    protected boolean canHitTargetForGroundContext(LivingEntity target) {
        return Utils.raycastForBlock(target.level(), target.position(), target.position().add((double) 0.0F, (double) -1.0F, (double) 0.0F), ClipContext.Fluid.NONE).getType() != HitResult.Type.MISS;
    }

    protected Vec3 getInflation() {
        return new Vec3((double) 0.0F, (double) 5.0F, (double) 0.0F);
    }

    public @NotNull EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, 3.0F);
    }

    public Optional<ParticleOptions> getParticle() {
        return Optional.empty();
    }

}
