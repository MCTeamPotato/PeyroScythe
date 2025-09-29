package com.rinko1231.peyroscythe.item;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class PeyroExtendedWeaponTier implements Tier, IronsWeaponTier {
    public static PeyroExtendedWeaponTier CAPTAIN_GREG;
    public static PeyroExtendedWeaponTier CAPTAIN_GREG_LANCE;
    public static PeyroExtendedWeaponTier ICE_JUDGEMENT;

    int uses;
    float damage;
    float speed;
    int enchantmentValue;
    TagKey<Block> incorrectBlocksForDrops;
    Supplier<Ingredient> repairIngredient;
    AttributeContainer[] attributes;

    public PeyroExtendedWeaponTier(int uses, float damage, float speed, int enchantmentValue, TagKey<Block> incorrectBlocksForDrops, Supplier<Ingredient> repairIngredient, AttributeContainer... attributes) {
        this.uses = uses;
        this.damage = damage;
        this.speed = speed;
        this.enchantmentValue = enchantmentValue;
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        this.repairIngredient = repairIngredient;
        this.attributes = attributes;
    }

    public int getUses() {
        return this.uses;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getAttackDamageBonus() {
        return this.damage;
    }

    public TagKey<Block> getIncorrectBlocksForDrops() {
        return this.incorrectBlocksForDrops;
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public Ingredient getRepairIngredient() {
        return (Ingredient)this.repairIngredient.get();
    }

    public AttributeContainer[] getAdditionalAttributes() {
        return this.attributes;
    }

    static {
        CAPTAIN_GREG = new PeyroExtendedWeaponTier(2031, 7.0F, -2.0F, 16, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, () -> Ingredient.of(new ItemLike[]{Items.NETHERITE_SCRAP}), new AttributeContainer[]{new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)});
        CAPTAIN_GREG_LANCE = new PeyroExtendedWeaponTier(2031, 5.0F, -1.33F, 16, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, () -> Ingredient.of(new ItemLike[]{Items.NETHERITE_SCRAP}), new AttributeContainer[]{new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)});
        ICE_JUDGEMENT = new PeyroExtendedWeaponTier(1000, 9.0F, -2.0F, 4, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, () -> Ingredient.of(new ItemLike[]{ItemRegistry.ICE_RUNE.get()}), new AttributeContainer[]{new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)});
 }
}
