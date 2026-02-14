package com.ironsword.gtportal;

import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import com.ironsword.gtportal.api.portal.teleporter.GTPTeleporter;
import com.ironsword.gtportal.api.portal.teleporter.TwilightTeleporter;
import com.ironsword.gtportal.client.ClientProxy;
import com.ironsword.gtportal.client.renderer.TestRenderer;
import com.ironsword.gtportal.common.CommonProxy;
import com.ironsword.gtportal.common.data.*;
import com.ironsword.gtportal.common.machine.multiblock.TestPortalMachine;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.ironsword.gtportal.common.registry.GTPRegistries;
import com.ironsword.gtportal.data.GTPDatagen;
import com.ironsword.gtportal.integration.aether.AetherInit;
import com.ironsword.gtportal.integration.twilightforest.TwilightInit;
import com.lowdragmc.lowdraglib.LDLib;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import twilightforest.init.TFBlocks;
import twilightforest.world.registration.TFGenerationSettings;

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
