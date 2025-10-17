package com.ironsword.gtportal.common.portal.teleporter;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class SimplePosTelepoter implements ITeleporter {
    protected final ServerLevel level;
    protected final BlockPos pos;

    public SimplePosTelepoter(ServerLevel level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity e = repositionEntity.apply(false);
        if (e instanceof ServerPlayer player){
            player.teleportTo(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5);
            return player;
        }else{
            return e;
        }
    }
}
