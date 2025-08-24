package com.rinko1231.peyroscythe.spellentity;



import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.SummonTimer;
import io.redspace.ironsspellbooks.entity.mobs.MagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.goals.*;
import io.redspace.ironsspellbooks.entity.spells.firebolt.FireboltProjectile;
import io.redspace.ironsspellbooks.spells.fire.FireboltSpell;
import io.redspace.ironsspellbooks.util.OwnerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;


import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class SummonedBlaze extends Blaze implements MagicSummon {

    // —— 自己管理的“充能”外观标志（驱动燃烧特效）——
    private static final EntityDataAccessor<Boolean> SUMMONED_CHARGED =
            SynchedEntityData.defineId(SummonedBlaze.class, EntityDataSerializers.BOOLEAN);

    // —— 召唤者 —— //
    protected LivingEntity cachedSummoner;
    protected UUID         summonerUUID;

    public SummonedBlaze(EntityType<? extends Blaze> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.xpReward = 0;
    }

    public SummonedBlaze(Level level, LivingEntity owner) {
        this((EntityType<? extends Blaze>) EntityRegistry.SUMMONED_BLAZE.get(), level);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setSummoner(owner);
    }



    // 同步数据
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SUMMONED_CHARGED, false);
    }

    private void setChargedSummoned(boolean value) {
        this.entityData.set(SUMMONED_CHARGED, value);
    }

    private boolean isChargedSummoned() {
        return this.entityData.get(SUMMONED_CHARGED);
    }

    // 原版 Blaze 用 isOnFire() 显示“充能状态”，我们用自己的同步位来控制
    @Override
    public boolean isOnFire() {
        return isChargedSummoned();
    }

    // —— AI —— //
    @Override
    protected void registerGoals() {
        // 攻击：沿用原版喷火节奏
        this.goalSelector.addGoal(6, new SummonedBlazeAttackGoal(this));
        this.goalSelector.addGoal(7, new MoveTowardsRestrictionGoal(this, 1.0D));
        // 跟随主人
        this.goalSelector.addGoal(5, new GenericFollowOwnerGoal(this, this::getSummoner, 1.0D, 32.0f, 8.0f, true, 48.0f));
        // 漫步&注视
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(16, new RandomLookAroundGoal(this));

        // 仇恨：只复制主人的目标/被打目标，不默认敌对玩家
        this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
        this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
        this.targetSelector.addGoal(3, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
        this.targetSelector.addGoal(4, (new GenericHurtByTargetGoal(this, (entity) -> entity == this.getSummoner())).setAlertOthers(new Class[0]));

    }

    // 睡觉判定：对友军玩家不捣乱
    @Override
    public boolean isPreventingPlayerRest(@NotNull Player player) {
        return !this.isAlliedTo(player);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity victim) {
        // 近战（贴身烧）伤害的来源走你的法术来源
        return Utils.doMeleeAttack(this, victim,
                ((AbstractSpell) NewSpellRegistry.SUMMON_BLAZE.get())
                        .getDamageSource(this, this.getSummoner()));
    }

    @Override
    public boolean hurt(@NotNull DamageSource src, float amt) {
        return !this.shouldIgnoreDamage(src) && super.hurt(src, amt);
    }
    public static AttributeSupplier.Builder createAttributes() {
        // 等同于原版 Blaze 的属性；如需血量等可自行添加
        return Monster.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
        // .add(Attributes.MAX_HEALTH, 20.0D); // 若想自定义血量，就加上
    }

    // —— 召唤者存取 —— //
    @Nullable
    public LivingEntity getSummoner() {
        return OwnerHelper.getAndCacheOwner(this.level(), this.cachedSummoner, this.summonerUUID);
    }

    public void setSummoner(@Nullable LivingEntity owner) {
        if (owner != null) {
            this.summonerUUID  = owner.getUUID();
            this.cachedSummoner = owner;
        }
    }

    // —— 生命周期钩子（与 SummonedVex 一致）—— //
    @Override
    public void die(@NotNull DamageSource source) {
        this.onDeathHelper();
        super.die(source);
    }

    @Override
    public void onRemovedFromWorld() {
        this.onRemovedHelper(this, (SummonTimer) MobEffectRegistry.SUMMON_BLAZE_TIMER.get());
        super.onRemovedFromWorld();
    }

    @Override
    public void onUnSummon() {
        if (!this.level().isClientSide) {
            MagicManager.spawnParticles(this.level(), net.minecraft.core.particles.ParticleTypes.POOF,
                    this.getX(), this.getY(), this.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);
            this.discard();
        }
    }

    @Override
    public boolean isAlliedTo(@NotNull Entity other) {
        return super.isAlliedTo(other) || this.isAlliedHelper(other);
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    // —— 存档 —— //
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        OwnerHelper.serializeOwner(tag, this.summonerUUID);
        tag.putBoolean("SummonedCharged", this.isChargedSummoned());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.summonerUUID = OwnerHelper.deserializeOwner(tag);
        if (tag.contains("SummonedCharged")) {
            this.setChargedSummoned(tag.getBoolean("SummonedCharged"));
        }
    }

    // ==========================
    //   攻击 AI：喷火/充能节奏
    // ==========================
    static class SummonedBlazeAttackGoal extends Goal {
        private final SummonedBlaze blaze;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public SummonedBlazeAttackGoal(SummonedBlaze owner) {
            this.blaze = owner;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = blaze.getTarget();
            return target != null && target.isAlive() && blaze.canAttack(target);
        }

        @Override
        public void start() {
            this.attackStep = 0;
        }

        @Override
        public void stop() {
            blaze.setChargedSummoned(false);
            this.lastSeen = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() { return true; }

        @Override
        public void tick() {
            --this.attackTime;
            LivingEntity target = blaze.getTarget();
            if (target == null) return;

            boolean canSee = blaze.getSensing().hasLineOfSight(target);
            if (canSee) this.lastSeen = 0; else this.lastSeen++;

            double d0 = blaze.distanceToSqr(target);
            if (d0 < 4.0D) {
                if (!canSee) return;
                if (this.attackTime <= 0) {
                    this.attackTime = 20;
                    blaze.doHurtTarget(target); // 贴身灼伤
                }
                blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);
            } else if (d0 < (getFollowDistance()/3) * (getFollowDistance()/3) && canSee) {
                double dx = target.getX() - blaze.getX();
                double dy = target.getY(0.5D) - blaze.getY(0.5D);
                double dz = target.getZ() - blaze.getZ();

                if (this.attackTime <= 0) {
                    ++this.attackStep;
                    if (this.attackStep == 1) {
                        this.attackTime = 60;
                        blaze.setChargedSummoned(true);
                    } else if (this.attackStep <= 4) {
                        this.attackTime = 6;
                    } else {
                        this.attackTime = 100;
                        this.attackStep = 0;
                        blaze.setChargedSummoned(false);
                    }


                    if (this.attackStep > 1) {
                        double spread = Math.sqrt(Math.sqrt(d0)) * 0.5D;
                        if (!blaze.isSilent()) {
                            blaze.level().levelEvent(null, 1018, blaze.blockPosition(), 0);
                        }

                        // === 用 FireboltProjectile 取代 SmallFireball ===
                        // 1) 构造并设定初始位置（对齐玩家那段：眼高 - 1/2弹体高度）
                        FireboltProjectile bolt = new FireboltProjectile(blaze.level(), blaze);
                        bolt.setPos(
                                blaze.getX(),
                                blaze.getEyeY() - bolt.getBoundingBox().getYsize() * 0.5D,
                                blaze.getZ()
                        );

                        // 2) 计算“带散布”的朝向向量（保持与小火球相同的三角分布扰动）
                        double vx = blaze.getRandom().triangle(dx, 2.297D * spread);
                        double vy = dy;
                        double vz = blaze.getRandom().triangle(dz, 2.297D * spread);
                        Vec3 dir = new Vec3(vx, vy, vz).normalize();


                        bolt.shoot(dir);
                        bolt.setOwner(blaze.getSummoner());

                        bolt.setDamage(0.8f * SpellRegistry.FIREBOLT_SPELL.get().getDamageSource(blaze.getSummoner()).spell().getSpellPower(1, blaze.getSummoner()));

                        blaze.level().addFreshEntity(bolt);
                    }

                }
                blaze.getLookControl().setLookAt(target, 10.0F, 10.0F);
            } else if (this.lastSeen < 5) {
                blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);
            }
        }

        private double getFollowDistance() {
            return blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }
}
