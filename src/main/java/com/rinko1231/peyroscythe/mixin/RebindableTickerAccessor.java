package com.rinko1231.peyroscythe.mixin;

import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelChunk.RebindableTickingBlockEntityWrapper.class)
public interface RebindableTickerAccessor {
    @Accessor("ticker")
    TickingBlockEntity getTicker();

    @Accessor("ticker")
    void setTicker(TickingBlockEntity value);
}
