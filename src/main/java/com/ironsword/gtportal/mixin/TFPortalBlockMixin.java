package com.ironsword.gtportal.mixin;

import com.ironsword.gtportal.GTPConfigHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.block.TFPortalBlock;

//unused, cause runServer failure
@Mixin(value = TFPortalBlock.class,remap = false)
public class TFPortalBlockMixin {

    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Ltwilightforest/block/TFPortalBlock;causeLightning(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Z)V",
                    shift = At.Shift.AFTER),
            method = "tryToCreatePortal",
            cancellable = true)
    public void injectTryToCreatePortal(Level level, BlockPos pos, ItemEntity catalyst, @Nullable Player player,CallbackInfoReturnable<Boolean> cir){
        if (GTPConfigHolder.INSTANCE.portalGateConfigs.allowVanillaTwilightForestPortalGate){
            return;
        }
        if (player != null){
            player.displayClientMessage(Component.translatable("gtportal.clientmessage.banned_structure"),true);
        }
        cir.setReturnValue(false);
    }
}
