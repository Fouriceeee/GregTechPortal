package com.ironsword.gtportal.common.data;

import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.lowdragmc.lowdraglib.LDLib;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import twilightforest.world.registration.TFGenerationSettings;

import java.util.function.Consumer;

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

        registerSingleDimensionalTeleportRecipe("overworld",Blocks.GRASS_BLOCK,Level.OVERWORLD.location(),0,5*20,provider);
        registerSingleDimensionalTeleportRecipe("nether",Blocks.NETHERRACK,Level.NETHER.location(),0,5*20,provider);
        registerSingleDimensionalTeleportRecipe("end",Blocks.END_STONE,Level.END.location(),0,5*20,provider);


        if (LDLib.isModLoaded("aether")){
            registerMultidimensionalTeleportRecipe("aether",AetherDimensions.AETHER_LEVEL.location(),32,20*5,provider);
            registerSingleDimensionalTeleportRecipe("aether",Blocks.GLOWSTONE,AetherDimensions.AETHER_LEVEL.location(),0,5*20,provider);
        }

        if (LDLib.isModLoaded("twilightforest")){
            registerMultidimensionalTeleportRecipe("twilight",TFGenerationSettings.DIMENSION,32,20*5,provider);
            registerSingleDimensionalTeleportRecipe("twilight", Items.DIAMOND,TFGenerationSettings.DIMENSION,0,5*20,provider);
        }
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

    private static void registerSingleDimensionalTeleportRecipe(String id, ItemLike item,ResourceLocation dimension, long eut, int duration, Consumer<FinishedRecipe> provider){
        GTPRecipeTypes.SINIGLE_DIMENSIONAL_TELEPORT_RECIPE_TYPE.recipeBuilder(id)
                .notConsumable(item.asItem())
                .EUt(eut)
                .duration(duration)
                .addData("dimension",dimension.toString())
                .save(provider);
    }
}
