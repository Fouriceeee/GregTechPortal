package com.ironsword.gtportal.common.data;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtportal.GTPortal;
import com.ironsword.gtportal.client.renderer.PortalBlockRenderer;
import com.ironsword.gtportal.common.machine.multiblock.MultidimensionalPortalControllerMachine;
import com.ironsword.gtportal.common.machine.multiblock.SingleDimensionPortalControllerMachine;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.ironsword.gtportal.common.registry.GTPRegistries;
import com.ironsword.gtportal.utils.MachineUtils;
import com.lowdragmc.lowdraglib.LDLib;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import twilightforest.world.registration.TFGenerationSettings;

import java.util.Optional;
import java.util.function.Supplier;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;


public class GTPMachines {
    static {
        GTPRegistries.REGISTRATE.creativeModeTab(()-> GTPCreativeModeTabs.GTP_TAB);
    }

    public static final MultiblockMachineDefinition MULTIDIMENSIONAL_PORTAL_CONTROLLER = GTPRegistries.REGISTRATE
            .multiblock("multidimensional_portal_controller", MultidimensionalPortalControllerMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE)
            .recipeModifier(RecipeModifier.NO_MODIFIER)
            .appearanceBlock(()->GTPBlocks.MULTIDIMENSIONAL_PORTAL_FRAME.get())
            .pattern(definition-> FactoryBlockPattern.start()
                    .aisle( "XXPXX",
                            "X   X",
                            "X   X",
                            "X   X",
                            "XXXXX")
                    .where('X',blocks(GTPBlocks.MULTIDIMENSIONAL_PORTAL_FRAME.get())
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setExactLimit(1)))
                    .where(' ', Predicates.air()
                            .or(Predicates.blockTag(GTPTags.PORTAL_BLOCKS)))
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

    public static final MultiblockMachineDefinition SIMPLE_OVERWORLD_PORTAL_CONTROLLER =
    MachineUtils.registerSingleDimensionPortalController(
                    "simple_overworld_portal_controller",
                    "Simple Overworld Portal Controller",
                    Level.OVERWORLD.location(),
                    GTPBlocks.SIMPLE_OVERWORLD_PORTAL_FRAME,
                    GTPortal.id("block/simple_overworld_portal_frame"),
                    GTPortal.id("block/portal_controller_overlay"),
                    GTPBlocks.OVERWORLD_PORTAL_BLOCK,
                    null
            );

    public static final MultiblockMachineDefinition SIMPLE_NETHER_PORTAL_CONTROLLER =
    MachineUtils.registerSingleDimensionPortalController(
            "simple_nether_portal_controller",
            "Simple Nether Portal Controller",
            Level.NETHER.location(),
            GTPBlocks.SIMPLE_NETHER_PORTAL_FRAME,
            GTPortal.id("block/simple_nether_portal_frame"),
            GTPortal.id("block/portal_controller_overlay"),
            GTPBlocks.NETHER_PORTAL_BLOCK,
            Component.translatable("gtportal.tooltip.machine.simple_nether_portal_controller")
    );

    public static final MultiblockMachineDefinition SIMPLE_END_PORTAL_CONTROLLER = MachineUtils.registerSingleDimensionPortalController(
            "simple_end_portal_controller",
            "Simple End Portal Controller",
            Level.END.location(),
            GTPBlocks.SIMPLE_END_PORTAL_FRAME,
            GTPortal.id("block/end"),
            GTPortal.id("block/portal_controller_overlay"),
            GTPBlocks.END_PORTAL_BLOCK,
            null
    );

    public static MultiblockMachineDefinition SIMPLE_AETHER_PORTAL_CONTROLLER;
    public static MultiblockMachineDefinition SIMPLE_TWILIGHT_PORTAL_CONTROLLER;

    public static void init() {

        if (LDLib.isModLoaded("aether")){
            SIMPLE_AETHER_PORTAL_CONTROLLER = MachineUtils.registerSingleDimensionPortalController(
                    "simple_aether_portal_controller",
                    "Simple Aether Portal Controller",
                    AetherDimensions.AETHER_LEVEL.location(),
                    GTPBlocks.SIMPLE_AETHER_PORTAL_FRAME,
                    GTPortal.id("block/aether"),
                    GTPortal.id("block/portal_controller_overlay"),
                    GTPBlocks.AETHER_PORTAL_BLOCK,
                    null
            );
        }
        if (LDLib.isModLoaded("twilightforest")){
            SIMPLE_TWILIGHT_PORTAL_CONTROLLER = MachineUtils.registerSingleDimensionPortalController(
                    "simple_twilight_portal_controller",
                    "Simple Twilight Portal Controller",
                    TFGenerationSettings.DIMENSION,
                    GTPBlocks.SIMPLE_TWILIGHT_PORTAL_FRAME,
                    GTPortal.id("block/twilight"),
                    GTPortal.id("block/portal_controller_overlay"),
                    GTPBlocks.TWILIGHT_PORTAL_BLOCK,
                    null
            );
        }
    }
}
