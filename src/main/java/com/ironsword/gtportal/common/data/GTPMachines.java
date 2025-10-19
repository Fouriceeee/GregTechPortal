package com.ironsword.gtportal.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.client.renderer.block.FluidBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderHelper;
import com.gregtechceu.gtceu.client.renderer.machine.impl.FluidAreaRender;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtportal.GTPortal;
import com.ironsword.gtportal.common.machine.multiblock.PortalControllerMachine;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.ironsword.gtportal.common.registry.GTPRegistries;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import net.minecraft.world.level.material.Fluids;

import java.util.Optional;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_COKE_BRICKS;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;


public class GTPMachines {
    static {
        GTPRegistries.REGISTRATE.creativeModeTab(()-> GTPCreativeModeTabs.GTP_TAB);
    }



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
                        .where('X',blocks(Blocks.OBSIDIAN))
                        .where(' ', Predicates.any())
                        .where('P',Predicates.controller(blocks(definition.getBlock())))
                        .build()
            )
            .langValue("Portal Controller")
            .hasBER(true)
//            .workableCasingModel(GTCEu.id("block/casings/gcym/atomic_casing"),
//                    GTPortal.id("block/test"))
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTCEu.id("block/casings/gcym/atomic_casing"),
                    GTPortal.id("block/test"))
                    .andThen(b->b.addDynamicRenderer(()->
                            DynamicRenderHelper.makeFluidAreaRender(FluidBlockRenderer.Builder.create()
                                .setForcedLight(LightTexture.FULL_BRIGHT)
                                .getRenderer(), Optional.of(Fluids.WATER.getSource()), FluidAreaRender.DEFAULT_FACES)
                    )))
            .register();


    public static void init() {}
}
