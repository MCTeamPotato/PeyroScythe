package com.rinko1231.peyroscythe.effect;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class HolyBellSuppressionEffect extends MagicMobEffect {
    public HolyBellSuppressionEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFD700);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Vec3 motion = entity.getDeltaMovement();

        double x = motion.x * PeyroScytheConfig.holyBellSuppressionMovementSpeedRatio.get();
        double z = motion.z * PeyroScytheConfig.holyBellSuppressionMovementSpeedRatio.get();

        double y = motion.y;
        if (y > 0) {
            y = y * PeyroScytheConfig.holyBellSuppressionMovementSpeedRatio.get();
        }


        entity.setDeltaMovement(x, y, z);
        entity.hasImpulse = true;
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // 每tick调用
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {

        if (entity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            entity.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(new AttributeModifier(
                    UUID.fromString("3b5c6c1d-4f3e-41f9-9c52-8d4d5f7c1e2a"),
                    "holy_bell_atk_down",
                    - PeyroScytheConfig.holyBellSuppressionATKLowerRatio.get(),
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        }


        if (entity.getAttribute(Attributes.ATTACK_SPEED) != null) {
            entity.getAttribute(Attributes.ATTACK_SPEED).addTransientModifier(new AttributeModifier(
                    UUID.fromString("8e9d13f7-2f44-4e6d-87e1-9a0f58d62b7c"),
                    "holy_bell_atk_speed_down",
                    - PeyroScytheConfig.holyBellSuppressionSPDLowerRatio.get(),
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        }

        if (entity.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(new AttributeModifier(
                    UUID.fromString("a7f9f2b0-6e3b-4dbf-bc91-6a8f38c2f9de"),
                    "holy_bell_move_slow",
                    -1 + PeyroScytheConfig.holyBellSuppressionMovementSpeedRatio.get(),
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        }

        super.addAttributeModifiers(entity, attributeMap, amplifier);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        if (entity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            entity.getAttribute(Attributes.ATTACK_DAMAGE)
                    .removeModifier(UUID.fromString("3b5c6c1d-4f3e-41f9-9c52-8d4d5f7c1e2a"));
        }

        if (entity.getAttribute(Attributes.ATTACK_SPEED) != null) {
            entity.getAttribute(Attributes.ATTACK_SPEED)
                    .removeModifier(UUID.fromString("8e9d13f7-2f44-4e6d-87e1-9a0f58d62b7c"));
        }

        if (entity.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED)
                    .removeModifier(UUID.fromString("a7f9f2b0-6e3b-4dbf-bc91-6a8f38c2f9de"));
        }

        super.removeAttributeModifiers(entity, attributeMap, amplifier);
    }
}
