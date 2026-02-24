package com.ironsword.gtportal.common;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.ironsword.gtportal.GTPConfigHolder;
import com.ironsword.gtportal.GTPortal;
import com.ironsword.gtportal.common.data.*;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.ironsword.gtportal.common.registry.GTPRegistries;
import com.ironsword.gtportal.data.GTPDatagen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = GTPortal.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {

    public CommonProxy(){
        init();
    }

    public static void init(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        GTPDatagen.initPre();
        GTPItems.init();
        GTPBlocks.init();

        bus.addGenericListener(MachineDefinition.class,CommonProxy::registerMachines);
        bus.addGenericListener(GTRecipeType.class,CommonProxy::registerRecipeTypes);

        GTPPoiTypes.register(bus);
        GTPCreativeModeTabs.init();
        GTPRegistries.REGISTRATE.registerRegistrate();
        GTPConfigHolder.init();
    }

    public static void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event){
        GTPMachines.init();
    }

    public static void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event){
        GTPRecipeTypes.init();
    }

}
