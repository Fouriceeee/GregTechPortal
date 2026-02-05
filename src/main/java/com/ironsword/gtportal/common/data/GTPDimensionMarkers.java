package com.ironsword.gtportal.common.data;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.item.AetherItems;
import com.gregtechceu.gtceu.api.data.DimensionMarker;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.ironsword.gtportal.api.portal.DimensionInfo;
import net.minecraft.resources.ResourceLocation;
import twilightforest.init.TFBlocks;

import static com.gregtechceu.gtceu.common.data.GTDimensionMarkers.createAndRegister;
import static com.gregtechceu.gtceu.common.registry.GTRegistration.REGISTRATE;

public class GTPDimensionMarkers {

    static {
        REGISTRATE.creativeModeTab(() -> null);
    }

    public static final DimensionMarker AETHER = createAndRegister(
            DimensionInfo.AETHER.getId(),
            0,
            ()-> AetherItems.AETHER_PORTAL_FRAME.get(),
            DimensionInfo.AETHER.getTranslateKey());
    public static final DimensionMarker TWILIGHT = createAndRegister(
            DimensionInfo.TWILIGHT.getId(),
            0,
            ()-> TFBlocks.TWILIGHT_PORTAL_MINIATURE_STRUCTURE.get(),
            DimensionInfo.TWILIGHT.getTranslateKey());


    public static void init(){}
}
