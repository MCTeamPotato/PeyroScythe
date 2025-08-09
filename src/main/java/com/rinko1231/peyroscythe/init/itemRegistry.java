package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.item.ExtendedSwordItem;
import com.rinko1231.peyroscythe.item.ExtendedWeaponTier;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import com.rinko1231.peyroscythe.item.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
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

    public static final RegistryObject<Item> HELLRAZOR = ITEMS.register("hellrazor",
            () -> new MagicSwordItem(ExtendedWeaponTier.HELLRAZOR,
                    12.0F, -2.6F,
                    new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(NewSpellRegistry.GLACIER_SPELL, 3)},
                    Map.of( // 没有额外属性就传空 Map
                            Attributes.ARMOR, new AttributeModifier("Armor bonus", 0.0F, AttributeModifier.Operation.ADDITION)
                    ),
                    new Item.Properties()
                            .rarity(Rarity.UNCOMMON)
                            .fireResistant()
            ));

    public static final RegistryObject<Item> DECREPIT_SCYTHE = ITEMS.register("decrepit_scythe",
            () -> new ExtendedSwordItem(
                    ExtendedWeaponTier.DECREPIT_SCYTHE,
                    10.0F, -2.6F,
                    ExtendedWeaponTier.DECREPIT_SCYTHE.getAdditionalAttributesMap(),
                    new Item.Properties()
                            .rarity(Rarity.UNCOMMON)
                            .fireResistant()
            ));

}
