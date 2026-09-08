package com.ironsword.gtportal.api.portal.teleporter;

import com.ironsword.gtportal.api.machine.feature.ITeleportMachine;
import com.ironsword.gtportal.mixin.accessor.TFTeleportAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class TwilightTeleporter extends DefaultTeleporter{
    public TwilightTeleporter(ServerLevel level, Vec3 offset, @Nullable Vec3i coordinate, @Nullable ITeleportMachine sourceMachine) {
        super(level, offset, coordinate, sourceMachine);
    }

    @Override
    protected PortalInfo searchProperPosNearby(Entity entity, ServerLevel destWorld, BlockPos destination, int searchRadius) {
        PortalInfo info = TFTeleportAccessor.callMoveToSafeCoords(destWorld, entity, destination);
        TFTeleportAccessor.callLoadSurroundingArea(destWorld, info.pos);

        BlockPos spot = TFTeleportAccessor.callFindPortalCoords(destWorld, info.pos,
                (blockPos)-> TFTeleportAccessor.callIsIdealForPortal(destWorld, blockPos));
        if (spot == null){
            spot = TFTeleportAccessor.callFindPortalCoords(destWorld, info.pos,
                    (blockPos)-> TFTeleportAccessor.callIsOkayForPortal(destWorld, blockPos));
        }

        if (spot != null){
            BlockPos standPos = spot.above();
            //search a portal machine around the spot again; if found, teleport to the machine
            Optional<PortalInfo> machinePortal = findMachinePortal(entity, destWorld, standPos, searchRadius);
            if (machinePortal.isPresent()){
                return machinePortal.get();
            }
            //still no machine around the spot, teleport directly to it
            return createPortalInfo(entity, standPos);
        }

        return super.searchProperPosNearby(entity, destWorld, destination, searchRadius);
    }
}
