package com.ironsword.gtportal.data;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class LangHandler extends com.gregtechceu.gtceu.data.lang.LangHandler {
    public static void init(RegistrateLangProvider provider) {
        provider.add("gtportal.clientmessage.banned_structure","The structure is banned. Check the Quest Book for more information.");

        provider.add("gtportal.machine.tooltip.dimension","Dimension");
        provider.add("gtportal.machine.tooltip.position","Position");
        provider.add("gtportal.machine.tooltip.no_data","No Destination Set.");

        provider.add("gtportal.dimension.empty","Empty");
        provider.add("gtportal.dimension.overworld","Overworld");
        provider.add("gtportal.dimension.nether","Nether");
        provider.add("gtportal.dimension.end","End");
        provider.add("gtportal.dimension.aether","The Aether");
        provider.add("gtportal.dimension.twilight","Twilight Forest");

    }
}
