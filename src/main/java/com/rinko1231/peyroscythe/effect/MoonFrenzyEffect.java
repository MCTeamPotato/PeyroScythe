package com.rinko1231.peyroscythe.effect;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class MoonFrenzyEffect extends MagicMobEffect {

    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("b5d8c0a4-914a-4d28-a3d7-6b122d5e92ab");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("3f8a1c29-6a4d-4f67-b8a1-5d4f9a3c0e7d");
    private static final UUID BLOOD_POWER_UUID  = UUID.fromString("ea217b64-29f3-4c75-bae0-1d6a84f7cbf4");

    public MoonFrenzyEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF3366); // 颜色可改
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
       // entity.hurt(entity.damageSources()., 1.0F + 0.3F* amplifier);
    }
    @Override
    public void addAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);

        double damageBonus = PeyroScytheConfig.MoonFrenzyAttackDamageBoostBasicRatio.get() + PeyroScytheConfig.MoonFrenzyAttackDamageBoostRatioGrowthPerLevel.get() * amplifier;
        double speedBonus  = PeyroScytheConfig.MoonFrenzyAttackSpeedBoostBasicRatio.get() + PeyroScytheConfig.MoonFrenzyAttackSpeedBoostRatioGrowthPerLevel.get() * amplifier;
        double bloodBonus  = PeyroScytheConfig.MoonFrenzyBloodPowerBoostBasicRatio.get() + PeyroScytheConfig.MoonFrenzyBloodPowerBoostRatioGrowthPerLevel.get() * amplifier;

        if (entity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            entity.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(
                    new AttributeModifier(ATTACK_DAMAGE_UUID, "moon_frenzy_attack_damage",
                            damageBonus, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        if (entity.getAttribute(Attributes.ATTACK_SPEED) != null) {
            entity.getAttribute(Attributes.ATTACK_SPEED).addTransientModifier(
                    new AttributeModifier(ATTACK_SPEED_UUID, "moon_frenzy_attack_speed",
                            speedBonus, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        if (entity.getAttribute(AttributeRegistry.BLOOD_SPELL_POWER.get()) != null) {
            entity.getAttribute(AttributeRegistry.BLOOD_SPELL_POWER.get()).addTransientModifier(
                    new AttributeModifier(BLOOD_POWER_UUID, "moon_frenzy_blood_power_bonus",
                            bloodBonus, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }

    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap attributes, int amplifier) {
        super.removeAttributeModifiers(entity, attributes, amplifier);

        if (entity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            entity.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(ATTACK_DAMAGE_UUID);
        }
        if (entity.getAttribute(Attributes.ATTACK_SPEED) != null) {
            entity.getAttribute(Attributes.ATTACK_SPEED).removeModifier(ATTACK_SPEED_UUID);
        }
        if (entity.getAttribute(AttributeRegistry.BLOOD_SPELL_POWER.get()) != null) {
            entity.getAttribute(AttributeRegistry.BLOOD_SPELL_POWER.get()).removeModifier(BLOOD_POWER_UUID);
        }
        // 清除标记
        if (entity.getPersistentData().contains("moonFrenzyMarks", Tag.TAG_LIST)) {
            entity.getPersistentData().remove("moonFrenzyMarks");
        }
    }

}
