package com.ironsword.gtportal;

import com.ironsword.gtportal.client.ClientProxy;
import com.ironsword.gtportal.common.CommonProxy;
import com.ironsword.gtportal.common.data.GTPBlocks;
import com.ironsword.gtportal.data.GTPDatagen;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
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

        DistExecutor.unsafeRunForDist(()-> ClientProxy::new,()-> CommonProxy::new);
    }


    public static ResourceLocation id(String name){
        return ResourceLocation.tryBuild(MODID,name);
    }

}
