package com.ironsword.gtportal.common.data;

import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.ironsword.gtportal.api.portal.DimensionData;
import com.ironsword.gtportal.api.portal.DimensionInfo;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import twilightforest.init.TFItems;
import twilightforest.world.registration.TFGenerationSettings;

import java.util.function.Consumer;

public class GTPRecipes {
    public static void createDefaultScannerRecipe(@NotNull String researchId,
                                                  ItemStack researchItem, FluidStack researchFluid,
                                                  @NotNull ItemStack dataItem, @NotNull DimensionInfo info,
                                                  int duration, long eu,Consumer<FinishedRecipe> provider){

        CompoundTag tag = dataItem.getOrCreateTag();
        DimensionData data = new DimensionData(info,null);
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

        VanillaRecipeHelper.addShapelessRecipe(provider,"dim_data_stick",GTPItems.DIM_DATA_STICK.asStack(),GTPItems.DIM_DATA_STICK.asStack());
        VanillaRecipeHelper.addShapelessRecipe(provider,"dim_data_recorder",GTPItems.DIM_DATA_RECORDER.asStack(),GTPItems.DIM_DATA_RECORDER.asStack());

        testRecipes(provider);
    }

    private static void testRecipes(Consumer<FinishedRecipe> provider) {
        GTPRecipeTypes.TEST_RECIPE_TYPE.recipeBuilder("overworld")
                .inputItems(GTPItems.DIM_DATA_STICK.asStack())
                .EUt(32)
                .duration(20)
                .addData("dimension",DimensionInfo.OVERWORLD.getSerializedName())
                .save(provider);
        GTPRecipeTypes.TEST_RECIPE_TYPE.recipeBuilder("nether")
                .inputItems(GTPItems.DIM_DATA_STICK.asStack())
                .EUt(64)
                .duration(20)
                .addData("dimension",DimensionInfo.NETHER.getSerializedName())
                .save(provider);
    }

    private static void scannerRecipes(Consumer<FinishedRecipe> provider){
        createDefaultScannerRecipe("overworld",
                Items.STONE.getDefaultInstance(),null,
                GTPItems.DIM_DATA_STICK.asStack(), DimensionInfo.OVERWORLD,
                20,8,provider);

        createDefaultScannerRecipe("nether",
                Items.NETHERRACK.getDefaultInstance(),null,
                GTPItems.DIM_DATA_STICK.asStack(), DimensionInfo.NETHER,
                20,32,provider);

        createDefaultScannerRecipe("end",
                Items.END_STONE.getDefaultInstance(),null,
                GTPItems.DIM_DATA_STICK.asStack(), DimensionInfo.END,
                40,128,provider);

        createDefaultScannerRecipe("twilight_forest",
                TFItems.TORCHBERRIES.get().getDefaultInstance(),null,
                GTPItems.DIM_DATA_STICK.asStack(), DimensionInfo.TWILIGHT,
                20,512,provider);

        createDefaultScannerRecipe("aether",
                Items.GLOWSTONE.getDefaultInstance(),null,
                GTPItems.DIM_DATA_STICK.asStack(), DimensionInfo.AETHER,
                20,2048,provider);

    }

    private static void teleportRecipes(Consumer<FinishedRecipe> provider){
        for (var info:DimensionInfo.values()){
            if (info.equals(DimensionInfo.EMPTY)) continue;
            GTPRecipeTypes.DIMENSION_TELEPORT_RECIPES.recipeBuilder(info.name())
                    .duration(20)
                    .EUt(info.getTeleportEnergy())
                    .dimension(info.getId())
                    .save(provider);
        }

    }

}
