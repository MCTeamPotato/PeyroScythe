package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.item.NewExtendedSwordItem;
import com.rinko1231.peyroscythe.item.NewExtendedWeaponTier;
import com.rinko1231.peyroscythe.item.NewMagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

import static com.rinko1231.peyroscythe.PeyroScythe.MOD_ID;


public class itemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> ICE_JUDGMENT = ITEMS.register("ice_judgment",
            () -> new NewMagicSwordItem(NewExtendedWeaponTier.ICE_JUDGEMENT,
                    10.0F, -2.0F,
                    new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(NewSpellRegistry.FROST_HELL_SPELL, 3)},
                    Map.of( // 没有额外属性就传空 Map
                            AttributeRegistry.ICE_SPELL_POWER.get(), new AttributeModifier("ice_power_bonus",0.15F,AttributeModifier.Operation.MULTIPLY_BASE)
                    ),
                    new Item.Properties()
                            .rarity(Rarity.EPIC)
                            .fireResistant()
            ));

    public static final RegistryObject<Item> CAPTAIN_GREG = ITEMS.register("captain_greg",
            () -> new NewMagicSwordItem(
                    NewExtendedWeaponTier.CAPTAIN_GREG,
                    8.0F, -2.0F,
                    new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(SpellRegistry.FROST_STEP_SPELL, 3)},
                    Map.of( // 没有额外属性就传空 Map
                            AttributeRegistry.ICE_SPELL_POWER.get(), new AttributeModifier("ice_power_plus",0.10F,AttributeModifier.Operation.MULTIPLY_BASE)
                    ),
                    new Item.Properties()
                            .rarity(Rarity.UNCOMMON)
                            .fireResistant()
            ));

}
