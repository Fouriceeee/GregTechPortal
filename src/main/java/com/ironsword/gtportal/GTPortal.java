package com.ironsword.gtportal;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.ironsword.gtportal.common.data.GTPBlockEntities;
import com.ironsword.gtportal.common.data.GTPBlocks;
import com.ironsword.gtportal.common.data.GTPItems;
import com.ironsword.gtportal.common.data.GTPMachines;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.ironsword.gtportal.common.registry.GTPRegistries;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@SuppressWarnings({"unused"})
@Mod(GTPortal.MODID)
public class GTPortal
{
    public static final String MODID = "gtportal";
    private static final Logger LOGGER = LogUtils.getLogger();

    public GTPortal(FMLJavaModLoadingContext context)
    {
        GTPortal.init();
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(this);

        bus.addGenericListener(MachineDefinition.class,this::registerMachines);
    }

    private static void init() {
        GTPCreativeModeTabs.init();
        GTPItems.init();
        GTPBlocks.init();
        GTPBlockEntities.init();

        GTPRegistries.REGISTRATE.registerRegistrate();
    }

    @SubscribeEvent
    public void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event){
        GTPMachines.init();
    }

    public static ResourceLocation id(String name){
        return ResourceLocation.tryBuild(MODID,name);
    }

}
