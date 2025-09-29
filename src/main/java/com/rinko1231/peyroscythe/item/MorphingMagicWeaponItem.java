package com.rinko1231.peyroscythe.item;

import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.util.List;
import java.util.function.Supplier;
public class MorphingMagicWeaponItem extends MagicSwordItem {
    private final Supplier<Item> morphTarget; // 变形后的目标物品

    public MorphingMagicWeaponItem(Tier pTier, Item.Properties pProperties, SpellDataRegistryHolder[] spellDataRegistryHolders,Supplier<Item> morphTarget) {
        super(pTier, pProperties,spellDataRegistryHolders);
        this.morphTarget = morphTarget;
    }


    /**
     * 变形成另一形态
     */
    public void morph(ItemStack stack, Player player) {
        if (morphTarget == null || morphTarget.get() == null) return;

        // 旧物品的数据组件复制器
        DataComponentMap oldComponents = stack.getComponents();

        // 构造新物品
        ItemStack newStack = new ItemStack(morphTarget.get(), stack.getCount());

        // 将旧物品的组件迁移到新物品
        // 注意：有些组件（如 DAMAGE、CUSTOM_NAME、ENCHANTMENTS）需要特殊处理
        DataComponentMap.Builder builder = DataComponentMap.builder();

        for (DataComponentType<?> type : oldComponents.keySet()) {
            // 过滤掉和新武器强绑定的组件（如 attribute modifiers）
            if (type == DataComponents.ATTRIBUTE_MODIFIERS) continue;

            Object value = oldComponents.get(type);
            if (value != null) {
                // 泛型安全写入
                @SuppressWarnings("unchecked")
                DataComponentType<Object> t = (DataComponentType<Object>) type;
                builder.set(t, value);
            }
        }

        // 应用迁移后的组件
        newStack.applyComponents(builder.build());

        // 初始化新武器的法术容器（如果是魔法剑）
        if (!ISpellContainer.isSpellContainer(newStack)) {
            if (newStack.getItem() instanceof MagicSwordItem magicSword) {
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipComponents, flag);

        if (morphTarget != null && morphTarget.get() != null) {
            Item targetItem = morphTarget.get();
            tooltipComponents.add(Component.translatable("tooltip.peyroscythe.morphing_weapon.transforms_into",
                    targetItem.getDescription()));
        }
    }


}

