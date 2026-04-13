package com.ironsword.gtportal.api.portal.teleporter;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.ironsword.gtportal.common.data.GTPPoiTypes;
import com.ironsword.gtportal.common.machine.multiblock.MultidimensionalPortalControllerMachine;
import com.ironsword.gtportal.common.machine.multiblock.SingleDimensionPortalControllerMachine;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class GTPTeleporter implements ITeleporter {
    public static final Map<ResourceLocation, ResourceKey<PoiType>> POITYPE_MAP = new HashMap<>(Map.of(
            Level.OVERWORLD.location(), GTPPoiTypes.OVERWORLD_PCM_POI.getKey(),
            Level.NETHER.location(), GTPPoiTypes.NETHER_PCM_POI.getKey(),
            Level.END.location(), GTPPoiTypes.END_PCM_POI.getKey()
    ));

    protected final Vec3 offset;
    protected final ServerLevel currWorld;
    protected final BlockPos currPos;
    protected BlockPos coordinate = null;
    protected final Block platformBlock;

    public GTPTeleporter(Vec3 offset,ServerLevel world, BlockPos controllerPos, @Nullable Vec3i coordinate, Block block){
        this.offset = offset;
        currWorld = world;
        currPos = controllerPos;
        this.coordinate = coordinate == null ? null : new BlockPos(coordinate);
        platformBlock = block;
    }

    protected static PortalInfo makePortalInfo(Entity entity,BlockPos pos){
        return new PortalInfo(new Vec3(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5),Vec3.ZERO,entity.getYRot(), entity.getXRot());
    }

    @Override
    public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        //if coordinate is not null, teleport to there directly
        if (coordinate != null){
            BlockEntity blockEntity = destWorld.getBlockEntity(coordinate);
            if (blockEntity instanceof MetaMachineBlockEntity machineEntity && machineEntity.getMetaMachine() instanceof MultidimensionalPortalControllerMachine portalMachine){
                return makePortalInfo(entity,portalMachine.getPos().relative(portalMachine.getFrontFacing()));
            }
            return makePortalInfo(entity, coordinate);
        }

        //else, find a proper place to teleport

        BlockPos scaledPos = getScaledPos(destWorld,this.currPos);

        //find nearest single dimension pcm
        Optional<PortalInfo> info1 = createSingleDimensionPCMPortalInfo(entity,destWorld,scaledPos,destWorld.getWorldBorder());

        if (info1.isPresent()){
            return info1.get();
        }

        //find nearest multi dimension pcm
        Optional<PortalInfo> info2 = createMultiDimensionPCMPortalInfo(entity,destWorld,scaledPos,destWorld.getWorldBorder());

        if (info2.isPresent()){
            return info2.get();
        }


        //find near portal block
//        Optional<Pair<Direction.Axis,BlockUtil.FoundRectangle>> pair = findPortalAround(destWorld,scaledPos,destWorld.getWorldBorder());
//
//        if (pair.isPresent()){
//            BlockPos pos = pair.get().getSecond().minCorner;
//            if (pair.get().getFirst().isHorizontal()){
//                Optional<BlockPos> safePos = safePortalEntrance(destWorld,pair.get().getFirst(),pair.get().getSecond());
//                if (safePos.isPresent()){
//                    return makePortalInfo(entity,safePos.get());
//                }
//            }else {
//                return makePortalInfo(entity,pos.offset(-1,1,-1));
//            }
//        }


        //find safe position to tp
        BlockPos destPos = searchDestPos(entity,destWorld,scaledPos);

        if (destPos == null){
            destPos = destWorld.getWorldBorder().isWithinBounds(scaledPos)
                    && destWorld.getMinBuildHeight() < scaledPos.getY() - 1
                    && destWorld.getMaxBuildHeight() > scaledPos.getY() + 2
                    ? scaledPos : new BlockPos(scaledPos.getX(), Math.max(destWorld.getMinBuildHeight(), 70), scaledPos.getZ());
            if (!isPositionSafe(destWorld,destPos)) buildPlatForm(destWorld,platformBlock.defaultBlockState(),destPos);
        }

        return makePortalInfo(entity,destPos);
    }

    protected Optional<BlockPos> findSingleDimensionPCMAround(ServerLevel destWorld, BlockPos scaledPos, WorldBorder worldBorder){
        PoiManager manager = destWorld.getPoiManager();
        manager.ensureLoadedAndValid(destWorld, scaledPos, 32);
        Optional<PoiRecord> optionalPoi = manager.getInSquare(poiType -> poiType.is(POITYPE_MAP.getOrDefault(currWorld.dimension().location(),GTPPoiTypes.OVERWORLD_PORTAL_POI.getKey())),scaledPos,32, PoiManager.Occupancy.ANY)
                .filter((poiRecord) -> worldBorder.isWithinBounds(poiRecord.getPos()))
                .sorted(Comparator.<PoiRecord>comparingDouble((poiRecord) -> poiRecord.getPos().distSqr(scaledPos)).thenComparingInt((poiRecord) -> poiRecord.getPos().getY()))
                .findFirst();
        return optionalPoi.map(PoiRecord::getPos);
    }

    protected Optional<PortalInfo> createSingleDimensionPCMPortalInfo(Entity entity, ServerLevel destWorld, BlockPos scaledPos, WorldBorder worldBorder){
        Optional<BlockPos> machinePos = findSingleDimensionPCMAround(destWorld,scaledPos,worldBorder);
        if (machinePos.isPresent()
                && destWorld.getBlockEntity(machinePos.get()) instanceof MetaMachineBlockEntity machineEntity
                && machineEntity.getMetaMachine() instanceof SingleDimensionPortalControllerMachine machine
                && machine.isActive()){
            return Optional.of(new PortalInfo(
                    machine.applyRelativeOffset(offset)
                    ,Vec3.ZERO, entity.getYRot(), entity.getXRot()));
        }else {
            return Optional.empty();
        }
    }

    protected Optional<BlockPos> findMultiDimensionPCMAround(ServerLevel destWorld, BlockPos scaledPos, WorldBorder worldBorder){
        PoiManager manager = destWorld.getPoiManager();
        manager.ensureLoadedAndValid(destWorld, scaledPos, 32);
        Optional<PoiRecord> optionalPois = manager.getInSquare(poiType -> poiType.is(GTPPoiTypes.MULTI_PCM_POI.getKey()), scaledPos, 32, PoiManager.Occupancy.ANY)
                .filter((poiRecord) -> worldBorder.isWithinBounds(poiRecord.getPos()))
                .filter(poiRecord -> {
                    BlockPos poiPos = poiRecord.getPos();
                    destWorld.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(poiPos), 3, poiPos);
                    return destWorld.getBlockEntity(poiPos) instanceof MetaMachineBlockEntity machineBlockEntity
                            && machineBlockEntity.getMetaMachine() instanceof MultidimensionalPortalControllerMachine pcm
                            && pcm.isActive() && currWorld.dimension().location().equals(pcm.getCache().getFirst());
                })
                .min(Comparator.<PoiRecord>comparingDouble((poiRecord) -> poiRecord.getPos().distSqr(scaledPos)).thenComparingInt((poiRecord) -> poiRecord.getPos().getY()));
        return optionalPois.map(PoiRecord::getPos);

    }

    protected Optional<PortalInfo> createMultiDimensionPCMPortalInfo(Entity entity, ServerLevel destWorld, BlockPos scaledPos, WorldBorder worldBorder){
        Optional<BlockPos> machinePos = findMultiDimensionPCMAround(destWorld,scaledPos,worldBorder);
        if(machinePos.isPresent()
                && destWorld.getBlockEntity(machinePos.get()) instanceof MetaMachineBlockEntity machineBlockEntity
                && machineBlockEntity.getMetaMachine() instanceof MultidimensionalPortalControllerMachine pcm
                && pcm.isActive()
                && currWorld.dimension().location().equals(pcm.getCache().getFirst())) {
            return Optional.of(new PortalInfo(
                    pcm.applyRelativeOffset(offset)
                    ,Vec3.ZERO, entity.getYRot(), entity.getXRot()));
        }else {
            return Optional.empty();
        }
    }

