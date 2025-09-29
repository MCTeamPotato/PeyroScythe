package com.rinko1231.peyroscythe.effect;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SinfireEmbraceEffect extends MagicMobEffect {
    private static final UUID ARMOR_DEBUFF_UUID = UUID.fromString("e1f8e44e-15e8-4cf7-9fd8-2d3f18b5c999");
    private static final UUID FIRE_POWER_UUID = UUID.fromString("8a7e1f32-3f2a-4f21-b58b-4f7e6e8d23f9");

    public SinfireEmbraceEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF4500); // 橙红色
    }

    @Override
    public void onEffectAdded(@NotNull LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity,amplifier);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        Level level = entity.level();
        if (level.isClientSide) return true;
            // 如果火熄灭了，就移除效果
        if (!entity.isOnFire()) {
            return false;
        }
        // 持续回血
        entity.heal(1.0F + entity.getMaxHealth() * (PeyroScytheConfig.sinfireEmbraceHealBasicRatio.get().floatValue() + PeyroScytheConfig.sinfireEmbraceHealRatioGrowthPerLevel.get().floatValue() * amplifier)); // 可调
    return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return  duration%20==0; // 每 tick 都执行
    }

    @Override
    public void onEffectRemoved(@NotNull LivingEntity entity,  int amplifier) {
        super.onEffectRemoved(entity, amplifier);
    }

    @Override
    public void addAttributeModifiers(@NotNull AttributeMap attributeMap, int amplifier)  {
        // 先调用父类逻辑，避免跳过默认处理
        super.addAttributeModifiers(attributeMap, amplifier);

        AttributeInstance amr = attributeMap.getInstance(Attributes.ARMOR);
        // 护甲降低
        if (amr != null) {
            if (amr.getModifier(PeyroScythe.id("sinfire_embrace_armor_debuff")) == null) {
            amr.addTransientModifier(
                    new AttributeModifier(
                            PeyroScythe.id("sinfire_embrace_armor_debuff"),
                            PeyroScytheConfig.sinfireEmbraceEffectArmorLossRatio.get(), // -60% 护甲
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));}
        }
        double fireBonus  = PeyroScytheConfig.sinfireEmbraceEffectFirePowerBoostBasicRatio.get() + PeyroScytheConfig.sinfireEmbraceEffectFirePowerBoostRatioGrowthPerLevel.get() * amplifier;
        AttributeInstance firePower = attributeMap.getInstance(AttributeRegistry.FIRE_SPELL_POWER);
        if (firePower!= null) {
            if (firePower.getModifier(PeyroScythe.id("sinfire_embrace_fire_power_bonus")) == null) {
            firePower.addTransientModifier(
                            new AttributeModifier(
                                    PeyroScythe.id("sinfire_embrace_fire_power_bonus"),
                                    fireBonus,
                                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE));}
        }
    }

    @Override
    public void removeAttributeModifiers(@NotNull AttributeMap attributeMap) {
        super.removeAttributeModifiers(attributeMap);

        // 移除我们手动加的 modifier
        AttributeInstance amr = attributeMap.getInstance(Attributes.ARMOR);
        if (amr != null) {
            amr.removeModifier(PeyroScythe.id("sinfire_embrace_armor_debuff"));
        }

        AttributeInstance firePower = attributeMap.getInstance(AttributeRegistry.FIRE_SPELL_POWER);

        if (firePower != null) {
            firePower.removeModifier(PeyroScythe.id("sinfire_embrace_fire_power_bonus"));
        }

    }


}
