package com.ironsword.gtportal.mixin;

import com.aetherteam.aether.event.hooks.DimensionHooks;
import com.ironsword.gtportal.GTPConfigHolder;
import com.ironsword.gtportal.common.machine.multiblock.PortalControllerMachine;
import com.ironsword.gtportal.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(DimensionHooks.class)
public class DimensionHooksMixin {

    @Inject(
            method = "createPortal",
            at = @At(value = "INVOKE", target = "Lcom/aetherteam/aether/block/portal/AetherPortalShape;createPortalBlocks()V"),
            cancellable = true,
            remap = false
    )
    private static void injectCreatePortal(Player player, Level level, BlockPos pos, @Nullable Direction direction, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<Boolean> cir){
        if (GTPConfigHolder.INSTANCE.portalGateConfigs.allowVanillaAetherPortalGate){
            return;
        }

        player.displayClientMessage(Component.translatable("gtportal.clientmessage.banned_structure"),true);
        cir.setReturnValue(false);
    }

    @Inject(
            method = "detectWaterInFrame",
            at = @At(value = "INVOKE", target = "Lcom/aetherteam/aether/block/portal/AetherPortalShape;createPortalBlocks()V"),
            cancellable = true,
            remap = false
    )
    private static void injectDetectWaterInFrame(LevelAccessor levelAccessor, BlockPos pos, BlockState blockState, FluidState fluidState,CallbackInfoReturnable<Boolean> cir){
        if (GTPConfigHolder.INSTANCE.portalGateConfigs.allowVanillaAetherPortalGate){
            return;
        }

        Utils.displayMessageInBoxes((Level) levelAccessor,pos,5,Component.translatable("gtportal.clientmessage.banned_structure"));
        cir.setReturnValue(false);
    }
}
