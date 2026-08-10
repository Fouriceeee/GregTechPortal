package com.ironsword.gtportal.common.machine.multiblock.logic;

import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.ironsword.gtportal.api.machine.feature.ITeleportMachine;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class PortalLogic extends RecipeLogic implements IWorkable {

    public PortalLogic(IRecipeLogicMachine machine) {
        super(machine);
    }

    protected ITeleportMachine getTeleportMachine(){
        return (ITeleportMachine) machine;
    }

    @Override
    public void setWaiting(@Nullable Component reason) {
        super.setWaiting(reason);
        this.interruptRecipe();
    }

    @Override
    public void handleRecipeWorking() {
        super.handleRecipeWorking();
        if (isWorking()){
            getTeleportMachine().teleportEntities();
        }
    }
}
