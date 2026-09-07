package com.ironsword.gtportal;

import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.ironsword.gtportal.api.portal.teleporter.DefaultTeleporter;
import com.ironsword.gtportal.api.portal.teleporter.GTPTeleporter;
import com.ironsword.gtportal.api.portal.teleporter.TwilightTeleporter;
import com.ironsword.gtportal.common.data.GTPBlocks;
import com.ironsword.gtportal.common.data.GTPPoiTypes;
import com.ironsword.gtportal.common.data.GTPRecipes;
import com.ironsword.gtportal.common.machine.multiblock.MultidimensionalPortalControllerMachine;
import com.ironsword.gtportal.common.registry.GTPRegistries;
import com.lowdragmc.lowdraglib.LDLib;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.level.block.Blocks;
import twilightforest.init.TFBlocks;
import twilightforest.world.registration.TFGenerationSettings;

import java.util.function.Consumer;

@GTAddon
public class GTPortalAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return GTPRegistries.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        if (LDLib.isModLoaded("aether")){
            MultidimensionalPortalControllerMachine.addDimensionInfo(
                    AetherDimensions.AETHER_LEVEL.location(),
                    MultidimensionalPortalControllerMachine.TeleportConsumer.DEFAULT,
                    GTPBlocks.AETHER_PORTAL_BLOCK);
            DefaultTeleporter.MAP.put(AetherDimensions.AETHER_LEVEL.location(),GTPPoiTypes.AETHER_PCM_POI.getKey());
        }

        if (LDLib.isModLoaded("twilightforest")){
            MultidimensionalPortalControllerMachine.addDimensionInfo(
                    TFGenerationSettings.DIMENSION,
                    (entity,destWorld, currLevel, offset,coordinate,sourceMachine) -> entity.changeDimension(destWorld, new TwilightTeleporter(currLevel, offset, coordinate, sourceMachine)),
                    GTPBlocks.TWILIGHT_PORTAL_BLOCK
            );
            DefaultTeleporter.MAP.put(TFGenerationSettings.DIMENSION, GTPPoiTypes.TWILIGHT_PCM_POI.getKey());
        }
    }



    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        GTPRecipes.init(provider);
    }



    @Override
    public String addonModId() {
        return GTPortal.MODID;
    }
}
