package com.ironsword.gtportal.common.block.property;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public enum Dimension implements StringRepresentable {
    ;

    private final ResourceLocation dimension;

    Dimension(ResourceLocation dimension) {
        this.dimension = dimension;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
