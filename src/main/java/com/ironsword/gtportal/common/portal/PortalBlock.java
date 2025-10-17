package com.ironsword.gtportal.common.portal;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class PortalBlock extends Block {

    protected ResourceKey<Level> dimension;

    public PortalBlock(Properties pProperties, ResourceKey<Level> dimension) {
        super(pProperties);
        this.dimension = dimension;
    }
}
