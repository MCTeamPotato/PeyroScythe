package com.rinko1231.peyroscythe.effect;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class SinfireEmbraceEffect extends MagicMobEffect {
    private static final UUID ARMOR_DEBUFF_UUID = UUID.fromString("e1f8e44e-15e8-4cf7-9fd8-2d3f18b5c999");
    private static final UUID FIRE_POWER_UUID = UUID.fromString("8a7e1f32-3f2a-4f21-b58b-4f7e6e8d23f9");

    public SinfireEmbraceEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF4500); // 橙红色
    }

    @Override
    public void addAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        // 护甲降低
        if (entity.getAttribute(Attributes.ARMOR) != null) {
            Objects.requireNonNull(entity.getAttribute(Attributes.ARMOR)).addTransientModifier(
                    new AttributeModifier(ARMOR_DEBUFF_UUID,
                            "sinfire_embrace_armor_debuff",
                            PeyroScytheConfig.sinfireEmbraceEffectArmorLossRatio.get(), // -60% 护甲
                            AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        double fireBonus  = PeyroScytheConfig.sinfireEmbraceEffectFirePowerBoostBasicRatio.get() + PeyroScytheConfig.sinfireEmbraceEffectFirePowerBoostRatioGrowthPerLevel.get() * amplifier;
        if (entity.getAttribute(AttributeRegistry.FIRE_SPELL_POWER.get()) != null) {
            Objects.requireNonNull(entity.getAttribute(AttributeRegistry.FIRE_SPELL_POWER.get())).addTransientModifier(
                    new AttributeModifier(FIRE_POWER_UUID, "sinfire_embrace_fire_power_bonus",
                            fireBonus, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        Level level = entity.level();
        if (level.isClientSide) return;
            // 如果火熄灭了，就移除效果
        if (!entity.isOnFire()) {
            entity.removeEffect(this);
            return;
        }
        // 持续回血
        entity.heal(1.0F + entity.getMaxHealth() * (PeyroScytheConfig.sinfireEmbraceHealBasicRatio.get().floatValue() + PeyroScytheConfig.sinfireEmbraceHealRatioGrowthPerLevel.get().floatValue() * amplifier)); // 可调
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // 每 tick 都执行
    }

    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap attributes, int amplifier) {
        super.removeAttributeModifiers(entity, attributes, amplifier);
        // 移除护甲减益
        if (entity.getAttribute(Attributes.ARMOR) != null) {
            Objects.requireNonNull(entity.getAttribute(Attributes.ARMOR)).removeModifier(ARMOR_DEBUFF_UUID);
        }
        if (entity.getAttribute(AttributeRegistry.FIRE_SPELL_POWER.get()) != null) {
            Objects.requireNonNull(entity.getAttribute(AttributeRegistry.FIRE_SPELL_POWER.get())).removeModifier(FIRE_POWER_UUID);
        }

    }


}
