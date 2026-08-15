package com.ironsword.gtportal.api.machine.feature;

import net.minecraft.world.phys.Vec3;

public interface ITeleportMachine {
    void teleportEntities();
    boolean canTeleport();
    Vec3 applyOffset(Vec3 offset);
}
