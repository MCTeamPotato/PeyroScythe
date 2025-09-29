package com.rinko1231.peyroscythe.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.rinko1231.peyroscythe.init.MobEffectRegistry.BLACK_FLAME_WINGS;

@Mixin(
        value = {IItemStackExtension.class},
        remap = false,
        priority = 0
)
public interface AnotherItemExtensionMixin {

        @Inject(
                method = {"canElytraFly"},
                at = {@At("RETURN")},
                cancellable = true,
                remap = false
        )
        default void canElytraFly(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
            if (entity.hasEffect(BLACK_FLAME_WINGS)) {
                cir.setReturnValue(true);
            }

        }

        @Inject(
                method = {"elytraFlightTick"},
                at = {@At("RETURN")},
                cancellable = true,
                remap = false
        )
        default void elytraFlightTick(LivingEntity entity, int flightTicks, CallbackInfoReturnable<Boolean> cir) {
            if (entity.hasEffect(BLACK_FLAME_WINGS)) {
                cir.setReturnValue(true);
            }

        }
    }
