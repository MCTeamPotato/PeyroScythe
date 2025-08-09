package com.rinko1231.peyroscythe.spell;


import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.VisualFallingBlockEntity;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class FireEruptionAoe extends AoeEntity {
    int waveAnim;

    public FireEruptionAoe(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.waveAnim = -1;
        this.reapplicationDelay = 25;
        this.setCircular();
    }

    public FireEruptionAoe(Level level, float radius) {
        this(EntityRegistry.FIRE_ERUPTION_AOE.get(), level);
        this.setRadius(radius);
    }



    public void applyEffect(LivingEntity target) {
        SpellDamageSource damageSource = ((AbstractSpell) NewSpellRegistry.RAISE_HELL_SPELL.get()).getDamageSource((Entity)(this.getOwner() == null ? this : this.getOwner()));
        DamageSources.ignoreNextKnockback(target);
        if (target.hurt(damageSource, this.getDamage())) {
            target.setSecondsOnFire(5);
            target.setDeltaMovement(target.getDeltaMovement().add((double)0.0F, 0.65, (double)0.0F));
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
        if ((float)(this.waveAnim++) < radius) {
            if (!level.isClientSide) {
                if (this.waveAnim % 2 == 0) {
                    float volume = (float)(this.waveAnim + 8) / 16.0F;
                    this.playSound((SoundEvent) SoundRegistry.EARTHQUAKE_IMPACT.get(), volume, (float) Utils.random.nextIntBetweenInclusive(90, 110) * 0.01F);
                }

                float circumferenceMin = (float)((this.waveAnim - 1) * 2) * 3.14F;
                float circumferenceMax = (float)((this.waveAnim + 1) * 2) * 3.14F;
                int minBlocks = Mth.clamp((int)circumferenceMin, 0, 60);
                int maxBlocks = Mth.clamp((int)circumferenceMax, 0, 60);
                float anglePerBlockMin = 360.0F / (float)minBlocks;
                float anglePerBlockMax = 360.0F / (float)maxBlocks;

                for(int i = 0; i < minBlocks; ++i) {
                    Vec3 vec3 = new Vec3((double)((float)this.waveAnim * Mth.cos(anglePerBlockMin * (float)i)), (double)0.0F, (double)((float)this.waveAnim * Mth.sin(anglePerBlockMin * (float)i)));
                    BlockPos blockPos = BlockPos.containing(Utils.moveToRelativeGroundLevel(level, this.position().add(vec3), 4)).below();
                    Utils.createTremorBlock(level, blockPos, 0.1F + this.random.nextFloat() * 0.2F);
                }

                for(int i = 0; i < maxBlocks; ++i) {
                    Vec3 vec3 = new Vec3((double)((float)(this.waveAnim + 1) * Mth.cos(anglePerBlockMax * (float)i)), (double)0.0F, (double)((float)(this.waveAnim + 1) * Mth.sin(anglePerBlockMax * (float)i)));
                    BlockPos blockPos = BlockPos.containing(Utils.moveToRelativeGroundLevel(level, this.position().add(vec3), 4).add((double)0.0F, 0.1, (double)0.0F));
                   /* if (level.getBlockState(blockPos.below()).isFaceSturdy(level, blockPos.below(), Direction.UP)) {
                        createTremorBlockWithState(level, Blocks.FIRE.defaultBlockState(), blockPos, 0.1F + this.random.nextFloat() * 0.2F);
                    }*/
                    if (level.getBlockState(blockPos.below()).isFaceSturdy(level, blockPos.below(), Direction.UP)) {
                        float impulse = 0.1F + this.random.nextFloat() * 0.2F;

                        // 可选：让火焰有随机“年龄”，更有燃烧层次
                        BlockState fire = Blocks.FIRE.defaultBlockState();
                        if (fire.hasProperty(net.minecraft.world.level.block.FireBlock.AGE)) {
                            int age = this.random.nextInt(15); // 0~15
                            fire = fire.setValue(net.minecraft.world.level.block.FireBlock.AGE, age);
                        }

                        createTremorStack(level, Blocks.NETHERRACK.defaultBlockState(), fire, blockPos, impulse);
                    }
                }

                List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(this.getInflation().x, this.getInflation().y, this.getInflation().z));
                int r1Sqr = this.waveAnim * this.waveAnim;
                int r2Sqr = (this.waveAnim + 1) * (this.waveAnim + 1);

                for(LivingEntity target : targets) {
                    double distanceSqr = target.distanceToSqr(this);
                    if (this.canHitEntity(target) && distanceSqr >= (double)r1Sqr && distanceSqr <= (double)r2Sqr && this.canHitTargetForGroundContext(target)) {
                        this.applyEffect(target);
                    }
                }
            } else {
                int particles = (int)((float)((this.waveAnim + 1) * 2) * 3.14F * 2.5F);
                float anglePerParticle = ((float)Math.PI * 2F) / (float)particles;

                for(int i = 0; i < particles; ++i) {
                    Vec3 trig = new Vec3((double)Mth.cos(anglePerParticle * (float)i), (double)0.0F, (double)Mth.sin(anglePerParticle * (float)i));
                    float r = Mth.lerp(Utils.random.nextFloat(), (float)this.waveAnim, (float)(this.waveAnim + 1));
                    Vec3 pos = trig.scale((double)r).add(Utils.getRandomVec3(0.4)).add(this.position()).add((double)0.0F, (double)0.5F, (double)0.0F);
                    Vec3 motion = trig.add(Utils.getRandomVec3((double)0.5F)).scale(0.1);
                    level.addParticle(ParticleHelper.FIRE, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
                }
            }
        } else {
            this.discard();
        }

    }

    public boolean shouldBeSaved() {
        return false;
    }

    protected boolean canHitTargetForGroundContext(LivingEntity target) {
        return Utils.raycastForBlock(target.level(), target.position(), target.position().add((double)0.0F, (double)-1.0F, (double)0.0F), ClipContext.Fluid.NONE).getType() != HitResult.Type.MISS;
    }

    protected Vec3 getInflation() {
        return new Vec3((double)0.0F, (double)5.0F, (double)0.0F);
    }

    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, 3.0F);
    }


    public Optional<ParticleOptions> getParticle() {
        return Optional.empty();
    }


    public static void createTremorBlock(Level level, BlockPos blockPos, float impulseStrength) {
        if (level.getBlockState(blockPos.above()).isAir() || level.getBlockState(blockPos.above().above()).isAir()) {
            VisualFallingBlockEntity fallingblockentity = new VisualFallingBlockEntity(level, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), level.getBlockState(blockPos), 10);
            fallingblockentity.setDeltaMovement((double)0.0F, (double)impulseStrength, (double)0.0F);
            level.addFreshEntity(fallingblockentity);
            if (!level.getBlockState(blockPos.above()).isAir()) {
                VisualFallingBlockEntity fallingblockentity2 = new VisualFallingBlockEntity(level, (double)blockPos.getX(), (double)(blockPos.getY() + 1), (double)blockPos.getZ(), level.getBlockState(blockPos.above()), 10);
                fallingblockentity2.setDeltaMovement((double)0.0F, (double)impulseStrength, (double)0.0F);
                level.addFreshEntity(fallingblockentity2);
            }
        }

    }
    public static void createTremorBlockWithState(Level level, BlockState state, BlockPos blockPos, float impulseStrength) {
        VisualFallingBlockEntity fallingblockentity = new VisualFallingBlockEntity(level, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), state, 10);
        fallingblockentity.setDeltaMovement((double)0.0F, (double)impulseStrength, (double)0.0F);
        level.addFreshEntity(fallingblockentity);
    }
    public static void createTremorStack(Level level, BlockState baseState, BlockState topState, BlockPos blockPos, float impulseStrength) {
        // 底座：地狱岩
        VisualFallingBlockEntity base = new VisualFallingBlockEntity(
                level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), baseState, 10
        );
        base.setDeltaMovement(0.0, impulseStrength, 0.0);
        level.addFreshEntity(base);

        // 顶层：火（放在上一格，和底座同速上抛，保持堆叠关系）
        VisualFallingBlockEntity top = new VisualFallingBlockEntity(
                level, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), topState, 10
        );
        top.setDeltaMovement(0.0, impulseStrength, 0.0);
        level.addFreshEntity(top);
    }

}
