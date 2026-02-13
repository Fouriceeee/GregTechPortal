package com.ironsword.gtportal.api.portal.teleporter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class GTPTeleporter implements ITeleporter {
    protected final ServerLevel currWorld;
    protected BlockPos controllerPos = null;
    protected final Block platformBlock;

    public GTPTeleporter(ServerLevel world, @Nullable Vec3i pos, Block block){
        currWorld = world;
        controllerPos = pos == null ? null : new BlockPos(pos);
        platformBlock = block;
    }

    protected static PortalInfo makePortalInfo(Entity entity,BlockPos pos){
        return new PortalInfo(new Vec3(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5),Vec3.ZERO,entity.getXRot(), entity.getYRot());
    }

    @Override
    public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        if (controllerPos != null){
//            BlockEntity blockEntity = destWorld.getBlockEntity(controllerPos);
//            if (blockEntity instanceof MetaMachineBlockEntity machineEntity && machineEntity.getMetaMachine() instanceof PortalControllerMachine portalMachine){
//                return makePortalInfo(entity,portalMachine.getFrontPos());
//            }
            return makePortalInfo(entity,controllerPos);
        }

        BlockPos currPos = getScaledPos(destWorld,entity.blockPosition()),
                destPos = searchDestPos(destWorld,currPos);

        if (destPos == null){
            destPos = destWorld.getWorldBorder().isWithinBounds(currPos)
                    && destWorld.getMinBuildHeight() < currPos.getY() - 1
                    && destWorld.getMaxBuildHeight() > currPos.getY() + 2
                    ? currPos : new BlockPos(currPos.getX(), Math.max(destWorld.getMinBuildHeight(), 70), currPos.getZ());
            if (!isPositionSafe(destWorld,destPos)) buildPlatForm(destWorld,platformBlock.defaultBlockState(),destPos);
        }

        return makePortalInfo(entity,destPos);
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
