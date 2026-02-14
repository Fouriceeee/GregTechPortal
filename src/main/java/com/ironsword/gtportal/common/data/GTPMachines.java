package com.ironsword.gtportal.common.data;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtportal.GTPortal;
import com.ironsword.gtportal.client.renderer.PortalBlockRenderer;
import com.ironsword.gtportal.common.machine.multiblock.MultidimensionalPortalControllerMachine;
import com.ironsword.gtportal.common.machine.multiblock.SingleDimensionPortalControllerMachine;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.ironsword.gtportal.common.registry.GTPRegistries;
import net.minecraft.world.level.Level;

import java.util.Optional;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;


public class GTPMachines {
    static {
        GTPRegistries.REGISTRATE.creativeModeTab(()-> GTPCreativeModeTabs.GTP_TAB);
    }

//    public static final MachineDefinition DIMENSION_DATA_HATCH = GTPRegistries.REGISTRATE
//            .machine("dimension_data_hatch",DimensionDataHatchMachine::new)
//            .langValue("Dimension Data Hatch")
//            .tier(LV)
//            .rotationState(RotationState.ALL)
//            .modelProperty(GTMachineModelProperties.IS_FORMED, false)
//            .overlayTieredHullModel(new ResourceLocation(GTCEu.MOD_ID, "block/machine/part/data_access_hatch" ))
//            .register();

//    public static final MultiblockMachineDefinition PORTAL_CONTROLLER = GTPRegistries.REGISTRATE
//            .multiblock("portal_controller", PortalControllerMachine::new)
//            .rotationState(RotationState.ALL)
//            .recipeType(GTPRecipeTypes.DIMENSION_TELEPORT_RECIPES)
//            .appearanceBlock(()->GTPBlocks.PORTAL_FRAME.get())
//            .pattern(definition-> FactoryBlockPattern.start()
//                    .aisle( "XXPXX",
//                            "X   X",
//                            "X   X",
//                            "X   X",
//                            "XXXXX")
//                    .where('X',blocks(GTPBlocks.PORTAL_FRAME.get())
//                            .or(blocks(DIMENSION_DATA_HATCH.get()).setExactLimit(1))
//                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setExactLimit(1)))
//                    .where(' ', Predicates.air().or(blocks(GTPBlocks.DIMENSIONAL_PORTAL_BLOCK.get())))
//                    .where('P',Predicates.controller(blocks(definition.getBlock())))
//                    .build()
//            )
//            .langValue("Portal Controller")
//            .hasBER(true)
//            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
//            .model(createWorkableCasingMachineModel(GTPortal.id("block/portal_frame"),
//                    GTPortal.id("block/portal_controller_overlay")))
//            .register();

//    public static final MultiblockMachineDefinition SIMPLE_OVERWORLD_PORTAL_CONTROLLER = GTPRegistries.REGISTRATE
//            .multiblock("simple_overworld_portal_controller",iMachineBlockEntity -> new SimplePortalControllerMachine(DimensionInfo.OVERWORLD,iMachineBlockEntity))
//            .rotationState(RotationState.ALL)
//            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
//            .appearanceBlock(()->GTPBlocks.SIMPLE_OVERWORLD_PORTAL_FRAME.get())
//            .pattern(definition-> FactoryBlockPattern.start()
//                    .aisle( "XXPXX",
//                            "X   X",
//                            "X   X",
//                            "X   X",
//                            "XXXXX")
//                    .where('X',blocks(GTPBlocks.SIMPLE_OVERWORLD_PORTAL_FRAME.get()))
//                    .where(' ', Predicates.air().or(blocks(GTPBlocks.DIMENSIONAL_PORTAL_BLOCK.get())))
//                    .where('P',Predicates.controller(blocks(definition.getBlock())))
//                    .build()
//            )
//            .langValue("Simple Overworld Portal Controller")
//            .hasBER(true)
//            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
//            .model(createWorkableCasingMachineModel(GTPortal.id("block/simple_overworld_portal_frame"),
//                    GTPortal.id("block/portal_controller_overlay")))
//            .register();
//    public static final MultiblockMachineDefinition SIMPLE_NETHER_PORTAL_CONTROLLER = GTPRegistries.REGISTRATE
//            .multiblock("simple_nether_portal_controller",iMachineBlockEntity -> new SimplePortalControllerMachine(DimensionInfo.NETHER,iMachineBlockEntity))
//            .rotationState(RotationState.ALL)
//            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
//            .appearanceBlock(()->GTPBlocks.SIMPLE_NETHER_PORTAL_FRAME.get())
//            .pattern(definition-> FactoryBlockPattern.start()
//                    .aisle( "XXPXX",
//                            "X   X",
//                            "X   X",
//                            "X   X",
//                            "XXXXX")
//                    .where('X',blocks(GTPBlocks.SIMPLE_NETHER_PORTAL_FRAME.get()))
//                    .where(' ', Predicates.air().or(blocks(GTPBlocks.DIMENSIONAL_PORTAL_BLOCK.get())))
//                    .where('P',Predicates.controller(blocks(definition.getBlock())))
//                    .build()
//            )
//            .langValue("Simple Nether Portal Controller")
//            .hasBER(true)
//            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
//            .model(createWorkableCasingMachineModel(GTPortal.id("block/simple_nether_portal_frame"),
//                    GTPortal.id("block/portal_controller_overlay")))
//            .tooltips(Component.translatable("gtportal.tooltip.machine.simple_nether_portal_controller"))
//            .register();


