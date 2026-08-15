package com.ironsword.gtportal.api.portal.teleporter;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.ironsword.gtportal.api.machine.feature.ITeleportMachine;
import com.ironsword.gtportal.common.data.GTPPoiTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class NewTeleporter implements ITeleporter {
    public static final Map<ResourceLocation, ResourceKey<PoiType>> MAP = new HashMap<>();

    protected final ServerLevel level;
    protected final Vec3 offset;

    public NewTeleporter(ServerLevel level, Vec3 offset){
        this.level = level;
        this.offset = offset;
    }

    @Override
    public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        PoiManager manager = destWorld.getPoiManager();
        int searchRadius = (int) (128 / destWorld.dimensionType().coordinateScale());

        /*
         * calculate the destination position
         * horizontally (X and Z), multiply the scale directly
         * vertically (Y), clamp entity's position to the logical range first, then multiply the scale
         * for example, if entity is at y = 200 in Nether, when calculating the destination position, use y = 128 instead of 200
         */
        int horizontalScale = (int) DimensionType.getTeleportationScale(this.level.dimensionType(), destWorld.dimensionType());
        int verticalScale = (int) (destWorld.getHeight()/this.level.getHeight());
        int height = Math.min(entity.blockPosition().getY() - this.level.getMinBuildHeight(), this.level.getLogicalHeight());
        BlockPos destination = destWorld.getWorldBorder().clampToBounds(
                entity.blockPosition().getX() * horizontalScale,
                height * verticalScale + destWorld.getMinBuildHeight(),
                entity.blockPosition().getZ() * horizontalScale);

        //load chunks
        manager.ensureLoadedAndValid(destWorld, destination, searchRadius);

        /*
         * 1. get all the pois of single or multi PCM in the square of searchRadius
         * 2. filter the pois within the world border
         * 3. sort the pois to list
         * 4. check each poi, if the machine can teleport, then return
         */
        List<PoiRecord> list = manager
                .getInSquare(holder -> holder.is(MAP.getOrDefault(destWorld.dimension().location(), null)) || holder.is(GTPPoiTypes.MULTI_PCM_POI.getKey()),
                        destination,
                        searchRadius,
                        PoiManager.Occupancy.ANY)
                .filter(record -> destWorld.getWorldBorder().isWithinBounds(record.getPos()))
                .sorted(Comparator.<PoiRecord>comparingDouble(record -> record.getPos().distSqr(destination)).thenComparingInt(record -> record.getPos().getY()))
                .toList();
        for(PoiRecord record: list){
            if(destWorld.getBlockEntity(record.getPos()) instanceof MetaMachineBlockEntity machineBlockEntity
                    && machineBlockEntity.getMetaMachine() instanceof ITeleportMachine teleportMachine
                    && teleportMachine.canTeleport()){
                return new PortalInfo(teleportMachine.applyOffset(offset),Vec3.ZERO,entity.getYRot(), entity.getXRot());
            }
        }

        return ITeleporter.super.getPortalInfo(entity, destWorld, defaultPortalInfo);
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        return ITeleporter.super.placeEntity(entity, currentWorld, destWorld, yaw, repositionEntity);
    }

    protected static PortalInfo makeTeleportMachinePortalInfo(Entity entity,BlockPos pos){
        return new PortalInfo(new Vec3(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5),Vec3.ZERO,entity.getYRot(), entity.getXRot());
    }
}
