package com.ironsword.gtportal.common.data;

import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.world.LevelUtil;
import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtportal.api.portal.DimensionData;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import twilightforest.init.TFDimensionSettings;
import twilightforest.init.TFItems;
import twilightforest.world.registration.TFGenerationSettings;

import java.util.function.Consumer;

public class GTPRecipes {

    public static ResourceLocation NETHER = Level.NETHER.location();
    public static ResourceLocation END = Level.END.location();
    public static ResourceLocation TWILIGHT = TFGenerationSettings.DIMENSION;
    public static ResourceLocation AETHER = AetherDimensions.AETHER_LEVEL.location();

    public static void createDefaultScannerRecipe(@NotNull String researchId,
                                                  ItemStack researchItem, FluidStack researchFluid,
                                                  @NotNull ItemStack dataItem, @NotNull ResourceLocation dimension,
                                                  int duration, long eu,Consumer<FinishedRecipe> provider){

        CompoundTag tag = dataItem.getOrCreateTag();
        DimensionData data = new DimensionData(dimension,null);
        tag.put("dim_data",data.toNbt());
        var builder = GTRecipeTypes.SCANNER_RECIPES.recipeBuilder(researchId)
                .inputItems(dataItem.getItem());

        if (researchItem != null) builder.inputItems(researchItem);
        if (researchFluid != null) builder.inputFluids(researchFluid);

        builder.outputItems(dataItem)
                .duration(duration)
                .EUt(eu)
                .researchScan(true)
                .save(provider);
    }

    public static void init(Consumer<FinishedRecipe> provider) {
        scannerRecipes(provider);
        teleportRecipes(provider);
    }

    private static void scannerRecipes(Consumer<FinishedRecipe> provider){
        createDefaultScannerRecipe("nether",
                Items.NETHERRACK.getDefaultInstance(),null,
                GTPItems.DIM_RECORDER.asStack(), NETHER,
                20,32,provider);

        createDefaultScannerRecipe("end",
                Items.END_STONE.getDefaultInstance(),null,
                GTPItems.DIM_RECORDER.asStack(), END,
                40,128,provider);

        createDefaultScannerRecipe("twilight_forest",
                TFItems.TORCHBERRIES.get().getDefaultInstance(),null,
                GTPItems.DIM_RECORDER.asStack(), TWILIGHT,
                20,512,provider);

        createDefaultScannerRecipe("aether",
                Items.GLOWSTONE.getDefaultInstance(),null,
                GTPItems.DIM_RECORDER.asStack(), AETHER,
                20,2048,provider);
    }

    private static void teleportRecipes(Consumer<FinishedRecipe> provider){
        GTPRecipeTypes.DIMENSION_TELEPORT_RECIPES.recipeBuilder("nether_portal")
                .dimension(NETHER)
                .EUt(32)
                .save(provider);

        GTPRecipeTypes.DIMENSION_TELEPORT_RECIPES.recipeBuilder("end_portal")
                .dimension(END)
                .EUt(128)
                .save(provider);
    }

}
