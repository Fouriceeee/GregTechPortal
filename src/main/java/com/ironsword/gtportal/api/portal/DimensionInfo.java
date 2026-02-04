package com.ironsword.gtportal.api.portal;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.ironsword.gtportal.GTPortal;
import com.ironsword.gtportal.api.portal.teleporter.GTPTeleporter;
import com.ironsword.gtportal.api.portal.teleporter.TwilightTeleporter;
import lombok.Getter;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import twilightforest.init.TFBlocks;
import twilightforest.world.registration.TFGenerationSettings;

public enum DimensionInfo implements StringRepresentable {
    EMPTY(null,new ResourceLocation(GTPortal.MODID,"block/portals/empty_portal"),0),
    OVERWORLD(
            Level.OVERWORLD.location(),
            new ResourceLocation(GTPortal.MODID,"block/portals/overworld_portal"),
            0,
            ((entity, currWorld,destWorld,pos) -> entity.changeDimension(destWorld,new GTPTeleporter(currWorld,pos,Blocks.COBBLESTONE)))),
    NETHER(
            Level.NETHER.location(),
            new ResourceLocation(GTPortal.MODID,"block/portals/nether_portal"),
            0,
            ((entity, currWorld,destWorld,pos) -> entity.changeDimension(destWorld,new GTPTeleporter(currWorld,pos,Blocks.OBSIDIAN)))),
    END(
            Level.END.location(),
            new ResourceLocation(GTPortal.MODID,"block/portals/end_portal"),
            128L,
            ((entity, currWorld,destWorld,pos) -> entity.changeDimension(destWorld))),
    TWILIGHT(
            TFGenerationSettings.DIMENSION,
            new ResourceLocation("minecraft","block/nether_portal"),
            128L,
            ((entity, currWorld, destWorld, pos) -> entity.changeDimension(destWorld,new TwilightTeleporter(destWorld,pos,TFBlocks.ROOT_BLOCK.get(),entity)))),
    AETHER(
            AetherDimensions.AETHER_LEVEL.location(),
            new ResourceLocation(Aether.MODID,"block/miscellaneous/aether_portal"),
            128L,
            ((entity, currWorld,destWorld,pos) -> entity.changeDimension(destWorld,new GTPTeleporter(currWorld,pos, Blocks.GLOWSTONE))));

    @Getter
    private final ResourceLocation id;
    @Getter
    private final ResourceLocation texture;
    @Getter
    private final long teleportEnergy;
    @Getter
    private final Teleport teleportFunc;

    DimensionInfo(ResourceLocation id, ResourceLocation texture, long teleportEnergy) {
        this.id = id;
        this.texture = texture;
        this.teleportEnergy = teleportEnergy;
        this.teleportFunc = (player,currWorld,destWorld,pos) -> {};
    }

    DimensionInfo(ResourceLocation id, ResourceLocation texture, long teleportEnergy,Teleport teleportFunc){
        this.id = id;
        this.texture = texture;
        this.teleportEnergy = teleportEnergy;
        this.teleportFunc = teleportFunc;
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

    @FunctionalInterface
    public interface Teleport{
        void apply(Entity entity,ServerLevel currWorld, ServerLevel destWorld, Vec3i pos);
    }
}
