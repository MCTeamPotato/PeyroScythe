package com.rinko1231.peyroscythe.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CurioAdvanceItem extends Item implements ICurioItem {
    // key = 槽位id, value = index -> 属性映射
    private final Map<String, Function<Integer, Multimap<Holder<Attribute>, AttributeModifier>>> slotAttributes = new HashMap<>();

    public CurioAdvanceItem(Properties properties) {
        super(properties);
    }

    public boolean isEquippedBy(@Nullable LivingEntity entity) {
        return entity != null && (Boolean)CuriosApi.getCuriosInventory(entity).map((inv) -> inv.findFirstCurio(this).isPresent()).orElse(false);
    }

    @NotNull
    public ICurio.@NotNull SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo((SoundEvent) SoundEvents.ARMOR_EQUIP_CHAIN.value(), 1.0F, 1.0F);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext, ResourceLocation id, ItemStack stack) {

        Function<Integer, Multimap<Holder<Attribute>, AttributeModifier>> func =
                slotAttributes.get(slotContext.identifier());

        if (func != null) {
            return func.apply(slotContext.index());
        }

        return ICurioItem.defaultInstance.getAttributeModifiers(slotContext, id);
    }

    /**
     * 为某个槽位添加属性效果
     */
    public CurioAdvanceItem withAttributes(String slot, AttributeContainer... attributes) {
        slotAttributes.put(slot, (index) -> {
            ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();

            for (AttributeContainer holder : attributes) {
                String id = String.format("%s_%s", slot, index);
                builder.put(holder.attribute(), holder.createModifier(id));
            }

            return builder.build();
        });
        return this;
    }


}
