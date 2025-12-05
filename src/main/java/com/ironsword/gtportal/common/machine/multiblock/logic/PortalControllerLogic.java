package com.ironsword.gtportal.common.machine.multiblock.logic;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.ironsword.gtportal.common.machine.multiblock.PortalControllerMachine;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class PortalControllerLogic extends RecipeLogic implements IWorkable {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(PortalControllerLogic.class, RecipeLogic.MANAGED_FIELD_HOLDER);

    @Setter
    @Nullable
    private IEnergyContainer energyContainer;

    public PortalControllerLogic(IRecipeLogicMachine machine) {
        super(machine);
    }

    @Override
    public PortalControllerMachine getMachine() {
        return (PortalControllerMachine) machine;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public void serverTick(){
        if (duration > 0){
            if (!consumeEnergy()){
                progress = 0;

                setWaiting(Component.translatable("gtceu.recipe_logic.insufficient_in").append(": ")
                        .append(EURecipeCapability.CAP.getName()));
                return;
            }
            setStatus(Status.WORKING);
            progress = 1;
        }else {
            setStatus(Status.IDLE);
            machine.afterWorking();
        }
    }

    protected boolean consumeEnergy(){
        long energyToDrain = getMachine().getEnergyPerTick();
        if (energyContainer!=null){
            long resultEnergy = energyContainer.getEnergyStored() - energyToDrain;
            if (resultEnergy >= 0 && resultEnergy<= energyContainer.getEnergyCapacity()) {
                energyContainer.removeEnergy(energyToDrain);
                return true;
            }
        }
        return false;
    }

    public void setDuration(int max) {
        this.duration = max;
    }
}
