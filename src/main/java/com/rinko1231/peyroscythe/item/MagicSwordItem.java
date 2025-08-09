package com.rinko1231.peyroscythe.item;



import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MagicSwordItem extends ExtendedSwordItem implements IPresetSpellContainer {
    private List<SpellData> spellData = null;
    private SpellDataRegistryHolder[] spellDataRegistryHolders;

    public MagicSwordItem(Tier tier, double attackDamage, double attackSpeed,
                          SpellDataRegistryHolder[] spellDataRegistryHolders,
                          Map<Attribute, AttributeModifier> additionalAttributes,
                          Properties properties) {
        super(tier, attackDamage, attackSpeed, additionalAttributes, properties);
        this.spellDataRegistryHolders = spellDataRegistryHolders;
    }

    public List<SpellData> getSpells() {
        if (this.spellData == null) {
            this.spellData = Arrays.stream(this.spellDataRegistryHolders)
                    .map(SpellDataRegistryHolder::getSpellData)
                    .toList();
            this.spellDataRegistryHolders = null;
        }
        return this.spellData;
    }

    public void initializeSpellContainer(ItemStack stack) {
        if (stack != null && !ISpellContainer.isSpellContainer(stack)) {
            List<SpellData> spells = this.getSpells();
            ISpellContainer spellContainer = ISpellContainer.create(spells.size(), true, false);
            spells.forEach(s -> spellContainer.addSpell(s.getSpell(), s.getLevel(), true, null));
            spellContainer.save(stack);
        }
    }
}
