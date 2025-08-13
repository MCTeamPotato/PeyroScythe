package com.rinko1231.peyroscythe.item;

import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
public class MorphingMagicWeaponItem extends NewMagicSwordItem {
    private final Supplier<Item> morphTarget; // 变形后的目标物品

    public MorphingMagicWeaponItem(
            Tier tier,
            double attackDamage, double attackSpeed,
            SpellDataRegistryHolder[] spellDataRegistryHolders,
            Map<Attribute, AttributeModifier> additionalAttributes,
            Properties properties,
            Supplier<Item> morphTarget
    ) {
        super(tier, attackDamage, attackSpeed, spellDataRegistryHolders, additionalAttributes, properties);
        this.morphTarget = morphTarget;
    }

    /**
     * 变形成另一形态
     */
    public void morph(ItemStack stack, Player player) {
        if (morphTarget == null || morphTarget.get() == null) return;

        // 保存旧物品的 NBT
        CompoundTag oldTag = stack.getTag() != null ? stack.getTag().copy() : new CompoundTag();

        // 替换为新物品
        ItemStack newStack = new ItemStack(morphTarget.get(), stack.getCount());
        if (!oldTag.isEmpty()) {
            newStack.setTag(oldTag);
        }

        // 因为是 NewMagicSwordItem 子类，可能需要重新初始化法术容器
        if (!ISpellContainer.isSpellContainer(newStack)) {
            if (newStack.getItem() instanceof NewMagicSwordItem magicSword) {
                magicSword.initializeSpellContainer(newStack);
            }
        }

        // 替换玩家手上的物品
        player.setItemInHand(player.getUsedItemHand(), newStack);
    }

    /**
     * 空气潜行右击触发变形
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 必须潜行 + 对空气右击
        if (player.isShiftKeyDown() && player.pick(5.0D, 0.0F, false).getType() == HitResult.Type.MISS) {
            if (!level.isClientSide) {
                this.morph(stack, player);
            }
            // 返回成功（客户端也会有动画）
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (morphTarget != null && morphTarget.get() != null) {
            Item targetItem = morphTarget.get();
            tooltip.add(Component.translatable("tooltip.morphing_weapon.transforms_into",
                    targetItem.getDescription()));
        }
    }


}

