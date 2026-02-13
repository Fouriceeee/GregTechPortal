package com.ironsword.gtportal.integration.twilightforest;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.ironsword.gtportal.api.portal.teleporter.GTPTeleporter;
import com.ironsword.gtportal.api.portal.teleporter.TwilightTeleporter;
import com.ironsword.gtportal.common.block.TestPortalBlock;
import com.ironsword.gtportal.common.data.GTPBlocks;
import com.ironsword.gtportal.common.machine.multiblock.TestPortalMachine;
import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import twilightforest.init.TFBlocks;
import twilightforest.world.registration.TFGenerationSettings;

public class TwilightInit {


    public static void init(){
        TestPortalMachine.MAP.put(
                TFGenerationSettings.DIMENSION,
                Pair.of(
                        GTPBlocks.TEST_TWILIGHT_PORTAL_BLOCK.get(),
                        (entity, currWorld, destWorld, coordinate) ->
                                entity.changeDimension(destWorld,new TwilightTeleporter(currWorld,coordinate, TFBlocks.ROOT_BLOCK.get(),entity))
                ));
    }
}
