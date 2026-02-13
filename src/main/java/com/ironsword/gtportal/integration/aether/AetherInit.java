package com.ironsword.gtportal.integration.aether;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.ironsword.gtportal.api.portal.teleporter.GTPTeleporter;
import com.ironsword.gtportal.common.block.TestPortalBlock;
import com.ironsword.gtportal.common.data.GTPBlocks;
import com.ironsword.gtportal.common.machine.multiblock.TestPortalMachine;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

public class AetherInit {

    public static void init(){
        TestPortalMachine.MAP.put(
                AetherDimensions.AETHER_LEVEL.location(),
                Pair.of(
                        GTPBlocks.TEST_AETHER_PORTAL_BLOCK.get(),
                        (entity, currWorld, destWorld, coordinate) ->
                                entity.changeDimension(destWorld,new GTPTeleporter(currWorld,coordinate, Blocks.GLOWSTONE))
                ));
    }
}
