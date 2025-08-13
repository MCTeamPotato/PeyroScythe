package com.rinko1231.peyroscythe.spellentity;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import com.rinko1231.peyroscythe.init.TagsRegistry;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.network.ClientboundSyncMana;
import io.redspace.ironsspellbooks.setup.Messages;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class DeathSmokeProjectile extends AbstractConeProjectile {

    private static final EntityDataAccessor<Integer> SPELL_LEVEL =
            SynchedEntityData.defineId(DeathSmokeProjectile.class, EntityDataSerializers.INT);

    public DeathSmokeProjectile(EntityType<? extends AbstractConeProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public DeathSmokeProjectile(Level level, LivingEntity entity) {
        super((EntityType) EntityRegistry.DEATH_SMOKE_PROJECTILE.get(), level, entity);
        this.setOwner(entity);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPELL_LEVEL, 1);
    }

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
    public void spawnParticles() {
        Entity owner = this.getOwner();
        if (this.level().isClientSide && owner != null) {
            Vec3 rotation = owner.getLookAngle().normalize();
            Vec3 pos = owner.position().add(rotation.scale(1.6));
            double x = pos.x;
            double y = pos.y + (double) (owner.getEyeHeight() * 0.9F);
            double z = pos.z;
            double speed = this.random.nextDouble() * 0.4 + 0.45;

            for (int i = 0; i < 20; ++i) {
                double offset = 0.25;
                double ox = Math.random() * 2.0 * offset - offset;
                double oy = Math.random() * 2.0 * offset - offset;
                double oz = Math.random() * 2.0 * offset - offset;
                double angularness = 0.8;
                Vec3 randomVec = (new Vec3(Math.random() * 2.0 * angularness - angularness,
                        Math.random() * 2.0 * angularness - angularness,
                        Math.random() * 2.0 * angularness - angularness)).normalize();
                Vec3 result = rotation.scale(3.0F).add(randomVec).normalize().scale(speed);

                this.level().addParticle(ParticleTypes.LARGE_SMOKE,
                        x + ox, y + oy, z + oz,
                        result.x, result.y, result.z);

                this.level().addParticle(ParticleTypes.SQUID_INK,
                        x + ox, y + oy, z + oz,
                        result.x, result.y, result.z);
            }
        }
    }


    @Override
    protected void onHitEntity(EntityHitResult hitResult) {

        Entity target = hitResult.getEntity();
        if (DamageSources.applyDamage(target, this.damage/5, ((AbstractSpell) NewSpellRegistry.DEATH_SMOKE_SPELL.get()).getDamageSource(this, this.getOwner())) && target instanceof LivingEntity livingEntity) {
            {
                if (!target.getType().is(TagsRegistry.DEATH_SMOKE_IMMUNE))//死者之王太捞了，转阶段会被秒
                {
                    // 给目标叠加 deathSmokeMark
                    MobEffectInstance existing = livingEntity.getEffect(MobEffectRegistry.DEATH_SMOKE_EROSION.get());
                    int amp = 0;
                    if (existing != null) {
                        amp = existing.getAmplifier() + 1; // 叠加等级
                    }

                    // 每次命中刷新持续时间
                    livingEntity.addEffect(new MobEffectInstance(
                            MobEffectRegistry.DEATH_SMOKE_EROSION.get(),
                            PeyroScytheConfig.deathSmokeErosionDuration.get(),
                            amp,
                            false,
                            true,
                            true
                    ));
                    if (target instanceof LivingEntity entity) {
                        // amplifier 从 0 开始，所以等级 = amplifier+1
                        int level = amp + 1;

                        // 斩杀阈值
                        double killThreshold = PeyroScytheConfig.deathSmokeBasicKillThreshold.get() + (PeyroScytheConfig.deathSmokeKillThresholdGrowthPerLevel.get() * this.getSpellLevel() * (level - 1));

                        //if(this.getOwner() instanceof ServerPlayer p) p.displayClientMessage(Component.literal("死烟吞噬阈值: "+killThreshold),false);
                        // 当前血量百分比
                        float healthPct = entity.getHealth() / entity.getMaxHealth();

                        if (healthPct <= killThreshold) {

                            entity.removeAllEffects();//避免庇护
                            entity.setHealth(0.001F);//防假死
                            DamageSources.applyDamage(target, entity.getMaxHealth(), NewSpellRegistry.DEATH_SMOKE_SPELL.get().getDamageSource(this, this.getOwner()));
                            if(this.getOwner() instanceof ServerPlayer p)
                            {
                                p.heal((float)(PeyroScytheConfig.deathSmokeHealthToHealthTransferRatePerLevel.get() * this.getSpellLevel() * livingEntity.getMaxHealth()));
                                MagicData magicData = MagicData.getPlayerMagicData(p);
                                magicData.addMana((float) (PeyroScytheConfig.deathSmokeHealthToManaTransferRatePerLevel.get() * this.getSpellLevel() * livingEntity.getMaxHealth()));
                                Messages.sendToPlayer(new ClientboundSyncMana(magicData), p);
                            }
                        }
                    }
                }
            }
        }
    }
}