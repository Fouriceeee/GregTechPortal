package com.ironsword.gtportal.api.portal.teleporter;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.ironsword.gtportal.common.machine.multiblock.PortalControllerMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class Teleporter implements ITeleporter {

    protected BlockPos controllerPos = null;

    public Teleporter() {
    }

    public Teleporter(@Nullable Vec3i pos){
        if (pos != null)
            this.controllerPos = new BlockPos(pos);
    }

    @Override
    public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {

        if (controllerPos != null) {
            BlockEntity blockEntity = destWorld.getBlockEntity(controllerPos);
            if (blockEntity instanceof MetaMachineBlockEntity machineEntity && machineEntity.getMetaMachine() instanceof PortalControllerMachine portalMachine){
                BlockPos pos = portalMachine.getFrontPos();
                return new PortalInfo(new Vec3(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5),Vec3.ZERO,entity.getXRot(), entity.getYRot());
            }
        }

        PortalInfo info = searchPortalInfo(entity,destWorld);
        if (info != null) {
            return info;
        }

        info = searchPortalInfoAndBuild(entity,destWorld);
        return info;
    }

    private static PortalInfo searchPortalInfo(Entity entity, ServerLevel destWorld){
        double dimensionScale = DimensionType.getTeleportationScale(entity.level().dimensionType(), destWorld.dimensionType());

        BlockPos spawnPos = destWorld.getWorldBorder().clampToBounds(entity.blockPosition().getX() * dimensionScale, entity.blockPosition().getY(), entity.blockPosition().getZ() * dimensionScale)
                .atY(Math.min(destWorld.getMaxBuildHeight(), destWorld.getMinBuildHeight() + destWorld.getLogicalHeight()) - 1);

        for (var checkPos: BlockPos.spiralAround(spawnPos, 16, Direction.EAST, Direction.SOUTH)) {
            destWorld.getChunk(checkPos);

            for (int heightY = Math.min(spawnPos.getY(), destWorld.getHeight(Heightmap.Types.MOTION_BLOCKING, checkPos.getX(), checkPos.getZ())); heightY > destWorld.getMinBuildHeight(); --heightY) {
                checkPos.setY(heightY);

                if (!destWorld.getBlockState(checkPos.immutable().relative(Direction.DOWN)).isSolid()
                        || !isPositionSafe(destWorld, checkPos)
                ) continue;

                return new PortalInfo(new Vec3(checkPos.getX()+0.5,checkPos.getY(), checkPos.getZ()+0.5),Vec3.ZERO,entity.getXRot(), entity.getYRot());
            }
        }

        return null;
    }

    private static PortalInfo searchPortalInfoAndBuild(Entity entity, ServerLevel destWorld){
        BlockPos teleportPos = destWorld.getWorldBorder().isWithinBounds(entity.blockPosition())
                && destWorld.getMinBuildHeight() < entity.blockPosition().getY() - 1
                && destWorld.getMaxBuildHeight() > entity.blockPosition().getY() + entity.getBbHeight() + 1
                ? entity.blockPosition() : new BlockPos(0, Math.max(destWorld.getMinBuildHeight(), 70), 0);

        int minX = teleportPos.getX() - 2,
                minY = teleportPos.getY() - 1,
                minZ = teleportPos.getZ() - 2,
                maxX = teleportPos.getX() + 2,
                maxY = teleportPos.getY() + 3,
                maxZ = teleportPos.getZ() + 2;

        for (var pedestalPos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)){
            if (!destWorld.getWorldBorder().isWithinBounds(pedestalPos)) continue;

            BlockState pedestalState = destWorld.getBlockState(pedestalPos);

            if(pedestalPos.getY() == minY && !pedestalState.isSolid())
                destWorld.setBlockAndUpdate(pedestalPos, Blocks.OBSIDIAN.defaultBlockState());
            else if ((pedestalPos.getY() == maxY || pedestalPos.getX() == minX || pedestalPos.getX() == maxX || pedestalPos.getZ() == minZ || pedestalPos.getZ() == maxZ) && !pedestalState.isSolid())
                destWorld.setBlockAndUpdate(pedestalPos, Blocks.COBBLESTONE.defaultBlockState());
            else if (!pedestalState.getBlock().isPossibleToRespawnInThis(pedestalState))
                destWorld.setBlockAndUpdate(pedestalPos, Blocks.AIR.defaultBlockState());

        }

        return new PortalInfo(new Vec3(teleportPos.getX()+0.5,teleportPos.getY(), teleportPos.getZ()+0.5),Vec3.ZERO,entity.getXRot(), entity.getYRot());
    }

    private static boolean isPositionSafe(ServerLevel destWorld, BlockPos checkPos) {
        for (int i=0;i<3;i++){
            var pos = checkPos.above(i);

            if (!destWorld.getWorldBorder().isWithinBounds(pos) || destWorld.getMinBuildHeight() >= pos.getY()) return false;
            BlockState state = destWorld.getBlockState(pos);
            if (!state.getBlock().isPossibleToRespawnInThis(state)){
                return false;
            }
        }

        return true;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        return repositionEntity.apply(false);
    }
}
