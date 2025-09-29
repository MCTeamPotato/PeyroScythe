package com.rinko1231.peyroscythe.spell.eldritch;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.init.TagsRegistry;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.registries.SoundRegistry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Optional;

@AutoSpellConfig
public class IllusionFearSpell extends AbstractSpell {
    private final ResourceLocation spellId = PeyroScythe.id("illusion_fear");
    private final DefaultConfig defaultConfig;

    public IllusionFearSpell() {
        this.defaultConfig = new DefaultConfig()
                .setMinRarity(SpellRarity.EPIC)
                .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
                .setMaxLevel(3)
                .setCooldownSeconds(45)
                .build();
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 8;
        this.spellPowerPerLevel = 2;
        this.castTime = 20;
        this.baseManaCost = 55;
    }

    @Override
    public boolean allowLooting() {
        return PeyroScytheConfig.illusionFearAllowLooting.get();
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
        return Optional.of((SoundEvent) SoundRegistry.BLIGHT_BEGIN.get());
    }

    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of((SoundEvent) SoundEvents.AMBIENT_CAVE.value());
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 24, 0.35F);
    }

    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        ICastData targetEntityData = playerMagicData.getAdditionalCastData();
        if (targetEntityData instanceof TargetEntityCastData targetData) {
            LivingEntity target = targetData.getTarget((ServerLevel) world);
            if (target != null&& !target.getType().is(TagsRegistry.ILLUSION_IMMUNE)) {
                CompoundTag tag = target.getPersistentData();
                // 移除已有的恐惧标记（别人施加的）
                tag.remove("peyroscythe:fear_caster");

                // 设置当前施法者 UUID
                tag.putUUID("peyroscythe:fear_caster", entity.getUUID());

                // 移除疯狂效果
                if(target.hasEffect(MobEffectRegistry.ILLUSION_CRAZY))
                    target.removeEffect(MobEffectRegistry.ILLUSION_CRAZY);

                // 添加恐惧效果
                target.addEffect(new MobEffectInstance(MobEffectRegistry.ILLUSION_FEAR, this.getDuration(spellLevel, entity), this.getAmplifier(spellLevel, entity)));
            }
        }
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    public int getAmplifier(int spellLevel, LivingEntity caster) {
        return 0;
    }

    public int getDuration(int spellLevel, LivingEntity caster) {
        return (int) (this.getSpellPower(spellLevel, caster) * 0.8F * 20.0F);
    }
}
