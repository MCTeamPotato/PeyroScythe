package com.rinko1231.peyroscythe.item;

import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class GregDonutItem extends Item {


    public GregDonutItem(Properties properties) {
        super(properties);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flagIn) {
        String OriginalId = this.getDescriptionId();
        String TooltipKey = "tooltip." + OriginalId;
        tooltipComponents.add(Component.translatable(ChatFormatting.GRAY + "" + I18n.get(TooltipKey)));
        super.appendHoverText(stack, context, tooltipComponents, flagIn);
    }

    public static final FoodProperties CAPTAIN_GREG_DONUT = (new FoodProperties.Builder())
            .nutrition(8).saturationModifier(1.2f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 300, 1), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 0), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.WATER_BREATHING, 300, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 300, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffectRegistry.PEYRO_CHAN, 1200, 0, false, false, false), 1.0F)
            .build();


}
