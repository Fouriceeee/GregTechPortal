package com.ironsword.gtportal.api.portal;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.ironsword.gtportal.GTPortal;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import twilightforest.world.registration.TFGenerationSettings;

public enum DimensionInfo implements StringRepresentable {
    EMPTY(null,new ResourceLocation(GTPortal.MODID,"block/portals/empty_portal"),0),
    OVERWORLD(Level.OVERWORLD.location(),new ResourceLocation(GTPortal.MODID,"block/portals/overworld_portal"),0),
    NETHER(Level.NETHER.location(),new ResourceLocation(GTPortal.MODID,"block/portals/nether_portal"),0),
    END(Level.END.location(),new ResourceLocation(GTPortal.MODID,"block/portals/end_portal"),128L),
    TWILIGHT(TFGenerationSettings.DIMENSION,new ResourceLocation("minecraft","block/nether_portal"),128L),
    AETHER(AetherDimensions.AETHER_LEVEL.location(),new ResourceLocation(Aether.MODID,"block/miscellaneous/aether_portal"),128L);

    @Getter
    private final ResourceLocation id;
    @Getter
    private final ResourceLocation texture;
    @Getter
    private final long teleportEnergy;

    DimensionInfo(ResourceLocation id, ResourceLocation texture, long teleportEnergy) {
        this.id = id;
        this.texture = texture;
        this.teleportEnergy = teleportEnergy;
    }

    public static DimensionInfo byDimension(ResourceLocation id) {
        for (DimensionInfo i:DimensionInfo.values()){
            if (id.equals(i.id)) return i;
        }
        return EMPTY;
    }

    public static DimensionInfo byName(String name) {
        for (DimensionInfo i:DimensionInfo.values()){
            if (name.equals(i.getSerializedName())) return i;
        }
        return EMPTY;
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }

    public String getTranslateKey(){
        return "gtportal.dimension." + getSerializedName();
    }
}
