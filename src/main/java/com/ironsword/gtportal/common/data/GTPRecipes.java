package com.ironsword.gtportal.common.data;

import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.lowdragmc.lowdraglib.LDLib;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
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
        registerMultidimensionalTeleportRecipe("overworld",Level.OVERWORLD.location(),0,5*20,provider);
        registerMultidimensionalTeleportRecipe("nether",Level.NETHER.location(),0,5*20,provider);
        registerMultidimensionalTeleportRecipe("end",Level.END.location(),32,5*20,provider);

        if (LDLib.isModLoaded("aether")){
            registerMultidimensionalTeleportRecipe("aether",AetherDimensions.AETHER_LEVEL.location(),32,20*5,provider);
        }

        if (LDLib.isModLoaded("twilightforest")){
            registerMultidimensionalTeleportRecipe("twilight",TFGenerationSettings.DIMENSION,32,20*5,provider);
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

    private static void registerMultidimensionalTeleportRecipe(String id, ResourceLocation dimension, long eut, int duration, Consumer<FinishedRecipe> provider){
        CompoundTag tag = new CompoundTag();
        tag.putString("dimension",dimension.toString());

        GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder(id+"_data_stick")
                .notConsumable(PartialNBTIngredient.of(tag,GTPItems.DIMENSION_DATA_STICK))
                .EUt(eut)
                .duration(duration)
                .addData("dimension",dimension.getPath())
                .save(provider);

        GTPRecipeTypes.MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder(id+"_data_recorder")
                .notConsumable(PartialNBTIngredient.of(tag,GTPItems.DIMENSION_DATA_RECORDER))
                .EUt(eut)
                .duration(duration)
                .addData("dimension",dimension.getPath())
                .save(provider);

    }
}
