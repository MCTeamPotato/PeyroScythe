package com.rinko1231.peyroscythe.effect;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class MoonFrenzyEffect extends MagicMobEffect {

    //private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("b5d8c0a4-914a-4d28-a3d7-6b122d5e92ab");
    //private static final UUID ATTACK_SPEED_UUID = UUID.fromString("3f8a1c29-6a4d-4f67-b8a1-5d4f9a3c0e7d");
    //private static final UUID BLOOD_POWER_UUID  = UUID.fromString("ea217b64-29f3-4c75-bae0-1d6a84f7cbf4");

    public MoonFrenzyEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF3366);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        return true;
       // entity.hurt(entity.damageSources()., 1.0F + 0.3F* amplifier);
    }


    @Override
    public void onEffectRemoved(@NotNull LivingEntity entity, int amplifier) {
        super.onEffectRemoved(entity, amplifier);
        // 清除标记
        if (entity.getPersistentData().contains("moonFrenzyMarks", Tag.TAG_LIST)) {
            entity.getPersistentData().remove("moonFrenzyMarks");
        }
    }

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int amplifier)  {
        // 先调用父类逻辑，避免跳过默认处理
        super.addAttributeModifiers(attributeMap, amplifier);
        double damageBonus = PeyroScytheConfig.MoonFrenzyAttackDamageBoostBasicRatio.get() + PeyroScytheConfig.MoonFrenzyAttackDamageBoostRatioGrowthPerLevel.get() * amplifier;
        double speedBonus  = PeyroScytheConfig.MoonFrenzyAttackSpeedBoostBasicRatio.get() + PeyroScytheConfig.MoonFrenzyAttackSpeedBoostRatioGrowthPerLevel.get() * amplifier;
        double bloodBonus  = PeyroScytheConfig.MoonFrenzyBloodPowerBoostBasicRatio.get() + PeyroScytheConfig.MoonFrenzyBloodPowerBoostRatioGrowthPerLevel.get() * amplifier;

        AttributeInstance atk = attributeMap.getInstance(Attributes.ATTACK_DAMAGE);
        if (atk!= null ) {
            if (atk.getModifier(PeyroScythe.id("moon_frenzy_attack_damage")) == null) {
            atk.addTransientModifier(
                    new AttributeModifier(
                            PeyroScythe.id("moon_frenzy_attack_damage"),
                            damageBonus,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE));}
        }
        AttributeInstance spd = attributeMap.getInstance(Attributes.ATTACK_SPEED);
        if (spd != null) {
            if (spd.getModifier(PeyroScythe.id("moon_frenzy_attack_speed")) == null) {
            spd.addTransientModifier(
                    new AttributeModifier(
                            PeyroScythe.id("moon_frenzy_attack_speed"),
                            speedBonus,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE));}
        }
        AttributeInstance bloodPower = attributeMap.getInstance(AttributeRegistry.BLOOD_SPELL_POWER);
        if (bloodPower != null) {
            if (bloodPower.getModifier(PeyroScythe.id("moon_frenzy_blood_power_bonus")) == null) {
            bloodPower.addTransientModifier(
                            new AttributeModifier(
                                    PeyroScythe.id("moon_frenzy_blood_power_bonus"),
                                    bloodBonus,
                                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE));}
        }
    }

    @Override
    public void removeAttributeModifiers(AttributeMap attributeMap) {
        super.removeAttributeModifiers(attributeMap);

        // 移除我们手动加的 modifier
        AttributeInstance atk = attributeMap.getInstance(Attributes.ATTACK_DAMAGE);
        if (atk != null) {
            atk.removeModifier(PeyroScythe.id("moon_frenzy_attack_damage"));
        }

        AttributeInstance spd = attributeMap.getInstance(Attributes.ATTACK_SPEED);
        if (spd != null) {
            spd.removeModifier(PeyroScythe.id("moon_frenzy_attack_speed"));
        }

        AttributeInstance bloodPower = attributeMap.getInstance(AttributeRegistry.BLOOD_SPELL_POWER);
        if (bloodPower != null) {
            bloodPower.removeModifier(PeyroScythe.id("moon_frenzy_blood_power_bonus"));
        }
    }

}
