package com.ironsword.gtportal;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.ironsword.gtportal.common.registry.GTPRegistries;

@GTAddon
public class GTPortalAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return GTPRegistries.REGISTRATE;
    }

    @Override
    public void initializeAddon() {

    }

    @Override
    public String addonModId() {
        return GTPortal.MODID;
    }
}
