package com.ironsword.gtportal.common.machine.multiblock.logic;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.ironsword.gtportal.common.machine.multiblock.TestPortalMachine;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

public class TestPortalLogic extends RecipeLogic implements IWorkable {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(TestPortalLogic.class, RecipeLogic.MANAGED_FIELD_HOLDER);

    public TestPortalLogic(IRecipeLogicMachine machine) {
        super(machine);
    }

    public TestPortalMachine getPortalMachine(){
        return (TestPortalMachine) machine;
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
