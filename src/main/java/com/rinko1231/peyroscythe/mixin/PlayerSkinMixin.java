package com.rinko1231.peyroscythe.mixin;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class PlayerSkinMixin {

    @Unique
    private static final ResourceLocation _skin$Peyro = PeyroScythe.id( "textures/entity/peyro.png");


    @Inject(method = "getSkinTextureLocation", at = @At("HEAD"), cancellable = true)
    private void injectCustomSkin(CallbackInfoReturnable<ResourceLocation> cir) {
        AbstractClientPlayer self = (AbstractClientPlayer)(Object)this;

        if (self.hasEffect(MobEffectRegistry.PEYRO_CHAN.get())) {
            cir.setReturnValue(_skin$Peyro);
        }
    }

    @Inject(method = "getModelName", at = @At("HEAD"), cancellable = true)
    private void injectCustomModel(CallbackInfoReturnable<String> cir) {
        AbstractClientPlayer self = (AbstractClientPlayer)(Object)this;

        if (self.hasEffect(MobEffectRegistry.PEYRO_CHAN.get())) {
            cir.setReturnValue("slim");
        }
    }
}