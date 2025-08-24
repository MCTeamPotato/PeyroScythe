package com.rinko1231.peyroscythe.spell.fire;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.spellentity.SummonedBlaze;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@AutoSpellConfig
public class SummonBlazeSpell extends AbstractSpell {
    private final ResourceLocation spellId = PeyroScythe.id("summon_blaze");
    private final DefaultConfig defaultConfig;

    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.summon_count",
                        new Object[]{Utils.stringTruncation(spellLevel , 1)}),
                Component.translatable("ui.peyroscythe.senator_possibility",
                        new Object[]{Utils.stringTruncation( (double) this.getSenatorPossibility(spellLevel, caster) * 100.0D, 2)})
       );
    }

    public SummonBlazeSpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.RARE)
                .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
                .setMaxLevel(5)
                .setCooldownSeconds((double)150.0F).build();
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 5;
        this.castTime = 20;
        this.baseManaCost = 60;
    }

    @Override
    public boolean allowLooting() {
        return PeyroScytheConfig.summonBlazeAllowLooting.get();
    }

    public CastType getCastType() {
        return CastType.LONG;
    }

    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    public ResourceLocation getSpellResource() {
        return this.spellId;
    }

    public double getSenatorPossibility(int spellLevel, LivingEntity caster)
    {
        return 0.19 + this.getSpellPower(spellLevel,caster) * 0.001;
    }

    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.EVOKER_PREPARE_SUMMON);
    }

    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.EVOKER_CAST_SPELL);
    }

    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        int summonTime = 9600;

        for(int i = 0; i < spellLevel; ++i) {
            SummonedBlaze summonedBlaze = new SummonedBlaze(world, entity);

            Random random = new Random();
            float roll = random.nextFloat();
            summonedBlaze.setCustomNameVisible(false);
            if(roll < (float)this.getSenatorPossibility(spellLevel,entity))
            {

                AttributeInstance maxHpAttr = summonedBlaze.getAttribute(Attributes.MAX_HEALTH);
                if (maxHpAttr != null) {
                    maxHpAttr.setBaseValue(maxHpAttr.getBaseValue() * (1.0D + PeyroScytheConfig.summonBlazeSenatorExtraHealth.get()));
                    summonedBlaze.setHealth((float) maxHpAttr.getValue());
                }
                summonedBlaze.addEffect(new MobEffectInstance(MobEffects.REGENERATION, -1, 2, false, false, true));
                summonedBlaze.setCustomName(
                        Component.translatable("entity.peyroscythe.summoned_blaze.senator.custom_name")
                                .withStyle(style -> style.withItalic(false)) // 关斜体
                );
            }
            else {

                summonedBlaze.setCustomName(
                        Component.translatable("entity.peyroscythe.summoned_blaze.custom_name")
                                .withStyle(style -> style.withItalic(false)) // 关斜体
                );
                summonedBlaze.addEffect(new MobEffectInstance(MobEffects.REGENERATION, summonTime/2, 1, false, false, true));

            }


            summonedBlaze.moveTo(entity.getEyePosition().add(new Vec3(Utils.getRandomScaled((double)2.0F), (double)1.0F, Utils.getRandomScaled((double)2.0F))));
            summonedBlaze.finalizeSpawn((ServerLevel)world, world.getCurrentDifficultyAt(summonedBlaze.getOnPos()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);

            summonedBlaze.setTarget(null);                // 清除攻击目标
            summonedBlaze.setLastHurtByMob(null);         // 清除最近伤害来源
            summonedBlaze.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);

            summonedBlaze.addEffect(new MobEffectInstance((MobEffect) MobEffectRegistry.SUMMON_BLAZE_TIMER.get(), summonTime, 0, false, false, false));
            world.addFreshEntity(summonedBlaze);
        }

        int effectAmplifier = spellLevel - 1;
        if (entity.hasEffect((MobEffect)MobEffectRegistry.SUMMON_BLAZE_TIMER.get())) {
            effectAmplifier += entity.getEffect((MobEffect)MobEffectRegistry.SUMMON_BLAZE_TIMER.get()).getAmplifier() + 1;
        }

        entity.addEffect(new MobEffectInstance((MobEffect)MobEffectRegistry.SUMMON_BLAZE_TIMER.get(), summonTime, effectAmplifier, false, false, true));
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }
}