//    protected Optional<Pair<Direction.Axis,BlockUtil.FoundRectangle>> findPortalAround(ServerLevel destWorld, BlockPos scaledPos, WorldBorder worldBorder){
//        PoiManager manager = destWorld.getPoiManager();
//        manager.ensureLoadedAndValid(destWorld, scaledPos, 32);
//        Optional<PoiRecord> optionalPoi = manager.getInSquare(poiType -> poiType.is(POI_TYPE_MAP.getOrDefault(currWorld.dimension().location(),GTPPoiTypes.OVERWORLD_PORTAL_POI.getKey())),scaledPos,32, PoiManager.Occupancy.ANY)
//                .filter((poiRecord) -> worldBorder.isWithinBounds(poiRecord.getPos()))
//                .sorted(Comparator.<PoiRecord>comparingDouble((poiRecord) -> poiRecord.getPos().distSqr(scaledPos)).thenComparingInt((poiRecord) -> poiRecord.getPos().getY()))
//                .filter((poiRecord) -> destWorld.getBlockState(poiRecord.getPos()).hasProperty(BlockStateProperties.AXIS))
//                .findFirst();
//        return optionalPoi.map((poiRecord) -> {
//            BlockPos poiPos = poiRecord.getPos();
//            destWorld.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(poiPos), 3, poiPos);
//            BlockState blockstate = destWorld.getBlockState(poiPos);
//            return Pair.of(blockstate.getValue(BlockStateProperties.AXIS),BlockUtil.getLargestRectangleAround(poiPos, blockstate.getValue(BlockStateProperties.AXIS), 21, Direction.Axis.Y, 21, (blockPos) -> destWorld.getBlockState(blockPos) == blockstate));
//        });
//    }

    protected Optional<BlockPos> safePortalEntrance(ServerLevel destWorld, Direction.Axis axis,BlockUtil.FoundRectangle rectangle){
        BlockPos corner = rectangle.minCorner;
        for (BlockPos pos:BlockPos.betweenClosed(corner.relative(axis,-1),corner.relative(axis,-1).offset(2,2,2))){
            if (destWorld.getBlockState(pos).isAir()&&destWorld.getBlockState(pos.above()).isAir()){
                return Optional.of(pos);
            }
        }
        return Optional.empty();
    }

    protected BlockPos searchDestPos(Entity entity,ServerLevel destWorld, BlockPos scaledPos){
        for (var checkPos:BlockPos.spiralAround(scaledPos,16, Direction.EAST,Direction.SOUTH)){
            destWorld.getChunk(checkPos);

            for (int y = Math.min(scaledPos.getY(), destWorld.getHeight(Heightmap.Types.MOTION_BLOCKING, checkPos.getX(), checkPos.getZ())); y > destWorld.getMinBuildHeight(); --y){
                checkPos.setY(y);
                if (isPositionSafe(destWorld,checkPos))
                    return checkPos;
            }
        }

        return null;
    }

    protected BlockPos getScaledPos(ServerLevel destWorld, BlockPos currentPos){
        double scale = DimensionType.getTeleportationScale(currWorld.dimensionType(),destWorld.dimensionType());
        return destWorld.getWorldBorder().clampToBounds(((double)currentPos.getX())*scale,currentPos.getY(),((double)currentPos.getZ())*scale)
                .atY(Math.min(destWorld.getMaxBuildHeight(), destWorld.getMinBuildHeight() + destWorld.getLogicalHeight()) - 1);
    }

    protected boolean isPositionSafe(ServerLevel destWorld, BlockPos checkPos) {
        if (destWorld.getBlockState(checkPos.below()).isAir() || destWorld.getBlockState(checkPos.below()).liquid()) return false;
        for (var pos:BlockPos.betweenClosed(checkPos,checkPos.above(2))){
            if (!destWorld.getWorldBorder().isWithinBounds(pos) || destWorld.getMinBuildHeight() >= pos.getY()) return false;
            BlockState state = destWorld.getBlockState(pos);
            if (!state.getBlock().isPossibleToRespawnInThis(state)){
                return false;
            }
        }

        return true;
    }

    protected static void buildPlatForm(ServerLevel destWorld, BlockState baseBlock, BlockPos pos){
        BlockPos.betweenClosed(pos.offset(-1,-1,-1),pos.offset(1,2,1)).forEach(blockPos -> {
            if (!destWorld.getBlockState(blockPos).hasBlockEntity()) {
                destWorld.setBlockAndUpdate(blockPos,blockPos.getY() == pos.getY() - 1 ? baseBlock : Blocks.AIR.defaultBlockState());
            }
        });
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        entity.setPortalCooldown();
        return repositionEntity.apply(false);
    }
}
