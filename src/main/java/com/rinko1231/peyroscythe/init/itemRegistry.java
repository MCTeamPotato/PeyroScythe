package com.rinko1231.peyroscythe.init;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.rinko1231.peyroscythe.item.MorphingMagicWeaponItem;
import com.rinko1231.peyroscythe.item.NewExtendedWeaponTier;
import com.rinko1231.peyroscythe.item.NewMagicSwordItem;
import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import io.redspace.ironsspellbooks.item.curios.SimpleAttributeCurio;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static com.rinko1231.peyroscythe.PeyroScythe.MOD_ID;


public class itemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> PONTIFICAL_KNIGHT_MEDAL = ITEMS.register("pontifical_knight_medal",
            () -> new CurioBaseItem(ItemPropertiesHelper.equipment().stacksTo(1)) {
                @Override
                public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
                    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

                    // +20% 冰霜法术强度
                    builder.put(AttributeRegistry.ICE_SPELL_POWER.get(),
                            new AttributeModifier(uuid, "medal_ice_power_bonus", 0.20F, AttributeModifier.Operation.MULTIPLY_BASE));
                    builder.put(AttributeRegistry.MAX_MANA.get(),
                            new AttributeModifier(uuid, "medal_max_mana_bonus", 100, AttributeModifier.Operation.ADDITION));
                    // +1% 神圣法术强度
                    builder.put(AttributeRegistry.ELDRITCH_SPELL_POWER.get(),
                            new AttributeModifier(uuid, "medal_eldritch_power_bonus", 0.01F, AttributeModifier.Operation.MULTIPLY_BASE));
                    // +10% 魔力回复
                    builder.put(AttributeRegistry.MANA_REGEN.get(),
                            new AttributeModifier(uuid, "medal_mana_regen_bonus", 0.10F, AttributeModifier.Operation.MULTIPLY_BASE));

                    return builder.build();
                }
                @Override
                public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
                    return true;
                }
            });



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
            () -> new MagicSwordItem(
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
    private static final Supplier<Item>[] LANCE_REF = new Supplier[]{null};
    private static final Supplier<Item>[] SCYTHE_REF = new Supplier[]{null};

    public static final RegistryObject<Item> CAPTAIN_GREG_TRANSFORMABLE = ITEMS.register("captain_greg_transformable",
            () -> new MorphingMagicWeaponItem(
                    NewExtendedWeaponTier.CAPTAIN_GREG,
                    8, -2.0,
                    new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(SpellRegistry.FROST_STEP_SPELL, 3)},
                    Map.of(),
                    new Item.Properties().stacksTo(1),
                    () -> LANCE_REF[0].get() // 延迟引用

            )
    );

    public static final RegistryObject<Item> CAPTAIN_GREG_LANCE = ITEMS.register("captain_greg_lance",
            () -> new MorphingMagicWeaponItem(
                    NewExtendedWeaponTier.CAPTAIN_GREG,
                    6.0, -1.33,
                    new SpellDataRegistryHolder[]{ /* spell holders */ },
                    Map.of(),
                    new Item.Properties().stacksTo(1),
                    () -> SCYTHE_REF[0].get() // 延迟引用
            )
    );

    // 静态块里完成互绑
    static {
        LANCE_REF[0] = CAPTAIN_GREG_LANCE;
        SCYTHE_REF[0] = CAPTAIN_GREG_TRANSFORMABLE;
    }

}
