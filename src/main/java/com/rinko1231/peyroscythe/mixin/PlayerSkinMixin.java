package com.rinko1231.peyroscythe.mixin;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
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


    @Unique
    private static final PlayerSkin PLAYER_SKIN = new PlayerSkin(_skin$Peyro, null, null, null, PlayerSkin.Model.SLIM, true);

    @Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
    private void skin(CallbackInfoReturnable<PlayerSkin> cir) {
        AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;
        if (
                player.level().isClientSide
                        && player.hasEffect(MobEffectRegistry.PEYRO_CHAN)
        ) {
            cir.setReturnValue(PLAYER_SKIN);
        }
    }
}