package com.rinko1231.peyroscythe.mixin;

import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin {
    @Inject(method = "addEffect", at = @At("HEAD"), cancellable = true)
    private void allowDeathSmoke(MobEffectInstance effect, @Nullable Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (effect.getEffect() == MobEffectRegistry.DEATH_SMOKE_EROSION.get()) {
            cir.setReturnValue(true);
            cir.cancel();

            //硬编码是吧，直接创死你喵
        }
    }
}
