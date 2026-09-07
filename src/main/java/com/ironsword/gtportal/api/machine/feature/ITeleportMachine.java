package com.ironsword.gtportal.api.machine.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public interface ITeleportMachine {
    void teleportEntities();
    boolean canTeleport();
    Vec3 applyOffset(Vec3 offset);

    /**
     * @return all the portal block positions of this portal machine structure
     */
    Set<BlockPos> getPortalPoses();

    /**
     * @return the controller block position of this machine
     */
    BlockPos getPos();
}
