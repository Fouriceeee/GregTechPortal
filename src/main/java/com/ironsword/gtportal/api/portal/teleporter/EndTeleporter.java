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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class EndTeleporter extends GTPTeleporter{


    public EndTeleporter(Vec3 offset, ServerLevel world, BlockPos controllerPos, @Nullable Vec3i coordinate, Block block) {
        super(offset, world, controllerPos, coordinate, block);
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

        BlockPos scaledPos = getScaledPos(destWorld,this.currPos);

        Optional<PortalInfo> info1 = createSingleDimensionPCMPortalInfo(entity,destWorld,scaledPos,destWorld.getWorldBorder());

        if (info1.isPresent()){
            return info1.get();
        }

        Optional<PortalInfo> info2 = createMultiDimensionPCMPortalInfo(entity,destWorld,scaledPos,destWorld.getWorldBorder());

        if (info2.isPresent()){
            return info2.get();
        }

        ServerLevel.makeObsidianPlatform(destWorld);
        return makePortalInfo(entity,ServerLevel.END_SPAWN_POINT);
    }
}
