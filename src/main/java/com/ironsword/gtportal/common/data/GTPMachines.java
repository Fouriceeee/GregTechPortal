package com.ironsword.gtportal.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtportal.common.machine.multiblock.electric.PortalControllerMachine;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.ironsword.gtportal.common.registry.GTPRegistries;
import net.minecraft.world.level.block.Blocks;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

public class GTPMachines {
    static {
        GTPRegistries.REGISTRATE.creativeModeTab(()-> GTPCreativeModeTabs.GTP_TAB);
    }



//    public static final MultiblockMachineDefinition PORTAL_CONTROLLER = GTPRegistries.REGISTRATE
//            .multiblock("portal_controller", WorkableElectricMultiblockMachine::new)
//            .rotationState(RotationState.NON_Y_AXIS)
//            .recipeType(GTRecipeTypes.COKE_OVEN_RECIPES)
//            .appearanceBlock(()->Blocks.OBSIDIAN)
//            .pattern(definition-> FactoryBlockPattern.start()
//                        .aisle("XXPXX","XOOOX","XOOOX","XOOOX","XXXXX")
//                        .where('X',blocks(Blocks.OBSIDIAN))
//                        .where('O', Predicates.air())
//                        .where('P',Predicates.controller(blocks(definition.getBlock())))
//                        .build()
//            )
//            .workableCasingModel(GTCEu.id("block/casings/solid/machine_coke_bricks"),
//                    GTCEu.id("block/multiblock/coke_oven"))
//            .register();

    public static void init() {}
}
