package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.item.CurioAdvanceItem;
import com.rinko1231.peyroscythe.item.GregDonutItem;
import com.rinko1231.peyroscythe.item.MorphingMagicWeaponItem;
import com.rinko1231.peyroscythe.item.PeyroExtendedWeaponTier;
import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;

import static com.rinko1231.peyroscythe.PeyroScythe.MOD_ID;
import static com.rinko1231.peyroscythe.init.NewSpellRegistry.FROST_HELL_SPELL;
import static net.minecraft.world.item.Rarity.RARE;


public class itemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MOD_ID);

    public static final DeferredHolder<Item, Item> PONTIFICAL_KNIGHT_MEDAL = ITEMS.register("pontifical_knight_medal",
            () -> new CurioBaseItem(ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, context, tooltip, flag);
                    String OriginalId = this.getDescriptionId();
                    String TooltipKey = "tooltip." + OriginalId;
                    tooltip.add(Component.translatable(ChatFormatting.BLUE + "" + I18n.get(TooltipKey)));
                }
            }.withAttributes("charm",
                    new AttributeContainer[]{
                            new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                            new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    })
                    );
    public static final DeferredHolder<Item, Item> WINTERMOON_HEART = ITEMS.register("wintermoon_heart",
            () -> new CurioAdvanceItem(ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, context, tooltip, flag);
                    String OriginalId = this.getDescriptionId();
                    String TooltipKey = "tooltip." + OriginalId;
                    tooltip.add(Component.translatable(ChatFormatting.BLUE + "" + I18n.get(TooltipKey)));
                }
            }.withAttributes(Curios.NECKLACE_SLOT,
                            new AttributeContainer[]{
                                    new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                                    new AttributeContainer(AttributeRegistry.MAX_MANA, 200, AttributeModifier.Operation.ADD_VALUE),
                                    new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                            })
                    .withAttributes("charm",
                    new AttributeContainer[]{
                            new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                            new AttributeContainer(AttributeRegistry.MAX_MANA, 200, AttributeModifier.Operation.ADD_VALUE),
                            new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    }));

    public static final DeferredHolder<Item, Item> ICE_JUDGMENT = ITEMS.register("ice_judgment",
        () -> new MagicSwordItem(PeyroExtendedWeaponTier.ICE_JUDGEMENT,
                ItemPropertiesHelper
                        .equipment()
                        .rarity(Rarity.UNCOMMON)
                        .fireResistant()
                        .attributes(ExtendedSwordItem.createAttributes(PeyroExtendedWeaponTier.ICE_JUDGEMENT)),
                SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{
                        new SpellDataRegistryHolder(FROST_HELL_SPELL, 3)}))
        {

        }

    );


    public static final DeferredHolder<Item, Item> CAPTAIN_GREG = ITEMS.register("captain_greg",
            () -> new MagicSwordItem(PeyroExtendedWeaponTier.CAPTAIN_GREG,
                    ItemPropertiesHelper
                            .equipment()
                            .rarity(Rarity.EPIC)
                            .attributes(ExtendedSwordItem.createAttributes(PeyroExtendedWeaponTier.CAPTAIN_GREG)),
                    SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{
                            new SpellDataRegistryHolder(SpellRegistry.FROST_STEP_SPELL, 3)})));

    private static final Supplier<Item>[] LANCE_REF = new Supplier[]{null};
    private static final Supplier<Item>[] SCYTHE_REF = new Supplier[]{null};

    public static final DeferredHolder<Item, Item> CAPTAIN_GREG_TRANSFORMABLE = ITEMS.register("captain_greg_transformable",
            () -> new MorphingMagicWeaponItem(PeyroExtendedWeaponTier.CAPTAIN_GREG,
                    ItemPropertiesHelper
                            .equipment()
                            .rarity(Rarity.EPIC)
                            .attributes(ExtendedSwordItem.createAttributes(PeyroExtendedWeaponTier.CAPTAIN_GREG)),
                    SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{
                            new SpellDataRegistryHolder(SpellRegistry.FROST_STEP_SPELL, 3)}),
                    () -> LANCE_REF[0].get()));

    public static final DeferredHolder<Item, Item> CAPTAIN_GREG_LANCE = ITEMS.register("captain_greg_lance",
            () -> new MorphingMagicWeaponItem(PeyroExtendedWeaponTier.CAPTAIN_GREG_LANCE,
                    ItemPropertiesHelper
                            .equipment()
                            .rarity(Rarity.EPIC)
                            .attributes(ExtendedSwordItem.createAttributes(PeyroExtendedWeaponTier.CAPTAIN_GREG_LANCE)),
                    SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{
                            new SpellDataRegistryHolder(SpellRegistry.FROST_STEP_SPELL, 3)}),
                    () -> SCYTHE_REF[0].get()));

    // 静态块里完成互绑
    static {
        LANCE_REF[0] = CAPTAIN_GREG_LANCE;
        SCYTHE_REF[0] = CAPTAIN_GREG_TRANSFORMABLE;
    }
    public static final DeferredHolder<Item, Item> CAPTAIN_GREG_DONUT = ITEMS.register("captain_greg_donut", () -> new GregDonutItem(new GregDonutItem.Properties().food(GregDonutItem.CAPTAIN_GREG_DONUT).rarity(RARE)));

    //public static final RegistryObject<Item> TEST_ICE_CREAM = ITEMS.register("test_ice_cream", () -> new IceCreamItem(new Item.Properties().food(GregDonutItem.CAPTAIN_GREG_DONUT).rarity(RARE)));


    private static final Supplier<Item>[] LANCE_REF_PA = new Supplier[]{null};
    private static final Supplier<Item>[] SCYTHE_REF_PA = new Supplier[]{null};


    public static final DeferredHolder<Item, Item> CAPTAIN_GREG_PA = ITEMS.register("captain_greg_pa",
            () -> new MorphingMagicWeaponItem(PeyroExtendedWeaponTier.CAPTAIN_GREG,
                    ItemPropertiesHelper
                            .equipment()
                            .rarity(Rarity.EPIC)
                            .attributes(ExtendedSwordItem.createAttributes(PeyroExtendedWeaponTier.CAPTAIN_GREG)),
                    SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{
                            new SpellDataRegistryHolder(SpellRegistry.FROST_STEP_SPELL, 3)}),
                    () -> LANCE_REF_PA[0].get()));

    public static final DeferredHolder<Item, Item> CAPTAIN_GREG_LANCE_PA = ITEMS.register("captain_greg_lance_pa",
            () -> new MorphingMagicWeaponItem(PeyroExtendedWeaponTier.CAPTAIN_GREG_LANCE,
                    ItemPropertiesHelper
                            .equipment()
                            .rarity(Rarity.EPIC)
                            .attributes(ExtendedSwordItem.createAttributes(PeyroExtendedWeaponTier.CAPTAIN_GREG_LANCE)),
                    SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{
                            new SpellDataRegistryHolder(SpellRegistry.FROST_STEP_SPELL, 3)}),
                    () -> SCYTHE_REF_PA[0].get()));

    // 静态块里完成互绑
    static {
        LANCE_REF_PA[0] = CAPTAIN_GREG_LANCE_PA;
        SCYTHE_REF_PA[0] = CAPTAIN_GREG_PA;
    }

    public static final DeferredHolder<Item, Item> CLAIR_DE_LUNE_DISC = ITEMS.register("clair_de_lune_disc",
            () -> new Item(ItemPropertiesHelper.material(1)
                    .rarity(Rarity.RARE)
                    .jukeboxPlayable(
                            ResourceKey.create(
                                    Registries.JUKEBOX_SONG,
                                    PeyroScythe.id("clair_de_lune"))))
            {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, context, tooltip, flag);
                    tooltip.add(Component.translatable("item.peyroscythe.clair_de_lune_disc.tooltip")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                }
            }
            );

    //public static final DeferredHolder<Item, Item> CLAIR_DE_LUNE_DISC = ITEMS.register("clair_de_lune_disc", new Item((new Item.Properties()).stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(PeyroJukeboxSongs.CLAIR_DE_LUNE.)));

/*
    public static final DeferredHolder<Item, Item> CLAIR_DE_LUNE_DISC = ITEMS.register("clair_de_lune_disc",
            () -> new RecordItem(
                    7,
                    SoundRegistry.RECORD_CLAIR_DE_LUNE,
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE),
                    6080)
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, level, tooltip, flag);
                    tooltip.add(Component.translatable("item.peyroscythe.clair_de_lune_disc.tooltip")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                }
            });



*/



}
