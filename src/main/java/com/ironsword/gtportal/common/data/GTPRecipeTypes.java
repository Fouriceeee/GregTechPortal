package com.ironsword.gtportal.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class GTPRecipeTypes {

    public static GTRecipeType register(String name, String group, RecipeType<?>... proxyRecipes) {
        var recipeType = new GTRecipeType(GTCEu.id(name), group, proxyRecipes);
        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, recipeType.registryName, recipeType);
        GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER, recipeType.registryName, new GTRecipeSerializer());
        GTRegistries.RECIPE_TYPES.register(recipeType.registryName, recipeType);
        return recipeType;
    }


    public static final GTRecipeType DIMENSION_TELEPORT_RECIPES = register("dimension_teleport_recipes", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(0,0,0,0)
            .setProgressBar(GuiTextures.BLANK_TRANSPARENT, ProgressTexture.FillDirection.ALWAYS_FULL)
            .setIconSupplier(()-> GTPMachines.PORTAL_CONTROLLER.asStack())
            .setEUIO(IO.IN);
//
//    public static final GTRecipeType DIMENSION_DATA_RECIPES = register("dimension_data_recipes", GTRecipeTypes.MULTIBLOCK)
//            .setMaxIOSize(0,0,0,0)
//            .setEUIO(IO.IN)
//            .addCustomRecipeLogic();
//
//
//    private record ScannerRecipeEntry(@NotNull String id,
//                                      @NotNull ItemStack researchItem, FluidStack researchFluid, @NotNull ItemStack dataStack,
//                                      ResourceLocation dimension, int duration, EnergyStack EUt, int CWUt){
//
//    }

    public static void init() {

    }
}
