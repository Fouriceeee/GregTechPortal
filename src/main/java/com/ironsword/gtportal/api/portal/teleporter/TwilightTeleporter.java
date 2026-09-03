package com.ironsword.gtportal.api.portal.teleporter;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.ironsword.gtportal.api.machine.feature.ITeleportMachine;
import com.ironsword.gtportal.common.data.GTPPoiTypes;
import com.ironsword.gtportal.mixin.accessor.TFTeleportAccessor;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import twilightforest.world.registration.TFGenerationSettings;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;


public class TwilightTeleporter extends DefaultTeleporter{
    public TwilightTeleporter(ServerLevel level, Vec3 offset, @Nullable Vec3i coordinate) {
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

        //no pcm is found, use TF API to find a safe position
        PortalInfo info = TFTeleportAccessor.callMoveToSafeCoords(destWorld,entity,destination);
        TFTeleportAccessor.callLoadSurroundingArea(destWorld,info.pos);

        BlockPos spot = TFTeleportAccessor.callFindPortalCoords(destWorld,info.pos,
                (blockPos)-> TFTeleportAccessor.callIsIdealForPortal(destWorld, blockPos));
        if (spot != null){
            return createPortalInfo(entity, spot.above());
        }
        spot = TFTeleportAccessor.callFindPortalCoords(destWorld,info.pos,
                (blockPos)-> TFTeleportAccessor.callIsOkayForPortal(destWorld, blockPos));
        if (spot != null) {
            return createPortalInfo(entity, spot.above());
        }

        //still no safe position is found
        for(BlockPos.MutableBlockPos pos: BlockPos.spiralAround(destination, 16, Direction.EAST, Direction.SOUTH)){
            for (int y = Math.min(destination.getY(), destWorld.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ())); y > destWorld.getMinBuildHeight(); --y){
                pos.setY(y);
                if (isPositionSafe(destWorld,pos)) {
                    return createPortalInfo(entity, pos);
                }
            }
        }

        BlockPos defaultPos = destWorld.getWorldBorder().isWithinBounds(destination)
                && destWorld.getMinBuildHeight() < destination.getY() - 1
                && destWorld.getMaxBuildHeight() > destination.getY() + 2
                ? destination : new BlockPos(destination.getX(), (int)(destWorld.getHeight() * 0.167f) + destWorld.getMinBuildHeight(), destination.getZ());

        if(!isPositionSafe(destWorld,defaultPos)){
            buildPlatForm(destWorld, Blocks.COBBLESTONE.defaultBlockState(), defaultPos);
        }
        return createPortalInfo(entity, defaultPos);

    }
}
