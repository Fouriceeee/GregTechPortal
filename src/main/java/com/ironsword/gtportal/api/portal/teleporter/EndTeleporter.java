package com.ironsword.gtportal.api.portal.teleporter;

import com.ironsword.gtportal.api.machine.feature.ITeleportMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EndTeleporter extends DefaultTeleporter{
    public EndTeleporter(ServerLevel level, Vec3 offset, @Nullable Vec3i coordinate, @Nullable ITeleportMachine sourceMachine) {
        super(level, offset, coordinate, sourceMachine);
    }

    @Override
    protected PortalInfo searchProperPosNearby(Entity entity, ServerLevel destWorld, BlockPos destination, int searchRadius) {
        ServerLevel.makeObsidianPlatform(destWorld);
        return createPortalInfo(entity, ServerLevel.END_SPAWN_POINT);
    }
}
