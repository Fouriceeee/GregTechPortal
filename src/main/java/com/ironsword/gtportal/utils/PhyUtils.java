package com.ironsword.gtportal.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class PhyUtils {
    public static AABB getPortalBlockBox(Vec3i pos1, Vec3i pos2, Direction.Axis axis){
        return switch (axis){
            case X -> new AABB(
                    Math.min(pos1.getX(), pos2.getX()) + 6.0d/16.0d,
                    Math.min(pos1.getY(), pos2.getY()),
                    Math.min(pos1.getZ(), pos2.getZ()),
                    Math.max(pos1.getX(), pos2.getX()) + 10.0d/16.0d,
                    Math.max(pos1.getY(), pos2.getY()) + 1,
                    Math.max(pos1.getZ(), pos2.getZ()) + 1
            );
            case Y -> new AABB(
                    Math.min(pos1.getX(), pos2.getX()),
                    Math.min(pos1.getY(), pos2.getY()) + 6.0d/16.0d,
                    Math.min(pos1.getZ(), pos2.getZ()),
                    Math.max(pos1.getX(), pos2.getX()) + 1,
                    Math.max(pos1.getY(), pos2.getY()) + 10.0d/16.0d,
                    Math.max(pos1.getZ(), pos2.getZ()) + 1
            );
            case Z -> new AABB(
                    Math.min(pos1.getX(), pos2.getX()),
                    Math.min(pos1.getY(), pos2.getY()),
                    Math.min(pos1.getZ(), pos2.getZ()) + 6.0d/16.0d,
                    Math.max(pos1.getX(), pos2.getX()) + 1,
                    Math.max(pos1.getY(), pos2.getY()) + 1,
                    Math.max(pos1.getZ(), pos2.getZ()) + 10.0d/16.0d
            );
        };
    }

    public static AABB getMaxBox(Vec3i pos1, Vec3i pos2) {
        return new AABB(
                Math.min(pos1.getX(), pos2.getX()),
                Math.min(pos1.getY(), pos2.getY()),
                Math.min(pos1.getZ(), pos2.getZ()),
                Math.max(pos1.getX(), pos2.getX()) + 1,
                Math.max(pos1.getY(), pos2.getY()) + 1,
                Math.max(pos1.getZ(), pos2.getZ()) + 1
        );
    }

    public static void displayMessageInBoxes(Level level, BlockPos pos,int range, Component message){
        BlockPos pos1 = pos.above(range).north(range).east(range),
                pos2 = pos.below(range).south(range).west(range);
        level.getEntities(null, getMaxBox(pos1,pos2)).forEach(e->{
            if (e instanceof Player player){
                player.displayClientMessage(message,true);
            }
        });
    }

    public static Direction getMachineRightFacing(Direction front, Direction up){
        Vec3i vec = up.getNormal().cross(front.getNormal());
        return Direction.fromDelta(vec.getX(),vec.getY(),vec.getZ());
    }

    public static Direction getMachineUpFacing(Direction front, Direction upwards){
        if (front.getAxis().isVertical())
            return upwards;
        else {
            Direction.Axis axis = front.getAxis();
            Direction result = Direction.UP;
            switch (upwards){
                case EAST:
                    result = result.getClockWise(axis);
                case SOUTH:
                    result = result.getClockWise(axis);
                case WEST:
                    result = result.getClockWise(axis);
                default :
                    return result;
            }
        }

    }
}
