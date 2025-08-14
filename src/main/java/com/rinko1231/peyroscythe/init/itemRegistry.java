package com.rinko1231.peyroscythe.init;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.item.MorphingMagicWeaponItem;
import com.rinko1231.peyroscythe.item.NewExtendedWeaponTier;
import com.rinko1231.peyroscythe.item.NewMagicSwordItem;
import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static com.rinko1231.peyroscythe.PeyroScythe.MOD_ID;
import static net.minecraft.world.item.Rarity.RARE;


public class itemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> PONTIFICAL_KNIGHT_MEDAL = ITEMS.register("pontifical_knight_medal",
            () -> new CurioBaseItem(ItemPropertiesHelper.equipment().stacksTo(1).rarity(Rarity.EPIC)) {
                @Override
                public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
                    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
                    builder.put(AttributeRegistry.ELDRITCH_SPELL_POWER.get(),
                            new AttributeModifier(uuid, "medal_eldritch_power_bonus", 0.20F, AttributeModifier.Operation.MULTIPLY_BASE));
                    builder.put(AttributeRegistry.HOLY_SPELL_POWER.get(),
                            new AttributeModifier(uuid, "medal_holy_power_bonus", 0.01F, AttributeModifier.Operation.MULTIPLY_BASE));
                    return builder.build();
                }
                @OnlyIn(Dist.CLIENT)
                @Override
                public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
                    super.appendHoverText(stack, worldIn, tooltip, flagIn);
                    String OriginalId = this.getDescriptionId();
                    String TooltipKey = "tooltip." + OriginalId;
                    String TooltipKey2 = "tooltip." + OriginalId+"2";
                    tooltip.add(Component.translatable(ChatFormatting.LIGHT_PURPLE + "" + I18n.get(TooltipKey)));
                    tooltip.add(Component.translatable(ChatFormatting.LIGHT_PURPLE + "" + I18n.get(TooltipKey2)));
                }

            });

    public static final RegistryObject<Item> WINTERMOON_HEART = ITEMS.register("wintermoon_heart",
            () -> new CurioBaseItem(ItemPropertiesHelper.equipment().stacksTo(1).rarity(Rarity.EPIC)) {
                @Override
                public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
                    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

                    // +20% 冰霜法术强度
                    builder.put(AttributeRegistry.ICE_SPELL_POWER.get(),
                            new AttributeModifier(uuid, "heart_ice_power_bonus", 0.20F, AttributeModifier.Operation.MULTIPLY_BASE));
                    builder.put(AttributeRegistry.MAX_MANA.get(),
                            new AttributeModifier(uuid, "heart_max_mana_bonus", 200, AttributeModifier.Operation.ADDITION));
                    builder.put(AttributeRegistry.MANA_REGEN.get(),
                            new AttributeModifier(uuid, "heart_mana_regen_bonus", 0.10F, AttributeModifier.Operation.MULTIPLY_BASE));

                    return builder.build();
                }
                @OnlyIn(Dist.CLIENT)
                @Override
                public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
                    super.appendHoverText(stack, worldIn, tooltip, flagIn);
                    String OriginalId = this.getDescriptionId();
                    String TooltipKey = "tooltip." + OriginalId;
                    tooltip.add(Component.translatable(ChatFormatting.BLUE + "" + I18n.get(TooltipKey)));
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
            )
            {
                public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
                    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
                    if(equipmentSlot==EquipmentSlot.MAINHAND) {
                        builder.put(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "ice_judgment_atk", PeyroScytheConfig.iceJudgementATK.get(), AttributeModifier.Operation.ADDITION));
                        builder.put(Attributes.ATTACK_SPEED,
                                new AttributeModifier(BASE_ATTACK_SPEED_UUID, "ice_judgment_spd", PeyroScytheConfig.iceJudgementSPD.get(), AttributeModifier.Operation.ADDITION));
                    }
                    return builder.build();
                }
            }
            );

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
            ){
                public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
                    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
                    if(equipmentSlot==EquipmentSlot.MAINHAND) {
                        builder.put(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "captain_greg_atk", PeyroScytheConfig.captainGregScytheATK.get(), AttributeModifier.Operation.ADDITION));
                        builder.put(Attributes.ATTACK_SPEED,
                                new AttributeModifier(BASE_ATTACK_SPEED_UUID, "captain_greg_spd", PeyroScytheConfig.captainGregScytheSPD.get(), AttributeModifier.Operation.ADDITION));
                    }
                    return builder.build();
                }
            });
    private static final Supplier<Item>[] LANCE_REF = new Supplier[]{null};
    private static final Supplier<Item>[] SCYTHE_REF = new Supplier[]{null};

    public static final RegistryObject<Item> CAPTAIN_GREG_TRANSFORMABLE = ITEMS.register("captain_greg_transformable",
            () -> new MorphingMagicWeaponItem(
                    NewExtendedWeaponTier.CAPTAIN_GREG,
                    8.0F, -2.0F,
                    new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(SpellRegistry.FROST_STEP_SPELL, 3)},
                    Map.of(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON),
                    () -> LANCE_REF[0].get() // 延迟引用

            )
            {
                public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
                    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

                    if(equipmentSlot==EquipmentSlot.MAINHAND) {
                        builder.put(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "captain_greg_t_atk", PeyroScytheConfig.captainGregScytheATK.get(), AttributeModifier.Operation.ADDITION));
                        builder.put(Attributes.ATTACK_SPEED,
                                new AttributeModifier(BASE_ATTACK_SPEED_UUID, "captain_greg_t_spd", PeyroScytheConfig.captainGregScytheSPD.get(), AttributeModifier.Operation.ADDITION));
                    }
                    return builder.build();
                }
            }
    );

    public static final RegistryObject<Item> CAPTAIN_GREG_LANCE = ITEMS.register("captain_greg_lance",
            () -> new MorphingMagicWeaponItem(
                    NewExtendedWeaponTier.CAPTAIN_GREG,
                    6.0F, -1.33F,
                    new SpellDataRegistryHolder[]{ /* spell holders */ },
                    Map.of(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON),
                    () -> SCYTHE_REF[0].get() // 延迟引用
            )
            {
                public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
                    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

                    if(equipmentSlot==EquipmentSlot.MAINHAND) {
                        builder.put(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "captain_greg_lance_atk", PeyroScytheConfig.captainGregLanceATK.get(), AttributeModifier.Operation.ADDITION));
                        builder.put(Attributes.ATTACK_SPEED,
                                new AttributeModifier(BASE_ATTACK_SPEED_UUID, "captain_greg_lance_spd", PeyroScytheConfig.captainGregLanceSPD.get(), AttributeModifier.Operation.ADDITION));
                    }
                    return builder.build();
                }
            }
    );

    // 静态块里完成互绑
    static {
        LANCE_REF[0] = CAPTAIN_GREG_LANCE;
        SCYTHE_REF[0] = CAPTAIN_GREG_TRANSFORMABLE;
    }
    public static final RegistryObject<Item> CAPTAIN_GREG_DONUT = ITEMS.register("captain_greg_donut", () -> new GregDonutItem(new GregDonutItem.Properties().food(GregDonutItem.CAPTAIN_GREG_DONUT).rarity(RARE)));



    private static final Supplier<Item>[] LANCE_REF_PA = new Supplier[]{null};
    private static final Supplier<Item>[] SCYTHE_REF_PA = new Supplier[]{null};


    public static final RegistryObject<Item> CAPTAIN_GREG_PA = ITEMS.register("captain_greg_pa",
            () -> new MorphingMagicWeaponItem(
                    NewExtendedWeaponTier.CAPTAIN_GREG,
                    8.0F , -2.0F,
                    new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(SpellRegistry.FROST_STEP_SPELL, 3)},
                    Map.of(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON),
                    () -> LANCE_REF_PA[0].get() // 延迟引用

            )
            {
                public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
                    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

                    if(equipmentSlot==EquipmentSlot.MAINHAND) {
                        builder.put(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "captain_greg_pa_atk", PeyroScytheConfig.captainGregScytheATK.get(), AttributeModifier.Operation.ADDITION));
                        builder.put(Attributes.ATTACK_SPEED,
                                new AttributeModifier(BASE_ATTACK_SPEED_UUID, "captain_greg_pa_spd", PeyroScytheConfig.captainGregScytheSPD.get(), AttributeModifier.Operation.ADDITION));
                    }
                    return builder.build();
                }
            }
    );

    public static final RegistryObject<Item> CAPTAIN_GREG_LANCE_PA = ITEMS.register("captain_greg_lance_pa",
            () -> new MorphingMagicWeaponItem(
                    NewExtendedWeaponTier.CAPTAIN_GREG,
                    6.0F, -1.33F,
                    new SpellDataRegistryHolder[]{ /* spell holders */ },
                    Map.of(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON),
                    () -> SCYTHE_REF_PA[0].get() // 延迟引用
            )
            {
                public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
                    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

                    if(equipmentSlot==EquipmentSlot.MAINHAND) {
                        builder.put(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "captain_greg_lance_pa_atk", PeyroScytheConfig.captainGregLanceATK.get(), AttributeModifier.Operation.ADDITION));
                        builder.put(Attributes.ATTACK_SPEED,
                                new AttributeModifier(BASE_ATTACK_SPEED_UUID, "captain_greg_lance_pa_spd", PeyroScytheConfig.captainGregLanceSPD.get(), AttributeModifier.Operation.ADDITION));
                    }
                    return builder.build();
                }
            }
    );

    // 静态块里完成互绑
    static {
        LANCE_REF_PA[0] = CAPTAIN_GREG_LANCE_PA;
        SCYTHE_REF_PA[0] = CAPTAIN_GREG_PA;
    }









}
