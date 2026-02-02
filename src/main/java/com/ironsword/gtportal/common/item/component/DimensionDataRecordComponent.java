package com.ironsword.gtportal.common.item.component;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.ironsword.gtportal.api.portal.DimensionData;
import com.ironsword.gtportal.common.machine.multiblock.PortalControllerMachine;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DimensionDataRecordComponent extends DimensionDataComponent implements IInteractionItem{
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getHand() == InteractionHand.MAIN_HAND ){
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(context.getClickedPos());
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof MetaMachineBlockEntity machineEntity && machineEntity.getMetaMachine() instanceof PortalControllerMachine portalMachine){
                ItemStack stack = context.getItemInHand();
                changeDimensionData(stack,
                        new DimensionData(level.dimension().location(),portalMachine.getPos()));
                return InteractionResult.SUCCESS;
            }
        }
        return IInteractionItem.super.useOn(context);
    }

}
