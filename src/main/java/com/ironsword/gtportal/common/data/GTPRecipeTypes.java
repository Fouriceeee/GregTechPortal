package com.ironsword.gtportal.common.data;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class GTPRecipeTypes {


    public static final GTRecipeType MULTIDIMENSIONAL_TELEPORT_RECIPE_TYPE = GTRecipeTypes.register("multidimensional_teleport_recipe",GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(1,0,0,0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setIconSupplier(()->GTPItems.DIMENSION_DATA_STICK.asStack())
            .setEUIO(IO.IN)
            .addDataInfo(data-> LocalizationUtils.format("gtportal.machine.tooltip.dimension") + ": " + LocalizationUtils.format("gtportal.dimension."+data.getString("dimension")));

    public static final GTRecipeType OVERWORLD_TELEPORT_RECIPE_TYPE = GTRecipeTypes.register("overworld_teleport_recipe",GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(1,0,0,0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setIconSupplier(()-> Blocks.GRASS_BLOCK.asItem().getDefaultInstance())
            .setEUIO(IO.IN);

    public static final GTRecipeType NETHER_TELEPORT_RECIPE_TYPE = GTRecipeTypes.register("nether_teleport_recipe",GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(1,0,0,0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setIconSupplier(()-> Blocks.NETHERRACK.asItem().getDefaultInstance())
            .setEUIO(IO.IN);

    public static void init() {

    }
}
