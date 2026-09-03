package com.ironsword.gtportal.api.portal.teleporter;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.ironsword.gtportal.api.machine.feature.ITeleportMachine;
import com.ironsword.gtportal.common.data.GTPPoiTypes;
import com.ironsword.gtportal.common.machine.multiblock.MultidimensionalPortalControllerMachine;
import com.mojang.datafixers.util.Pair;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EndTeleporter extends DefaultTeleporter{
    public EndTeleporter(ServerLevel level, Vec3 offset, @Nullable Vec3i coordinate) {
        super(level, offset, coordinate);
    }

    @Override
    public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        PoiManager manager = destWorld.getPoiManager();

        if(coordinate != null){
            manager.ensureLoadedAndValid(destWorld, coordinate, 16);
            BlockEntity blockEntity = destWorld.getBlockEntity(coordinate);
            if (blockEntity instanceof MetaMachineBlockEntity machineEntity && machineEntity.getMetaMachine() instanceof ITeleportMachine machine && machine.canTeleport()){
                return new PortalInfo(machine.applyOffset(offset),Vec3.ZERO,entity.getYRot(), entity.getXRot());
            }
        }

        int searchRadius = (int) (128 / destWorld.dimensionType().coordinateScale());

        double horizontalScale = DimensionType.getTeleportationScale(this.level.dimensionType(), destWorld.dimensionType());
        double verticalScale = (double) destWorld.getHeight() / this.level.getHeight();
        int height = Math.min(entity.blockPosition().getY() - this.level.getMinBuildHeight(), this.level.getLogicalHeight());
        BlockPos destination = destWorld.getWorldBorder().clampToBounds(
                entity.blockPosition().getX() * horizontalScale,
                height * verticalScale + destWorld.getMinBuildHeight(),
                entity.blockPosition().getZ() * horizontalScale);

        manager.ensureLoadedAndValid(destWorld, destination, searchRadius);

        ResourceKey<PoiType> poiType = MAP.getOrDefault(level.dimension().location(), null);
        if(poiType != null){
            List<PoiRecord> list = manager
                    .getInSquare(holder -> holder.is(poiType) || holder.is(GTPPoiTypes.MULTI_PCM_POI.getKey()),
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
        }

        //if no PCM is found, teleport to the default spawn point
        ServerLevel.makeObsidianPlatform(destWorld);
        return createPortalInfo(entity,ServerLevel.END_SPAWN_POINT);
    }
}
