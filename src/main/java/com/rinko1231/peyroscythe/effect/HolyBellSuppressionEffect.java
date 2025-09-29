package com.rinko1231.peyroscythe.effect;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class HolyBellSuppressionEffect extends MagicMobEffect {
    public HolyBellSuppressionEffect() {

        super(MobEffectCategory.HARMFUL, 0xFFD700);
/*
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE,
                PeyroScythe.id("holy_bell_atk_down"),
                    - PeyroScytheConfig.holyBellSuppressionATKLowerRatio.get(),
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        this.addAttributeModifier(Attributes.ATTACK_SPEED,
                PeyroScythe.id("holy_bell_atk_speed_down"),
                    - PeyroScytheConfig.holyBellSuppressionSPDLowerRatio.get(),
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                PeyroScythe.id("holy_bell_move_slow"),
                    -1 + PeyroScytheConfig.holyBellSuppressionMovementSpeedRatio.get(),
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
*/
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        Vec3 motion = entity.getDeltaMovement();

        double x = motion.x * PeyroScytheConfig.holyBellSuppressionMovementSpeedRatio.get();
        double z = motion.z * PeyroScytheConfig.holyBellSuppressionMovementSpeedRatio.get();

        double y = motion.y;
        if (y > 0) {
            y = y * PeyroScytheConfig.holyBellSuppressionMovementSpeedRatio.get();
        }


        entity.setDeltaMovement(x, y, z);
        entity.hasImpulse = true;
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true; // 每tick调用
    }
    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int amplifier)  {
        // 先调用父类逻辑，避免跳过默认处理
        super.addAttributeModifiers(attributeMap, amplifier);

        // 攻击力
        AttributeInstance atk = attributeMap.getInstance(Attributes.ATTACK_DAMAGE);
        if (atk != null) {
            if (atk.getModifier(PeyroScythe.id("holy_bell_atk_down")) == null) {
            atk.addTransientModifier(new AttributeModifier(
                    PeyroScythe.id("holy_bell_atk_down"),
                    -PeyroScytheConfig.holyBellSuppressionATKLowerRatio.get(),
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));}
        }

        // 攻击速度
        AttributeInstance spd = attributeMap.getInstance(Attributes.ATTACK_SPEED);
        if (spd != null) {
            if (spd.getModifier(PeyroScythe.id("holy_bell_atk_speed_down")) == null) {
            spd.addTransientModifier(new AttributeModifier(
                    PeyroScythe.id("holy_bell_atk_speed_down"),
                    -PeyroScytheConfig.holyBellSuppressionSPDLowerRatio.get(),
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));}
        }

        // 移动速度
        AttributeInstance move = attributeMap.getInstance(Attributes.MOVEMENT_SPEED);
        if (move != null) {
            if (move.getModifier(PeyroScythe.id("holy_bell_move_slow")) == null) {
            move.addTransientModifier(new AttributeModifier(
                    PeyroScythe.id("holy_bell_move_slow"),
                    -1 + PeyroScytheConfig.holyBellSuppressionMovementSpeedRatio.get(),
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));}
        }
    }

    @Override
    public void removeAttributeModifiers(AttributeMap attributeMap) {
        super.removeAttributeModifiers(attributeMap);

        // 移除我们手动加的 modifier
        AttributeInstance atk = attributeMap.getInstance(Attributes.ATTACK_DAMAGE);
        if (atk != null) {
            atk.removeModifier(PeyroScythe.id("holy_bell_atk_down"));
        }

        AttributeInstance spd = attributeMap.getInstance(Attributes.ATTACK_SPEED);
        if (spd != null) {
            spd.removeModifier(PeyroScythe.id("holy_bell_atk_speed_down"));
        }

        AttributeInstance move = attributeMap.getInstance(Attributes.MOVEMENT_SPEED);
        if (move != null) {
            move.removeModifier(PeyroScythe.id("holy_bell_move_slow"));
        }
    }
    /*
    public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            pLivingEntity.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(new AttributeModifier(
                    PeyroScythe.id("holy_bell_atk_down"),
                    - PeyroScytheConfig.holyBellSuppressionATKLowerRatio.get(),
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        }

        if (pLivingEntity.getAttribute(Attributes.ATTACK_SPEED) != null) {
            pLivingEntity.getAttribute(Attributes.ATTACK_SPEED).addTransientModifier(new AttributeModifier(
                    PeyroScythe.id("holy_bell_atk_speed_down"),
                    - PeyroScytheConfig.holyBellSuppressionSPDLowerRatio.get(),
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        }

        if (pLivingEntity.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            pLivingEntity.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(new AttributeModifier(
                    PeyroScythe.id("holy_bell_move_slow"),
                    -1 + PeyroScytheConfig.holyBellSuppressionMovementSpeedRatio.get(),
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        }

        super.onEffectAdded(pLivingEntity, pAmplifier);
    }
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            pLivingEntity.getAttribute(Attributes.ATTACK_DAMAGE)
                    .removeModifier(PeyroScythe.id("holy_bell_atk_down"));
        }

        if (pLivingEntity.getAttribute(Attributes.ATTACK_SPEED) != null) {
            pLivingEntity.getAttribute(Attributes.ATTACK_SPEED)
                    .removeModifier(PeyroScythe.id("holy_bell_atk_speed_down"));
        }

        if (pLivingEntity.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            pLivingEntity.getAttribute(Attributes.MOVEMENT_SPEED)
                    .removeModifier(PeyroScythe.id("holy_bell_move_slow"));
        }

    }*/

}
