package com.ironsword.gtportal.data;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class LangHandler extends com.gregtechceu.gtceu.data.lang.LangHandler {
    public static void init(RegistrateLangProvider provider) {
        provider.add("gtportal.clientmessage.banned_structure","The structure is banned. Check the Quest Book for more information.");

        //machine
        provider.add("gtportal.machine.tooltip.dimension","Dimension");
        provider.add("gtportal.machine.tooltip.position","Position");
        provider.add("gtportal.machine.tooltip.no_data","No Destination Set");

        //tooltips
        //machine
        provider.add("gtportal.tooltip.machine.simple_nether_portal_controller","This machine will §c§lnot§r create a portal to return\nBring enough things to build a overworld portal BEFORE you enter the nether");

        //item
        provider.add("gtportal.tooltip.item.dim_data_recorder","Shift + Right click at Portal Controller to record dimension data");

        //dimension infos
        provider.add("gtportal.dimension.empty","Unknown Dimension");
        provider.add("gtportal.dimension.overworld","Overworld");
        provider.add("gtportal.dimension.nether","Nether");
        provider.add("gtportal.dimension.end","End");
        provider.add("gtportal.dimension.aether","The Aether");
        provider.add("gtportal.dimension.twilight","Twilight Forest");

        //creativeModeTabs
        provider.add("gtportal.creativemodetab.main","GT Portal");
        provider.add("gtportal.creativemodetab.dimension_data_sticks","Dimension Data Sticks");

        //jei
        provider.add("gtceu.dimension_teleport_recipes","Teleport Energy");

        //test_recipe


    }
}
