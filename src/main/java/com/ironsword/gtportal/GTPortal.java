package com.ironsword.gtportal;

import com.ironsword.gtportal.client.ClientProxy;
import com.ironsword.gtportal.common.CommonProxy;
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
//        GTPortal.init();
//        var bus = FMLJavaModLoadingContext.get().getModEventBus();
//        bus.register(this);

        DistExecutor.unsafeRunForDist(()-> ClientProxy::new,()-> CommonProxy::new);
    }

    private static void init() {
//        if (LDLib.isModLoaded("aether")){
//            TestPortalMachine.MAP.put(
//                    AetherDimensions.AETHER_LEVEL.location(),
//                    Pair.of(
//                            GTPBlocks.TEST_AETHER_PORTAL_BLOCK.get(),
//                            (entity, currWorld, destWorld, coordinate) ->
//                                    entity.changeDimension(destWorld,new GTPTeleporter(currWorld,coordinate, Blocks.GLOWSTONE))
//                    ));
//        }
//
//        if (LDLib.isModLoaded("twilightforest")){
//            TestPortalMachine.MAP.put(
//                    TFGenerationSettings.DIMENSION,
//                    Pair.of(
//                            GTPBlocks.TEST_TWILIGHT_PORTAL_BLOCK.get(),
//                            (entity, currWorld, destWorld, coordinate) ->
//                                    entity.changeDimension(destWorld,new TwilightTeleporter(currWorld,coordinate, TFBlocks.ROOT_BLOCK.get(),entity))
//                    ));
//        }
    }

    public static ResourceLocation id(String name){
        return ResourceLocation.tryBuild(MODID,name);
    }

}
