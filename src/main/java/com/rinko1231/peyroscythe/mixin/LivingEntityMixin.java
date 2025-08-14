package com.rinko1231.peyroscythe.mixin;

import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    // -------------------------------------------------------------------------
    // 原版在 hurt 方法里一堆 else if
    // 把防火伤逻辑硬写成 this.hasEffect(MobEffects.FIRE_RESISTANCE)
    //   → 恭喜你，如果想让其他效果免疫火焰，乖乖自己mixin吧
    // -------------------------------------------------------------------------

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void sinfireImmuneToFire(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
         if (!self.level().isClientSide && self.hasEffect(MobEffectRegistry.SINFIRE_EMBRACE.get())) {
            if (source.is(DamageTypeTags.IS_FIRE)) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}
