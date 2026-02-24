package com.ironsword.gtportal;

import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.ironsword.gtportal.api.portal.teleporter.GTPTeleporter;
import com.ironsword.gtportal.api.portal.teleporter.TwilightTeleporter;
import com.ironsword.gtportal.common.data.GTPBlocks;
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
            MultidimensionalPortalControllerMachine.MAP.put(
                    AetherDimensions.AETHER_LEVEL.location(),
                    Pair.of(
                            GTPBlocks.AETHER_PORTAL_BLOCK::get,
                            (entity, currWorld, destWorld, contrllerPos,coordinate) ->
                                    entity.changeDimension(destWorld,new GTPTeleporter(currWorld,contrllerPos,coordinate, Blocks.GLOWSTONE))
                    ));
        }

        if (LDLib.isModLoaded("twilightforest")){
            MultidimensionalPortalControllerMachine.MAP.put(
                    TFGenerationSettings.DIMENSION,
                    Pair.of(
                            GTPBlocks.TWILIGHT_PORTAL_BLOCK::get,
                            (entity, currWorld, destWorld,contrllerPos, coordinate) ->
                                    entity.changeDimension(destWorld,new TwilightTeleporter(currWorld,contrllerPos,coordinate, TFBlocks.ROOT_BLOCK.get(),entity))
                    ));
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
