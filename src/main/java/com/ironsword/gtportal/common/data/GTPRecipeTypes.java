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
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
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

    public static final GTRecipeType DIMENSION_TELEPORT_RECIPES = GTRecipeTypes.register("dimension_teleport_recipes", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(0,0,0,0)
            .setProgressBar(GuiTextures.BLANK_TRANSPARENT, ProgressTexture.FillDirection.ALWAYS_FULL)
            .setIconSupplier(()-> GTPMachines.PORTAL_CONTROLLER.asStack())
            .setEUIO(IO.IN);

    public static void init() {

    }
}
