package com.ironsword.gtportal.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class Utils {

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
}
