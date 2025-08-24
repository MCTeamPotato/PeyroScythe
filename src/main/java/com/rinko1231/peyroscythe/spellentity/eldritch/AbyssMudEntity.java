package com.rinko1231.peyroscythe.spellentity.eldritch;

import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import com.rinko1231.peyroscythe.spell.eldritch.ChaosCradleSpell;
import io.redspace.ironsspellbooks.api.entity.NoKnockbackProjectile;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public class AbyssMudEntity extends Projectile implements NoKnockbackProjectile {
    // —— 同步数据 —— //
    private static final EntityDataAccessor<Float> DATA_RADIUS =
            SynchedEntityData.defineId(AbyssMudEntity.class, EntityDataSerializers.FLOAT);

    // —— 运行参数 —— //
    private static final int OWNER_CHECK_INTERVAL = 30 * 20; // 30s
    private static final int DAMAGE_PERIOD = 20;             // 每秒伤害一次
    private static final int DURATION_DEFAULT = 300;        // 存活 6000t

    // —— 状态 —— //
    private UUID ownerUuid;
    private int ownerCheckTicker;
    private int duration = DURATION_DEFAULT;


    public AbyssMudEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setDeltaMovement(Vec3.ZERO);
    }
    public AbyssMudEntity(Level level) { this(EntityRegistry.ABYSS_MUD.get(), level); }

    // —— 基本属性 —— //
    public void setDuration(int duration) { this.duration = duration; }
    public void setRadius(float r) {
        if (!level().isClientSide) this.entityData.set(DATA_RADIUS, Mth.clamp(r, 0.0F, 32.0F));
    }
    public float getRadius() { return this.entityData.get(DATA_RADIUS); }

    private int spellLevel = 1;
    public void setSpellLevel(int a)
    {
        this.spellLevel = a;
    }
    public int getSpellLevel() {
        return this.spellLevel;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_RADIUS, 2.0F);
    }

    // 不因半径变化在客户端 discard（避免“看不见但还在”的错觉）
    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        if (DATA_RADIUS.equals(key)) this.refreshDimensions();
        super.onSyncedDataUpdated(key);
    }

    @Override
    public void refreshDimensions() {
        double x = getX(), y = getY(), z = getZ();
        super.refreshDimensions();
        setPos(x, y, z);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        // 地毯式扁平体
        return EntityDimensions.scalable(getRadius() * 2.0F, 0.8F);
    }

    @Override
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        if (owner instanceof LivingEntity living) ownerUuid = living.getUUID();
    }

    // —— Tick —— //
    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            // 1) 寿命
            if (this.tickCount > duration) {
                this.discard();
                return;
            }

            final float r = getRadius();
            final double r2 = r * r;

            // 2) 主人在圈内 → 每 tick 刷 21t 的 abyss_undead
            LivingEntity owner = getOwner() instanceof LivingEntity le ? le : null;
            if (owner != null && owner.level() == this.level()) {
                double dy = owner.getY() - this.getY();
                if (Math.abs(dy) <= 2.0D) {
                    double dx = owner.getX() - this.getX();
                    double dz = owner.getZ() - this.getZ();
                    if (dx*dx + dz*dz <= r2) {
                        owner.addEffect(new MobEffectInstance(
                                MobEffectRegistry.ABYSSAL_GRACE.get(),
                                21, 0, false, true, true));
                    }
                }
            }

            // 3) 每秒对外伤害一次
            if ((this.tickCount % DAMAGE_PERIOD) == 0) {
                AABB box = new AABB(getX()-r, getY()-2, getZ()-r,
                                    getX()+r, getY()+2, getZ()+r);
                List<LivingEntity> targets = this.level().getEntitiesOfClass(
                        LivingEntity.class, box,
                        e -> e.isAlive() && !e.isSpectator());

                for (LivingEntity t : targets) {
                    if (t == owner) continue;
                    double dx = t.getX() - this.getX();
                    double dz = t.getZ() - this.getZ();
                    if (dx*dx + dz*dz <= r2) {
                        if (owner == null || DamageSources.isFriendlyFireBetween(owner, t)) continue;
                        DamageSources.ignoreNextKnockback(t);
                        DamageSources.applyDamage(
                                t,
                                t.getMaxHealth() * (0.01f * this.spellLevel),
                                ((AbstractSpell) NewSpellRegistry.DEATH_SMOKE_SPELL.get())
                                        .getDamageSource(this, owner)
                        );
                    }
                }
            }

            // 4) 每 30s：不在同维或找不到主人 → 自散
            if (++ownerCheckTicker >= OWNER_CHECK_INTERVAL) {
                ownerCheckTicker = 0;
                ServerLevel here = (ServerLevel) this.level();
                boolean sameDimOwner = false;

                if (owner instanceof ServerPlayer serverPlayer) {
                    sameDimOwner = (serverPlayer.level() == here);
                } else if (ownerUuid != null) {
                    ServerPlayer sp = here.getServer().getPlayerList().getPlayer(ownerUuid);
                    sameDimOwner = (sp != null && sp.level() == here);
                    if (owner == null && sp != null) super.setOwner(sp); // 可选：补回引用
                }

                if (!sameDimOwner) {
                    this.discard();
                    return;
                }
            }
        } else {
            // 客户端：粒子
            this.ambientParticles();
        }
    }

    // —— 存取 —— //
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Radius", getRadius());
        tag.putInt("Age", this.tickCount);
        tag.putInt("Duration", this.duration);
        tag.putInt("SpellLevel", this.spellLevel);
        if (ownerUuid != null) tag.putUUID("OwnerUUID", ownerUuid);
        super.addAdditionalSaveData(tag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.tickCount = tag.getInt("Age");
        if (tag.contains("Radius")) setRadius(tag.getFloat("Radius"));
        if (tag.contains("Duration")) this.duration = tag.getInt("Duration");
        if (tag.hasUUID("OwnerUUID")) ownerUuid = tag.getUUID("OwnerUUID");
        if (tag.contains("SpellLevel")) this.spellLevel = tag.getInt("SpellLevel");

        super.readAdditionalSaveData(tag);
    }

    // —— 视觉（与你现有一致）—— //
    public float getParticleCount() { return 0.10F * this.getRadius(); }
    protected float particleYOffset() { return 0.25F; }
    protected float getParticleSpeedModifier() { return 1.2F; }

    public Optional<ParticleOptions> getParticle() { return Optional.empty(); }

    public void ambientParticles() {
        if (!this.level().isClientSide) return;
        this.ambientParticles(ParticleHelper.VOID_TENTACLE_FOG, 0.6f);
        this.ambientParticles(ParticleHelper.UNSTABLE_ENDER);
        this.ambientParticles(ParticleTypes.LARGE_SMOKE, 0.6f);
    }
    public void ambientParticles(ParticleOptions particle) { ambientParticles(particle, 1.0f); }
    public void ambientParticles(ParticleOptions particle, float rate) {
        float f = this.getParticleCount() * rate;
        f = Mth.clamp(f * this.getRadius(), f / 4.0F, f * 10.0F);
        for (int i = 0; (float)i < f; ++i) {
            if (f - (float)i < 1.0F && this.random.nextFloat() > f - (float)i) return;
            float r = this.getRadius();
            Vec3 pos;
            if (true /* circular */) {
                float distance = r * (1.0F - this.random.nextFloat() * this.random.nextFloat());
                float theta = this.random.nextFloat() * (float)(Math.PI * 2);
                pos = new Vec3(distance * Mth.cos(theta), 0.2F, distance * Mth.sin(theta));
            } else {
                pos = new Vec3(Utils.getRandomScaled(r * 0.85F), 0.2F, Utils.getRandomScaled(r * 0.85F));
            }
            Vec3 motion = new Vec3(Utils.getRandomScaled(0.03F), this.random.nextDouble() * 0.01F, Utils.getRandomScaled(0.03F))
                    .scale(this.getParticleSpeedModifier());
            Vec3 base = new Vec3(this.getX() + pos.x, this.getY() + pos.y + 1.0F, this.getZ() + pos.z);
            base = Utils.moveToRelativeGroundLevel(this.level(), base, 1, 2);
            this.level().addParticle(particle, base.x, base.y + this.particleYOffset(), base.z, motion.x, motion.y, motion.z);
        }
    }

    @Override public boolean isPushedByFluid(FluidType type) { return false; }
    @Override public boolean isOnFire() { return false; }
    @Override public boolean displayFireAnimation() { return false; }
}

