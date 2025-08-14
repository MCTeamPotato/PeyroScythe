package com.rinko1231.peyroscythe.item;



import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class NewExtendedWeaponTier implements Tier {
    public static NewExtendedWeaponTier ICE_JUDGEMENT;
    public static NewExtendedWeaponTier CAPTAIN_GREG;

    private final int uses;
    private final float attackDamageBonus;
    private final float attackSpeed;
    private final int enchantmentValue;
    private final TagKey<Block> incorrectBlocksForDrops;
    private final Supplier<Ingredient> repairIngredient;
    private final AttributeContainer[] attributes;

    public NewExtendedWeaponTier(int uses, float attackDamageBonus, float attackSpeed, int enchantmentValue,
                                 TagKey<Block> incorrectBlocksForDrops,
                                 Supplier<Ingredient> repairIngredient,
                                 AttributeContainer... attributes) {
        this.uses = uses;
        this.attackDamageBonus = attackDamageBonus;
        this.attackSpeed = attackSpeed;
        this.enchantmentValue = enchantmentValue;
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        this.repairIngredient = repairIngredient;
        this.attributes = attributes;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return attackSpeed;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamageBonus;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    public TagKey<Block> getIncorrectBlocksForDrops() {
        return incorrectBlocksForDrops;
    }

    public AttributeContainer[] getAdditionalAttributes() {
        return attributes;
    }

    /**
     * 适配到 Forge 1.20.1 可用的 Map<Attribute, AttributeModifier>
     */
    public Map<Attribute, AttributeModifier> getAdditionalAttributesMap() {
        Map<Attribute, AttributeModifier> map = new HashMap<>();
        for (AttributeContainer container : attributes) {
            String name = "tier_bonus_" + container.attribute().getDescriptionId();
            UUID id = UUID.nameUUIDFromBytes(name.getBytes());
            map.put(container.attribute(),
                    new AttributeModifier(id, name, container.value(), container.operation()));
        }
        return map;
    }

    // 初始化静态实例
    static {
        ICE_JUDGEMENT = new NewExtendedWeaponTier(
                2031, 8.0F, -1.0F, 25,
                BlockTags.NEEDS_DIAMOND_TOOL,
                () -> Ingredient.of(Items.NETHERITE_SCRAP, Items.BLUE_ICE)
                // 如果有额外属性可加 AttributeContainer
        );

        CAPTAIN_GREG = new NewExtendedWeaponTier(
                1000, 7.0F, -1.0F, 4,
                BlockTags.NEEDS_DIAMOND_TOOL,
                () -> Ingredient.of(Items.NETHERITE_SCRAP)
        );
    }
}
