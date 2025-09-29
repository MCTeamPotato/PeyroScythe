package com.rinko1231.peyroscythe.api;

import net.minecraft.world.entity.Entity;

public interface PreventDismountNew {
    /**
     * 决定是否允许某个实体下马
     */
    boolean canEntityDismount(Entity rider);
}
