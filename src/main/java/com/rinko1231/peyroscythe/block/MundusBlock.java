package com.rinko1231.peyroscythe.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

// MundusBlock.java
public class MundusBlock extends Block implements EntityBlock {
    // 用状态属性保存亮度（0~15），以便原生发光
    public static final IntegerProperty LUMINANCE = IntegerProperty.create("lum", 0, 15);

    // 小立方体形状：边长 0.5 格（你可改成 0.3、0.4…）
    private static final VoxelShape VISUAL_SHAPE =
            Block.box(4, 4, 4, 12, 12, 12); // (4/16=0.25) 到 (12/16=0.75)

    public MundusBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(LUMINANCE, 12));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(LUMINANCE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return VISUAL_SHAPE;
    }

    // 不阻挡、可穿过
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    // BlockEntity
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MundusBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, p, s, be) -> {
            if (be instanceof MundusBlockEntity e) e.serverTick();
        };
    }
}
