package com.ironsword.gtportal.common.machine.multiblock.logic;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

public class RecipePortalLogic extends RecipeLogic implements IWorkable {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(RecipePortalLogic.class, RecipeLogic.MANAGED_FIELD_HOLDER);

    @Setter
    @Nullable
    private IEnergyContainer energyContainer;

    public RecipePortalLogic(IRecipeLogicMachine machine) {
        super(machine);
    }


}
