package com.ironsword.gtportal.api.portal.teleporter;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.ironsword.gtportal.common.machine.multiblock.MultidimensionalPortalControllerMachine;
import com.mojang.datafixers.util.Pair;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.portal.PortalInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class EndTeleporter extends GTPTeleporter{
    public EndTeleporter(ServerLevel world, BlockPos controllerPos, @Nullable Vec3i coordinate, Block block) {
        super(world, controllerPos, coordinate, block);
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

        Optional<Pair<Direction.Axis, BlockUtil.FoundRectangle>> pair = findPortalAround(destWorld,currPos,destWorld.getWorldBorder());

        if (pair.isPresent()){
            BlockPos pos = pair.get().getSecond().minCorner;
            if (pair.get().getFirst().isHorizontal()){
                return makePortalInfo(entity,pos.relative(pair.get().getFirst(),1));
            }else {
                return makePortalInfo(entity,pos.offset(-1,1,-1));
            }
        }

        ServerLevel.makeObsidianPlatform(destWorld);
        return makePortalInfo(entity,ServerLevel.END_SPAWN_POINT);
    }
}
