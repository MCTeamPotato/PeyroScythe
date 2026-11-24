package com.rinko1231.peyroscythe.network.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PeyroMagicProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<PeyroMagicData> PLAYER_PEYRO_MAGIC = CapabilityManager.get(new CapabilityToken<>(){});

    private final PeyroMagicData data;

    public PeyroMagicProvider(ServerPlayer player) {
        this.data = new PeyroMagicData(player);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == PLAYER_PEYRO_MAGIC ? LazyOptional.of(() -> (T) data) : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        data.saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        data.loadNBTData(nbt);
    }
}
