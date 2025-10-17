package com.ironsword.gtportal.common.machine.multiblock.electric;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

public class PortalControllerMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(PortalControllerMachine.class,WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public PortalControllerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public boolean onWorking() {
        return super.onWorking();
    }

    @Override
    public void onWaiting() {
        super.onWaiting();
    }
}
