package com.ironsword.gtportal.common.portal.teleporter;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class NetherTeleporter implements ITeleporter {
    protected final ServerLevel level;
    protected final BlockPos pos;

    public NetherTeleporter(ServerLevel pLevel,BlockPos pos) {
        this.level = pLevel;
        this.pos = pos;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity e = repositionEntity.apply(false);
        if (e instanceof ServerPlayer player){
            BlockPos targetPos;
            Block block;
            if (level.dimension() == Level.NETHER){
                targetPos = new BlockPos(pos.getX()/8,70,pos.getZ()/8);
                block = Blocks.NETHERRACK;
            }else {
                targetPos = new BlockPos(pos.getX()*8,64,pos.getZ()*8);
                block = Blocks.STONE;
            }

            BlockPos searchPos = searchPlatform(targetPos);
            if (searchPos!=null)
                targetPos = searchPos;

            placePlatform2(targetPos,block);
            player.teleportTo(targetPos.getX()+0.5,targetPos.getY(),targetPos.getZ()+0.5);

            return player;
        }else {
            return e;
        }
    }

    private BlockPos searchPlatform(BlockPos startingPos){
        BlockPos pos1 = startingPos;
        int flag = 0;
        for (int y= startingPos.getY();y<level.getMaxBuildHeight()-2;y++){
            if (level.getBlockState(pos1).isAir()&&level.getBlockState(pos1.above()).isAir()){
                return pos1;
            }
            pos1 = pos1.above();
        }
        pos1 = startingPos;
        for (int y = startingPos.getY();y>level.getMinBuildHeight();y++){
            if (level.getBlockState(pos1).isAir()&&level.getBlockState(pos1.above()).isAir()){
                return pos1;
            }
            pos1 = pos1.below();
        }
        return null;
    }

    private void placePlatform2(BlockPos targetPos, Block block){
        level.setBlockAndUpdate(targetPos.below(), block.defaultBlockState());
        BlockState blockState = level.getBlockState(targetPos.above().above());
        if (!blockState.isSolid()&&!blockState.isAir()){
            level.setBlockAndUpdate(targetPos.above().above(), block.defaultBlockState());
        }
    }


    //de
    private void placePlatform(BlockPos targetPos, Block block){
        for (int y=-1;y<3;y++){
            for (int x=-1;x<2;x++){
                for (int z=-1;z<2;z++){
                    BlockPos pos = targetPos.offset(x,y,z);
                    BlockState blockState = level.getBlockState(pos);
                    if (blockState.hasBlockEntity()){
                        continue;
                    }
                    if (y==-1){
                        if (!blockState.isSolid()){
                            level.setBlockAndUpdate(pos, block.defaultBlockState());
                        }
                    } else {
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
    }


}
