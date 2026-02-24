package com.ironsword.gtportal.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class GTPEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry emiRegistry) {
        //emiRegistry.addWorkstation(EmiRecipeCategory, EmiStack.of(GTPMachines.PORTAL_CONTROLLER.asStack()));
    }

    @Override
    public void initialize(EmiInitRegistry registry) {
        //registry.addRegistryAdapter();
    }
}
