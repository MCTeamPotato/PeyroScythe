package com.rinko1231.peyroscythe.init;

import com.rinko1231.peyroscythe.block.MundusBlock;
import com.rinko1231.peyroscythe.block.MundusBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.rinko1231.peyroscythe.PeyroScythe.MODID;

// ModBlocks.java
public class BlockRegistry {


    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    // —— mundus 方块 —— //
    public static final RegistryObject<MundusBlock> MUNDUS_BLOCK = BLOCKS.register("mundus",
            () -> new MundusBlock(BlockBehaviour.Properties
                    .of()
                    .noOcclusion() // 不占满体素，避免遮挡
                    .noCollission() // 可穿过
                    .strength(0.0F)
                    .lightLevel(state -> state.getValue(MundusBlock.LUMINANCE)) // 亮度由状态控制
            ));

    public static final RegistryObject<Item> MUNDUS_ITEM = ITEMS.register("mundus",
            () -> new BlockItem(MUNDUS_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<MundusBlockEntity>> MUNDUS_BE =
            BLOCK_ENTITIES.register("mundus",
                    () -> BlockEntityType.Builder.of(MundusBlockEntity::new, MUNDUS_BLOCK.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }
}
