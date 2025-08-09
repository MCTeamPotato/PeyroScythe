package com.rinko1231.peyroscythe.item;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;


import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class AttributeContainer {
    private final Attribute attribute;
    private final double value;
    private final AttributeModifier.Operation operation;

    public AttributeContainer(Attribute attribute, double value, AttributeModifier.Operation operation) {
        this.attribute = attribute;
        this.value = value;
        this.operation = operation;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public double getValue() {
        return value;
    }

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

    // Forge 1.20.1 版本的 AttributeModifier 创建
    public AttributeModifier toForgeModifier(String slotName) {
        String name = slotName + "_" + ForgeRegistries.ATTRIBUTES.getKey(attribute).getPath();
        UUID id = UUID.nameUUIDFromBytes(name.getBytes());
        return new AttributeModifier(id, name, value, operation);
    }
}
