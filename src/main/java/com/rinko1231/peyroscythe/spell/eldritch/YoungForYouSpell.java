package com.rinko1231.peyroscythe.spell.eldritch;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.Level;

import java.util.Optional;

    @AutoSpellConfig
    public class YoungForYouSpell extends AbstractSpell {
        private final ResourceLocation spellId = PeyroScythe.id( "young_for_you");
        private final DefaultConfig defaultConfig;

        public YoungForYouSpell() {
            this.defaultConfig = new DefaultConfig()
                    .setMinRarity(SpellRarity.LEGENDARY)
                    .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
                    .setMaxLevel(2)
                    .setCooldownSeconds(45)
                    .build();
            this.manaCostPerLevel = 5;
            this.baseSpellPower = 0;
            this.spellPowerPerLevel = 0;
            this.castTime = 20;
            this.baseManaCost = 80;
        }

        @Override
        public boolean allowLooting() {
            return PeyroScytheConfig.youngForYouAllowLooting.get();
        }

        @Override
        public CastType getCastType() {
            return CastType.LONG;
        }

        @Override
        public DefaultConfig getDefaultConfig() {
            return this.defaultConfig;
        }

        @Override
        public ResourceLocation getSpellResource() {
            return this.spellId;
        }

        public Optional<SoundEvent> getCastStartSound() {
            return Optional.of((SoundEvent) SoundEvents.EVOKER_PREPARE_WOLOLO);
        }

        public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
            return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, 0.35F, true, (livingEntity) -> {
                boolean var10000;
                if (livingEntity instanceof AgeableMob sheep || livingEntity instanceof Piglin piglin || livingEntity instanceof Zombie zombie || livingEntity instanceof Zoglin zoglin) {
                    var10000 = true;
                } else {
                    var10000 = false;
                }

                return var10000;
            });
        }

        public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
            ICastData targetEntity = playerMagicData.getAdditionalCastData();
            if (targetEntity instanceof TargetEntityCastData healTargetingData) {
                LivingEntity target = healTargetingData.getTarget((ServerLevel) world);
                if (target instanceof AgeableMob ageableMob) {
                    ageableMob.setBaby(true);
                    if (spellLevel >= 2)
                        target.addEffect(new MobEffectInstance(MobEffectRegistry.FOREVER_YOUNG, -1, 0, false, false, false));
                    MagicManager.spawnParticles(world, ParticleTypes.CRIT, ageableMob.getX(), ageableMob.getY() + 0.6, ageableMob.getZ(), 25, (double) 0.5F, (double) 0.5F, (double) 0.5F, (double) 0.0F, false);
                    MagicManager.spawnParticles(world, ParticleTypes.LARGE_SMOKE, ageableMob.getX(), ageableMob.getY() + 0.6, ageableMob.getZ(), 25, (double) 0.5F, (double) 0.5F, (double) 0.5F, (double) 0.0F, false);
                } else if (target instanceof Zombie zombie) {
                    zombie.setBaby(true);
                    if (spellLevel >= 2)
                        target.addEffect(new MobEffectInstance(MobEffectRegistry.FOREVER_YOUNG, -1, 0, false, false, false));
                    MagicManager.spawnParticles(world, ParticleTypes.CRIT, zombie.getX(), zombie.getY() + 0.6, zombie.getZ(), 25, (double) 0.5F, (double) 0.5F, (double) 0.5F, (double) 0.0F, false);
                    MagicManager.spawnParticles(world, ParticleTypes.LARGE_SMOKE, zombie.getX(), zombie.getY() + 0.6, zombie.getZ(), 25, (double) 0.5F, (double) 0.5F, (double) 0.5F, (double) 0.0F, false);
                } else if (target instanceof Piglin piglin) {
                    piglin.setBaby(true);
                    if (spellLevel >= 2)
                        target.addEffect(new MobEffectInstance(MobEffectRegistry.FOREVER_YOUNG, -1, 0, false, false, false));
                    MagicManager.spawnParticles(world, ParticleTypes.CRIT, piglin.getX(), piglin.getY() + 0.6, piglin.getZ(), 25, (double) 0.5F, (double) 0.5F, (double) 0.5F, (double) 0.0F, false);
                    MagicManager.spawnParticles(world, ParticleTypes.LARGE_SMOKE, piglin.getX(), piglin.getY() + 0.6, piglin.getZ(), 25, (double) 0.5F, (double) 0.5F, (double) 0.5F, (double) 0.0F, false);
                } else if (target instanceof Zoglin zoglin) {
                    zoglin.setBaby(true);
                    if (spellLevel >= 2)
                        target.addEffect(new MobEffectInstance(MobEffectRegistry.FOREVER_YOUNG, -1, 0, false, false, false));
                    MagicManager.spawnParticles(world, ParticleTypes.CRIT, zoglin.getX(), zoglin.getY() + 0.6, zoglin.getZ(), 25, (double) 0.5F, (double) 0.5F, (double) 0.5F, (double) 0.0F, false);
                    MagicManager.spawnParticles(world, ParticleTypes.LARGE_SMOKE, zoglin.getX(), zoglin.getY() + 0.6, zoglin.getZ(), 25, (double) 0.5F, (double) 0.5F, (double) 0.5F, (double) 0.0F, false);
                }

            }

            super.onCast(world, spellLevel, entity, castSource, playerMagicData);
        }

    }
