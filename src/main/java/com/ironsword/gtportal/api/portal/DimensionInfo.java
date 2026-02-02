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
    EMPTY("empty",null,new ResourceLocation(GTPortal.MODID,"block/portals/empty_portal"),0),
    OVERWORLD("overworld", Level.OVERWORLD.location(),new ResourceLocation(GTPortal.MODID,"block/portals/overworld_portal"),0),
    NETHER("nether",Level.NETHER.location(),new ResourceLocation(GTPortal.MODID,"block/portals/nether_portal"),0),
    END("end",Level.END.location(),new ResourceLocation(GTPortal.MODID,"block/portals/end_portal"),128L),
    TWILIGHT("twilight", TFGenerationSettings.DIMENSION,new ResourceLocation("minecraft","block/nether_portal"),128L),
    AETHER("aether", AetherDimensions.AETHER_LEVEL.location(),new ResourceLocation(Aether.MODID,"block/miscellaneous/aether_portal"),128L);

    private final String name;
    @Getter
    private final ResourceLocation id;
    @Getter
    private final ResourceLocation texture;
    @Getter
    private final long teleportEnergy;

    DimensionInfo(String name, ResourceLocation id, ResourceLocation texture, long teleportEnergy) {
        this.name = name;
        this.id = id;
        this.texture = texture;
        this.teleportEnergy = teleportEnergy;
    }

    public static DimensionInfo byId(ResourceLocation id) {
        for (DimensionInfo i:DimensionInfo.values()){
            if (id.equals(i.id)) return i;
        }
        return EMPTY;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
