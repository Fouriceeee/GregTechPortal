package com.ironsword.gtportal.mixin;

import com.ironsword.gtportal.utils.TFPortalBlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import twilightforest.TFTickHandler;
import twilightforest.block.TFPortalBlock;

@Mixin(value = TFTickHandler.class,remap = false)
public abstract class TFTickHandlerMixin {

    @Redirect(
            method = "checkForPortalCreation",
            at = @At(
                    value = "INVOKE",
                    target = "Ltwilightforest/block/TFPortalBlock;tryToCreatePortal(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/entity/player/Player;)Z"
            )
    )
    private static boolean redirectTryToCreatePortal(TFPortalBlock block, Level level, BlockPos pos, ItemEntity catalyst, @Nullable Player player){
        return TFPortalBlockUtils.tryToCreatePortal(level,pos,catalyst,player);
    }

}
