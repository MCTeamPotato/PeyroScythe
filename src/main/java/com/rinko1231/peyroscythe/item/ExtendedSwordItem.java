package com.rinko1231.peyroscythe.item;


import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

import java.util.Map;


import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class ExtendedSwordItem extends SwordItem {
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public ExtendedSwordItem(Tier tier, double attackDamage, double attackSpeed,
                             Map<Attribute, AttributeModifier> additionalAttributes,
                             Properties properties) {
        super(tier, 3, -2.4F, properties); // Forge 构造要填 nominal 3/-2.4，但实际会用 modifiers 覆盖

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

        // 基础伤害 / 攻速
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION));

        // 附加属性
        for (Map.Entry<Attribute, AttributeModifier> entry : additionalAttributes.entrySet()) {
            builder.put(entry.getKey(), entry.getValue());
        }

        this.defaultModifiers = builder.build();
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }
}
