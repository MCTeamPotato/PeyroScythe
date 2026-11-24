package com.rinko1231.peyroscythe.item;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;

public class IceCreamItem extends Item {

    private static final String CUSTOM_DURABILITY_TAG = "CustomDurability";
    private static final int MAX_TICKS = 200; // 10秒 = 200 tick

    public IceCreamItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide) return; // 客户端不处理逻辑
        if( entity instanceof ServerPlayer sp){

        CompoundTag tag = stack.getOrCreateTag();

        // 初始化自定义耐久
        if (!tag.contains(CUSTOM_DURABILITY_TAG)) {
            tag.putInt(CUSTOM_DURABILITY_TAG, MAX_TICKS);
        }

        int durability = tag.getInt(CUSTOM_DURABILITY_TAG);

        // 每tick减少1点自定义耐久
        durability -= 1;

        if (durability <= 0) {
            // 耐久耗尽，物品消失
            sp.getInventory().removeItem(stack); // 如果entity是玩家，这会删除物品
        } else {
            // 更新耐久
            tag.putInt(CUSTOM_DURABILITY_TAG, durability);
        }
        }
    }
}