    public static final MultiblockMachineDefinition MULTIDIMENSIONAL_PORTAL_CONTROLLER = GTPRegistries.REGISTRATE
            .multiblock("multidimensional_portal_controller", MultidimensionalPortalControllerMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE)
            .appearanceBlock(()->GTPBlocks.MULTIDIMENSIONAL_PORTAL_FRAME.get())
            .pattern(definition-> FactoryBlockPattern.start()
                    .aisle( "XPX",
                            "X X",
                            "X X",
                            "XXX")
                    .where('X',blocks(GTPBlocks.MULTIDIMENSIONAL_PORTAL_FRAME.get())
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setExactLimit(1)))
                    .where(' ', Predicates.air())
                    .where('P',Predicates.controller(blocks(definition.getBlock())))
                    .build()
            )
            .langValue("Multidimensional Portal Controller")
            .hasBER(true)
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTPortal.id("block/portal_frame"),
                    GTPortal.id("block/portal_controller_overlay"))
                    .andThen(b->b.addDynamicRenderer(()->new PortalBlockRenderer(Optional.empty()))))
            .register();

    public static final MultiblockMachineDefinition SIMPLE_OVERWORLD_PORTAL_CONTROLLER = GTPRegistries.REGISTRATE
            .multiblock("simple_overworld_portal_controller",iMachineBlockEntity -> new SingleDimensionPortalControllerMachine(iMachineBlockEntity, Level.OVERWORLD.location()))
            .rotationState(RotationState.ALL)
            .recipeType(GTPRecipeTypes.OVERWORLD_TELEPORT_RECIPE_TYPE)
            .appearanceBlock(() -> GTPBlocks.SIMPLE_OVERWORLD_PORTAL_FRAME.get())
            .pattern(definition-> FactoryBlockPattern.start()
                    .aisle( "XPX",
                            "X X",
                            "X X",
                            "XXX")
                    .where('X',blocks(GTPBlocks.SIMPLE_OVERWORLD_PORTAL_FRAME.get())
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setExactLimit(1)))
                    .where(' ', Predicates.air())
                    .where('P',Predicates.controller(blocks(definition.getBlock())))
                    .build()
            )
            .langValue("Simple Overworld Portal Controller")
            .hasBER(true)
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTPortal.id("block/simple_overworld_portal_frame"),
                    GTPortal.id("block/portal_controller_overlay"))
                    .andThen(b->b.addDynamicRenderer(()->new PortalBlockRenderer(Optional.of(GTPBlocks.OVERWORLD_PORTAL_BLOCK.get())))))
            .register();

    public static final MultiblockMachineDefinition SIMPLE_NETHER_PORTAL_CONTROLLER = GTPRegistries.REGISTRATE
            .multiblock("simple_nether_portal_controller",iMachineBlockEntity -> new SingleDimensionPortalControllerMachine(iMachineBlockEntity, Level.NETHER.location()))
            .rotationState(RotationState.ALL)
            .recipeType(GTPRecipeTypes.NETHER_TELEPORT_RECIPE_TYPE)
            .appearanceBlock(() -> GTPBlocks.SIMPLE_NETHER_PORTAL_FRAME.get())
            .pattern(definition-> FactoryBlockPattern.start()
                    .aisle( "XPX",
                            "X X",
                            "X X",
                            "XXX")
                    .where('X',blocks(GTPBlocks.SIMPLE_NETHER_PORTAL_FRAME.get())
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setExactLimit(1)))
                    .where(' ', Predicates.air())
                    .where('P',Predicates.controller(blocks(definition.getBlock())))
                    .build()
            )
            .langValue("Simple Nether Portal Controller")
            .hasBER(true)
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTPortal.id("block/simple_nether_portal_frame"),
                    GTPortal.id("block/portal_controller_overlay"))
                    .andThen(b->b.addDynamicRenderer(()->new PortalBlockRenderer(Optional.of(GTPBlocks.NETHER_PORTAL_BLOCK.get())))))
            .register();

    public static void init() {}
}
