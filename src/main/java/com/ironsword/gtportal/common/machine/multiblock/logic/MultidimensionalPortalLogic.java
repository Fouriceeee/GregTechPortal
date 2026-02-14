package com.ironsword.gtportal.common.machine.multiblock.logic;

import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.ironsword.gtportal.common.machine.multiblock.MultidimensionalPortalControllerMachine;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

public class MultidimensionalPortalLogic extends RecipeLogic implements IWorkable {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(MultidimensionalPortalLogic.class, RecipeLogic.MANAGED_FIELD_HOLDER);

    public MultidimensionalPortalLogic(IRecipeLogicMachine machine) {
        super(machine);
    }

    public MultidimensionalPortalControllerMachine getPortalMachine(){
        return (MultidimensionalPortalControllerMachine) machine;
    }

//    @Override
//    public void setStatus(Status status) {
//        Status oldStatus = getStatus();
//        super.setStatus(status);
//        if (oldStatus != status){
//            TestPortalMachine portal = (TestPortalMachine) machine;
//            if (status == Status.WORKING){
//                portal.fillBlock(portal.getPortalBlockState());
//            }
//        }
//    }
}
