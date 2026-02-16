package com.ironsword.gtportal.common.data;

import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.lowdragmc.lowdraglib.LDLib;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import org.checkerframework.checker.units.qual.A;
import twilightforest.world.registration.TFGenerationSettings;

import java.util.Map;
import java.util.function.Consumer;

import static com.ironsword.gtportal.common.item.component.DimensionDataComponent.putDimensionNbt;

public class GTPRecipes {
//    public static void createDefaultScannerRecipe(@NotNull String researchId,
//                                                  ItemStack researchItem, FluidStack researchFluid,
//                                                  @NotNull ItemStack dataItem, @NotNull DimensionInfo info,
//                                                  int duration, long eu,Consumer<FinishedRecipe> provider){
//
//        CompoundTag tag = dataItem.getOrCreateTag();
//        DimensionData data = new DimensionData(info,null);
//        tag.put("dim_data",data.toNbt());
//        var builder = GTRecipeTypes.SCANNER_RECIPES.recipeBuilder(researchId)
//                .inputItems(dataItem.getItem());
//
//        if (researchItem != null) builder.inputItems(researchItem);
//        if (researchFluid != null) builder.inputFluids(researchFluid);
//
//        builder.outputItems(dataItem)
//                .duration(duration)
//                .EUt(eu)
//                .researchScan(true)
//                .save(provider);
//    }

    public static void init(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapelessRecipe(provider,"dimension_data_stick_clear_data",GTPItems.DIMENSION_DATA_STICK.asStack(),GTPItems.DIMENSION_DATA_STICK.asStack());

        teleportRecipes(provider);
    }

    private static void teleportRecipes(Consumer<FinishedRecipe> provider) {

        CompoundTag
                OVERWORLD_TAG = new CompoundTag(),
                NETHER_TAG = new CompoundTag(),
                END_TAG = new CompoundTag();
        OVERWORLD_TAG.putString("dimension",Level.OVERWORLD.location().toString());
        NETHER_TAG.putString("dimension",Level.NETHER.location().toString());
        END_TAG.putString("dimension",Level.END.location().toString());

        GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder("overworld")
                .notConsumable(PartialNBTIngredient.of(OVERWORLD_TAG,GTPItems.DIMENSION_DATA_STICK))
                .EUt(0)
                .duration(20*5)
                .addData("dimension",Level.OVERWORLD.location().getPath())
                .save(provider);

        GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder("overworld_with_coordinate")
                .notConsumable(PartialNBTIngredient.of(OVERWORLD_TAG,GTPItems.DIMENSION_DATA_RECORDER))
                .EUt(0)
                .duration(20*5)
                .addData("dimension",Level.OVERWORLD.location().getPath())
                .save(provider);

        GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder("nether")
                .notConsumable(PartialNBTIngredient.of(NETHER_TAG,GTPItems.DIMENSION_DATA_STICK))
                .EUt(0)
                .duration(20*5)
                .addData("dimension",Level.NETHER.location().getPath())
                .save(provider);

        GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder("nether_with_coordinate")
                .notConsumable(PartialNBTIngredient.of(NETHER_TAG,GTPItems.DIMENSION_DATA_RECORDER))
                .EUt(0)
                .duration(20*5)
                .addData("dimension",Level.NETHER.location().getPath())
                .save(provider);

        GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder("end")
                .notConsumable(PartialNBTIngredient.of(END_TAG,GTPItems.DIMENSION_DATA_STICK))
                .EUt(32)
                .duration(20*5)
                .addData("dimension",Level.END.location().getPath())
                .save(provider);

        if (LDLib.isModLoaded("aether")){
            CompoundTag AETHER_TAG = new CompoundTag();
            AETHER_TAG.putString("dimension",AetherDimensions.AETHER_LEVEL.location().toString());

            GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder("aether")
                    .notConsumable(PartialNBTIngredient.of(AETHER_TAG,GTPItems.DIMENSION_DATA_STICK))
                    .EUt(32)
                    .duration(20*5)
                    .addData("dimension",AetherDimensions.AETHER_LEVEL.location().getPath())
                    .save(provider);
        }

        if (LDLib.isModLoaded("twilightforest")){
            CompoundTag TWILIGHT_TAG = new CompoundTag();
            TWILIGHT_TAG.putString("dimension",TFGenerationSettings.DIMENSION.toString());

            GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder("twilight")
                    .notConsumable(PartialNBTIngredient.of(TWILIGHT_TAG,GTPItems.DIMENSION_DATA_STICK))
                    .EUt(32)
                    .duration(20*5)
                    .addData("dimension",TFGenerationSettings.DIMENSION.getPath())
                    .save(provider);
        }






//        GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder("overworld")
//                .notConsumable(StrictNBTIngredient.of(putDimensionNbt(GTPItems.DIMENSION_DATA_STICK.asStack(),Level.OVERWORLD.location())))
//                .EUt(0)
//                .duration(20*5)
//                .addData("dimension",Level.OVERWORLD.location().toString())
//                .save(provider);
//
//        GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder("nether")
//                .notConsumable(StrictNBTIngredient.of(putDimensionNbt(GTPItems.DIMENSION_DATA_STICK.asStack(),Level.NETHER.location())))
//                .EUt(0)
//                .duration(20*5)
//                .addData("dimension",Level.NETHER.location().toString())
//                .save(provider);
//
//        GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder("end")
//                .notConsumable(StrictNBTIngredient.of(putDimensionNbt(GTPItems.DIMENSION_DATA_STICK.asStack(),Level.END.location())))
//                .EUt(32)
//                .duration(20*5)
//                .addData("dimension",Level.END.location().toString())
//                .save(provider);
//
//        GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder("aether")
//                .notConsumable(StrictNBTIngredient.of(putDimensionNbt(GTPItems.DIMENSION_DATA_STICK.asStack(), AetherDimensions.AETHER_LEVEL.location())))
//                .EUt(32)
//                .duration(20*5)
//                .addData("dimension",AetherDimensions.AETHER_LEVEL.location().toString())
//                .save(provider);
//
//        GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder("twilight")
//                .notConsumable(StrictNBTIngredient.of(putDimensionNbt(GTPItems.DIMENSION_DATA_STICK.asStack(), TFGenerationSettings.DIMENSION)))
//                .EUt(32)
//                .duration(20*5)
//                .addData("dimension",AetherDimensions.AETHER_LEVEL.location().toString())
//                .save(provider);



        GTPRecipeTypes.OVERWORLD_TELEPORT_RECIPE_TYPE.recipeBuilder("overworld")
                .notConsumable(Blocks.GRASS_BLOCK.asItem())
                .EUt(0)
                .duration(20*5)
                .save(provider);

        GTPRecipeTypes.NETHER_TELEPORT_RECIPE_TYPE.recipeBuilder("nether")
                .notConsumable(Blocks.NETHERRACK.asItem())
                .EUt(0)
                .duration(20*5)
                .save(provider);
    }

    private static void registerMultidimensionalTeleportRecipe(){

    }
}
