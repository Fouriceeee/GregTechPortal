package com.ironsword.gtportal.common.data;

import com.google.common.collect.ImmutableSet;
import com.ironsword.gtportal.GTPortal;
import com.lowdragmc.lowdraglib.LDLib;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class GTPPoiTypes {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, GTPortal.MODID);

    public static final RegistryObject<PoiType> OVERWORLD_PORTAL_POI = POI_TYPES.register("overworld_portal",()->new PoiType(getBlockStates(
            GTPBlocks.OVERWORLD_PORTAL_BLOCK.get()
    ),0,1));
    public static final RegistryObject<PoiType> NETHER_PORTAL_POI = POI_TYPES.register("nether_portal",()->new PoiType(getBlockStates(
            GTPBlocks.NETHER_PORTAL_BLOCK.get()
    ),0,1));
    public static final RegistryObject<PoiType> END_PORTAL_POI = POI_TYPES.register("end_portal",()->new PoiType(getBlockStates(
            GTPBlocks.END_PORTAL_BLOCK.get()
    ),0,1));

    public static RegistryObject<PoiType> AETHER_PORTAL_POI;
    public static RegistryObject<PoiType> TWILIGHT_PORTAL_POI;

    public static final RegistryObject<PoiType> MULTI_PCM_POI = POI_TYPES.register("multi_pcm",()->createSimplePoiType(GTPMachines.MULTIDIMENSIONAL_PORTAL_CONTROLLER.getBlock()));

    public static final RegistryObject<PoiType> OVERWORLD_PCM_POI = POI_TYPES.register("overworld_pcm",()->createSimplePoiType(GTPMachines.SIMPLE_OVERWORLD_PORTAL_CONTROLLER.getBlock()));
    public static final RegistryObject<PoiType> NETHER_PCM_POI = POI_TYPES.register("nether_pcm",()->createSimplePoiType(GTPMachines.SIMPLE_NETHER_PORTAL_CONTROLLER.getBlock()));
    public static final RegistryObject<PoiType> END_PCM_POI = POI_TYPES.register("end_pcm",()->createSimplePoiType(GTPMachines.SIMPLE_END_PORTAL_CONTROLLER.getBlock()));

    public static RegistryObject<PoiType> AETHER_PCM_POI;
    public static RegistryObject<PoiType> TWILIGHT_PCM_POI;


    private static <T extends Block> PoiType createSimplePoiType(T block){
        return new PoiType(ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates()),0,1);
    }

    private static <T extends Block> Set<BlockState> getBlockStates(T block) {

//        return Arrays.stream(blocks)
//                .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
//                .collect(Collectors.toSet());

        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    public static void register(IEventBus bus){
        if (LDLib.isModLoaded("aether")){
            AETHER_PORTAL_POI = POI_TYPES.register("aether_portal",()->new PoiType(getBlockStates(
                    GTPBlocks.AETHER_PORTAL_BLOCK.get()
            ),0,1));

            AETHER_PCM_POI = POI_TYPES.register("aether_pcm",()->createSimplePoiType(GTPMachines.SIMPLE_AETHER_PORTAL_CONTROLLER.getBlock()));
        }
        if (LDLib.isModLoaded("twilightforest")){
            TWILIGHT_PORTAL_POI = POI_TYPES.register("twilight_portal",()->new PoiType(getBlockStates(
                    GTPBlocks.TWILIGHT_PORTAL_BLOCK.get()
            ),0,1));

            TWILIGHT_PCM_POI = POI_TYPES.register("twilight_pcm",()->createSimplePoiType(GTPMachines.SIMPLE_TWILIGHT_PORTAL_CONTROLLER.getBlock()));
        }
        POI_TYPES.register(bus);
    }
}
