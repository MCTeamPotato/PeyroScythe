package com.rinko1231.peyroscythe.spell.fire;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.spellentity.SummonedBlaze;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;

import io.redspace.ironsspellbooks.capabilities.magic.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

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

    public float extraHealthBasedOnSpellPower(int spellLevel, LivingEntity caster)
    {
        return  0.2f * this.getSpellPower(spellLevel,caster);
    }

    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
        if (!recasts.hasRecastForSpell(this)) {
            int summonTime = 9600;
            SummonedEntitiesCastData summonedEntitiesCastData = new SummonedEntitiesCastData();
            for (int i = 0; i < spellLevel; ++i) {
                SummonedBlaze summonedBlaze = new SummonedBlaze(world, entity);

                Random random = new Random();
                float roll = random.nextFloat();
                summonedBlaze.setCustomNameVisible(false);
                if (roll < (float) this.getSenatorPossibility(spellLevel, entity)) {

                    AttributeInstance maxHpAttr = summonedBlaze.getAttribute(Attributes.MAX_HEALTH);
                    if (maxHpAttr != null) {
                        maxHpAttr.setBaseValue((maxHpAttr.getBaseValue() + extraHealthBasedOnSpellPower(spellLevel, entity)) * (1.0D + PeyroScytheConfig.summonBlazeSenatorExtraHealth.get()));
                        summonedBlaze.setHealth((float) maxHpAttr.getValue());
                    }
                    summonedBlaze.addEffect(new MobEffectInstance(MobEffects.REGENERATION, -1, 2, false, false, true));
                    summonedBlaze.setCustomName(
                            Component.translatable("entity.peyroscythe.summoned_blaze.senator.custom_name")
                                    .withStyle(style -> style.withItalic(false)) // 关斜体
                    );
                } else {

                    AttributeInstance maxHpAttr = summonedBlaze.getAttribute(Attributes.MAX_HEALTH);
                    if (maxHpAttr != null) {
                        maxHpAttr.setBaseValue(maxHpAttr.getBaseValue() + extraHealthBasedOnSpellPower(spellLevel, entity));
                        summonedBlaze.setHealth((float) maxHpAttr.getValue());
                    }
                    summonedBlaze.setCustomName(
                            Component.translatable("entity.peyroscythe.summoned_blaze.custom_name")
                                    .withStyle(style -> style.withItalic(false)) // 关斜体
                    );
                    summonedBlaze.addEffect(new MobEffectInstance(MobEffects.REGENERATION, summonTime / 2, 1, false, false, true));

                }


                summonedBlaze.moveTo(entity.getEyePosition().add(new Vec3(Utils.getRandomScaled((double) 2.0F), (double) 1.0F, Utils.getRandomScaled((double) 2.0F))));
                summonedBlaze.finalizeSpawn((ServerLevel) world, world.getCurrentDifficultyAt(summonedBlaze.getOnPos()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null);

                summonedBlaze.setTarget(null);                // 清除攻击目标
                summonedBlaze.setLastHurtByMob(null);         // 清除最近伤害来源
                summonedBlaze.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);

                //summonedBlaze.addEffect(new MobEffectInstance(MobEffectRegistry.SUMMON_BLAZE_TIMER, summonTime, 0, false, false, false));
                Monster creature = (Monster) ((SpellSummonEvent) NeoForge.EVENT_BUS.post(new SpellSummonEvent(entity, summonedBlaze, this.spellId, spellLevel))).getCreature();
                world.addFreshEntity(creature);
                SummonManager.initSummon(entity, creature, summonTime, summonedEntitiesCastData);
            }
            RecastInstance recastInstance = new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTime, castSource, summonedEntitiesCastData);
            recasts.addRecast(recastInstance, playerMagicData);
            //int effectAmplifier = spellLevel - 1;
            //if (entity.hasEffect(MobEffectRegistry.SUMMON_BLAZE_TIMER)) {
            //   effectAmplifier += entity.getEffect(MobEffectRegistry.SUMMON_BLAZE_TIMER).getAmplifier() + 1;
            //}

            //entity.addEffect(new MobEffectInstance(MobEffectRegistry.SUMMON_BLAZE_TIMER, summonTime, effectAmplifier, false, false, true));
        }
            super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }
    public ICastDataSerializable getEmptyCastData() {
        return new SummonedEntitiesCastData();
    }
    public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
        if (SummonManager.recastFinishedHelper(serverPlayer, recastInstance, recastResult, castDataSerializable)) {
            super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
        }

    }
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 2;
    }
}
