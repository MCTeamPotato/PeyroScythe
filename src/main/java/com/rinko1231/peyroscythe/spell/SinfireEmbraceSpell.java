package com.rinko1231.peyroscythe.spell;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

@AutoSpellConfig
public class SinfireEmbraceSpell extends AbstractSpell {
    private final ResourceLocation spellId = PeyroScythe.id( "sinfire_embrace");
    private final DefaultConfig defaultConfig;

    public SinfireEmbraceSpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.RARE)
                .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
                .setMaxLevel(3)
                .setCooldownSeconds(60.0)
                .build();

        this.manaCostPerLevel = 20;
        this.baseSpellPower = 30;  // 这里可以不用于伤害，而是用于持续时间推算
        this.spellPowerPerLevel = 10;
        this.castTime = 0;
        this.baseManaCost = 50;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return this.spellId;
    }

    @Override
    public boolean allowLooting() {
        return PeyroScytheConfig.sinfireEmbraceAllowLooting.get();
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        int durationSec = getEffectDuration(spellLevel, caster);
        return List.of(
                // 效果持续时间
                Component.translatable("ui.irons_spellbooks.effect_length",
                        Utils.timeFromTicks(durationSec * 20, 1)),
                // 生命回复百分比
                Component.translatable("attribute.modifier.plus.1",
                        Utils.stringTruncation(getRegenPerSecond(spellLevel) * 100, 0),
                        Component.translatable("ui.peyroscythe.health_regen"))
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster,
                       CastSource castSource, MagicData playerMagicData) {

        // 必须已经着火才施加业火焚身
        if (!caster.isOnFire()) {
            // 如果你希望施法时自动点燃，可以在这里加
            caster.setSecondsOnFire(getEffectDuration(spellLevel, caster));
        }

        caster.addEffect(new MobEffectInstance(
                MobEffectRegistry.SINFIRE_EMBRACE.get(),
                getEffectDuration(spellLevel, caster) * 20,
                spellLevel - 1,
                false, false, true
        ));

        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }

    /** 效果持续秒数 */
    private int getEffectDuration(int spellLevel, LivingEntity entity) {
        return PeyroScytheConfig.sinfireEmbraceDurationBasicSeconds.get() + (spellLevel * PeyroScytheConfig.sinfireEmbraceDurationGrowthPerLevel.get()); // 1级12秒，3级20秒
    }

    /** 每秒回血百分比（最大生命值百分比） */
    private float getRegenPerSecond(int spellLevel) {
        return PeyroScytheConfig.sinfireEmbraceHealBasicRatio.get().floatValue() + (spellLevel - 1) * PeyroScytheConfig.sinfireEmbraceHealRatioGrowthPerLevel.get().floatValue(); // 1级5%，每级+2%
    }
}