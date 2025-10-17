package com.ironsword.gtportal;

import com.ironsword.gtportal.common.data.GTPBlocks;
import com.ironsword.gtportal.common.data.GTPMachines;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.ironsword.gtportal.common.registry.GTPRegistries;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

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

    }

    private static void init() {
        GTPCreativeModeTabs.init();
        GTPBlocks.init();
        GTPMachines.init();
        GTPRegistries.REGISTRATE.registerRegistrate();
    }


}
