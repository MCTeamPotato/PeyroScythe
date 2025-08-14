package com.rinko1231.peyroscythe.item;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record AttributeContainer(Attribute attribute, double value, AttributeModifier.Operation operation) {

}
