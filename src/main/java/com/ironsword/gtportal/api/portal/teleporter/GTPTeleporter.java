package com.ironsword.gtportal.api.portal.teleporter;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.ironsword.gtportal.common.data.GTPPoiTypes;
import com.ironsword.gtportal.common.machine.multiblock.MultidimensionalPortalControllerMachine;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
    public static final Map<ResourceLocation, ResourceKey<PoiType>> POI_TYPE_MAP = new HashMap<>(Map.of(
            Level.OVERWORLD.location(), GTPPoiTypes.OVERWORLD_PORTAL_POI.getKey(),
            Level.NETHER.location(), GTPPoiTypes.NETHER_PORTAL_POI.getKey(),
            Level.END.location(), GTPPoiTypes.END_PORTAL_POI.getKey()
    ));

    protected final ServerLevel currWorld;
    protected final BlockPos currPos;
    protected BlockPos coordinate = null;
    protected final Block platformBlock;

    public GTPTeleporter(ServerLevel world, BlockPos controllerPos, @Nullable Vec3i coordinate, Block block){
        currWorld = world;
        currPos = controllerPos;
        this.coordinate = coordinate == null ? null : new BlockPos(coordinate);
        platformBlock = block;
    }

    protected static PortalInfo makePortalInfo(Entity entity,BlockPos pos){
        return new PortalInfo(new Vec3(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5),Vec3.ZERO,entity.getXRot(), entity.getYRot());
    }

    @Override
    public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        if (coordinate != null){
            BlockEntity blockEntity = destWorld.getBlockEntity(coordinate);
            if (blockEntity instanceof MetaMachineBlockEntity machineEntity && machineEntity.getMetaMachine() instanceof MultidimensionalPortalControllerMachine portalMachine){
                return makePortalInfo(entity,portalMachine.getPos().relative(portalMachine.getFrontFacing()));
            }
            return makePortalInfo(entity, coordinate);
        }

        BlockPos currPos = getScaledPos(destWorld,this.currPos);

        Optional<Pair<Direction.Axis,BlockUtil.FoundRectangle>> pair = findPortalAround(destWorld,currPos,destWorld.getWorldBorder());

        if (pair.isPresent()){
            BlockPos pos = pair.get().getSecond().minCorner;
            if (pair.get().getFirst().isHorizontal()){
                return makePortalInfo(entity,pos.relative(pair.get().getFirst(),1));
            }else {
                return makePortalInfo(entity,pos.offset(-1,1,-1));
            }
        }

        BlockPos destPos = searchDestPos(destWorld,currPos);

        if (destPos == null){
            destPos = destWorld.getWorldBorder().isWithinBounds(currPos)
                    && destWorld.getMinBuildHeight() < currPos.getY() - 1
                    && destWorld.getMaxBuildHeight() > currPos.getY() + 2
                    ? currPos : new BlockPos(currPos.getX(), Math.max(destWorld.getMinBuildHeight(), 70), currPos.getZ());
            if (!isPositionSafe(destWorld,destPos)) buildPlatForm(destWorld,platformBlock.defaultBlockState(),destPos);
        }

        return makePortalInfo(entity,destPos);
    }

    protected Optional<Pair<Direction.Axis,BlockUtil.FoundRectangle>> findPortalAround(ServerLevel destWorld, BlockPos scaledPos, WorldBorder worldBorder){
        PoiManager manager = destWorld.getPoiManager();
        manager.ensureLoadedAndValid(destWorld, scaledPos, 128);
        Optional<PoiRecord> optionalPoi = manager.getInSquare(poiType -> poiType.is(POI_TYPE_MAP.getOrDefault(destWorld.dimension().location(),GTPPoiTypes.OVERWORLD_PORTAL_POI.getKey())),scaledPos,128, PoiManager.Occupancy.ANY)
                .filter((poiRecord) -> worldBorder.isWithinBounds(poiRecord.getPos()))
                .sorted(Comparator.<PoiRecord>comparingDouble((poiRecord) -> poiRecord.getPos().distSqr(scaledPos)).thenComparingInt((poiRecord) -> poiRecord.getPos().getY()))
                .filter((poiRecord) -> destWorld.getBlockState(poiRecord.getPos()).hasProperty(BlockStateProperties.AXIS))
                .findFirst();
        return optionalPoi.map((poiRecord) -> {
            BlockPos poiPos = poiRecord.getPos();
            destWorld.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(poiPos), 3, poiPos);
            BlockState blockstate = destWorld.getBlockState(poiPos);
            return Pair.of(blockstate.getValue(BlockStateProperties.AXIS),BlockUtil.getLargestRectangleAround(poiPos, blockstate.getValue(BlockStateProperties.AXIS), 21, Direction.Axis.Y, 21, (blockPos) -> destWorld.getBlockState(blockPos) == blockstate));
        });
    }

    protected BlockPos searchDestPos(ServerLevel destWorld, BlockPos scaledPos){
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
        return repositionEntity.apply(false);
    }
}
