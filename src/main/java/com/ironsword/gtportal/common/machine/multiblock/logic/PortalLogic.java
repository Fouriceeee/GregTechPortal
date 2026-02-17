package com.ironsword.gtportal.common.machine.multiblock.logic;

import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.ironsword.gtportal.common.machine.multiblock.MultidimensionalPortalControllerMachine;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class PortalLogic extends RecipeLogic implements IWorkable {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(PortalLogic.class, RecipeLogic.MANAGED_FIELD_HOLDER);

    public PortalLogic(IRecipeLogicMachine machine) {
        super(machine);
    }

    public MultidimensionalPortalControllerMachine getPortalMachine(){
        return (MultidimensionalPortalControllerMachine) machine;
    }

    @Override
    public void setWaiting(@Nullable Component reason) {
        super.setWaiting(reason);
        this.interruptRecipe();
    }
}
