package com.ironsword.gtportal.utils;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.ironsword.gtportal.client.renderer.PortalBlockRenderer;
import com.ironsword.gtportal.common.data.GTPRecipeTypes;
import com.ironsword.gtportal.common.data.GTPTags;
import com.ironsword.gtportal.common.machine.multiblock.SingleDimensionPortalControllerMachine;
import com.ironsword.gtportal.common.registry.GTPRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;

public class MachineUtils {
    public static MultiblockMachineDefinition registerSingleDimensionPortalController(
            String id,
            String name,
            ResourceLocation dimension,
            Supplier<? extends Block> frame,
            ResourceLocation texture,
            ResourceLocation overlayTexture,
            Supplier<? extends Block> portal,
            @Nullable Component tooltips){
        return registerSingleDimensionPortalController(
                GTPRegistries.REGISTRATE,
                id,
                name,
                dimension,
                frame,
                texture,
                overlayTexture,
                portal,
                tooltips);
    }
    public static MultiblockMachineDefinition registerSingleDimensionPortalController(
            GTRegistrate registrate,
            String id,
            String name,
            ResourceLocation dimension,
            Supplier<? extends Block> frame,
            ResourceLocation texture,
            ResourceLocation overlayTexture,
            Supplier<? extends Block> portal,
            @Nullable Component tooltips){

        return registrate
                .multiblock(id,iMachineBlockEntity -> new SingleDimensionPortalControllerMachine(iMachineBlockEntity,dimension))
                .rotationState(RotationState.ALL)
                .recipeType(GTPRecipeTypes.SINIGLE_DIMENSIONAL_TELEPORT_RECIPE_TYPE)
                .recipeModifier(RecipeModifier.NO_MODIFIER)
                .appearanceBlock(frame)
                .pattern(definition-> FactoryBlockPattern.start()
                        .aisle( "XXPXX",
                                "X   X",
                                "X   X",
                                "X   X",
                                "XXXXX")
                        .where('X',blocks(frame.get())
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setExactLimit(1))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(1)))
                        .where(' ', Predicates.air()
                                .or(Predicates.blockTag(GTPTags.PORTAL_BLOCKS)))
                        .where('P',Predicates.controller(blocks(definition.getBlock())))
                        .build()
                )
                .langValue(name)
                .hasBER(true)
                .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
                .model(createWorkableCasingMachineModel(texture, overlayTexture)
                        .andThen(b->b.addDynamicRenderer(()->new PortalBlockRenderer(Optional.of(portal.get())))))
                .tooltips(tooltips)
                .register();
    }
}
