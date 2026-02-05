package com.ironsword.gtportal.integration.emi;


import com.ironsword.gtportal.common.data.GTPMachines;
import com.ironsword.gtportal.common.data.GTPRecipeTypes;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;

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
