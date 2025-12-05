package com.ironsword.gtportal.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtportal.GTPortal;
import com.ironsword.gtportal.client.renderer.PortalBlockRender;
import com.ironsword.gtportal.common.machine.multiblock.PortalControllerMachine;
import com.ironsword.gtportal.common.machine.multiblock.part.DimensionDataHatchMachine;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.ironsword.gtportal.common.registry.GTPRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

import static com.gregtechceu.gtceu.api.GTValues.LV;
import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;


public class GTPMachines {
    static {
        GTPRegistries.REGISTRATE.creativeModeTab(()-> GTPCreativeModeTabs.GTP_TAB);
    }


    public static final MachineDefinition DIMENSION_DATA_HATCH = GTPRegistries.REGISTRATE
            .machine("dimension_data_hatch",DimensionDataHatchMachine::new)
            .langValue("Dimension Data Hatch")
            .tier(LV)
            .rotationState(RotationState.ALL)
            .modelProperty(GTMachineModelProperties.IS_FORMED, false)
            .overlayTieredHullModel(new ResourceLocation(GTCEu.MOD_ID, "block/machine/part/data_access_hatch" ))
            .register();

    public static final MultiblockMachineDefinition PORTAL_CONTROLLER = GTPRegistries.REGISTRATE
            .multiblock("portal_controller", PortalControllerMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .appearanceBlock(()->Blocks.OBSIDIAN)
            .pattern(definition-> FactoryBlockPattern.start()
                    .aisle( "XXPXX",
                            "X   X",
                            "X   X",
                            "X   X",
                            "XXXXX")
                    .where('X',blocks(Blocks.OBSIDIAN)
                            .or(blocks(DIMENSION_DATA_HATCH.get()).setExactLimit(1))
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setExactLimit(1)))
                    .where(' ', Predicates.air())
                    .where('P',Predicates.controller(blocks(definition.getBlock())))
                    .build()
            )
            .langValue("Portal Controller")
            .hasBER(true)
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTCEu.id("block/casings/gcym/atomic_casing"),
                    GTPortal.id("block/test"))
                    .andThen(b->b.addDynamicRenderer(()->
                            new PortalBlockRender(Optional.of(GTPBlocks.NETHER_PORTAL_BLOCK.get()))
                    )))
            .register();


    public static void init() {}
}
