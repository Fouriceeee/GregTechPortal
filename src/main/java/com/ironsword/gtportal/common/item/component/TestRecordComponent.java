package com.ironsword.gtportal.common.item.component;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.ironsword.gtportal.common.machine.multiblock.TestPortalMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class TestRecordComponent extends TestComponent implements IInteractionItem {
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getHand() == InteractionHand.MAIN_HAND ){
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            if (level.getBlockEntity(pos) instanceof MetaMachineBlockEntity machineEntity && machineEntity.getMetaMachine() instanceof TestPortalMachine){
                ItemStack stack = context.getItemInHand();
                putDimensionNbt(stack,level.dimension().location());
                putCoordinateNbt(stack,pos);
                return InteractionResult.SUCCESS;
            }
        }
        return IInteractionItem.super.useOn(context);
    }
}
