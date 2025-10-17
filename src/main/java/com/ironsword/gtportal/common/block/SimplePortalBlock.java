package com.ironsword.gtportal.common.block;

import com.ironsword.gtportal.common.portal.teleporter.NetherTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SimplePortalBlock extends Block {

    public SimplePortalBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if(pLevel instanceof ServerLevel && pEntity.canChangeDimensions()){
            ResourceKey<Level> resourcekey = pLevel.dimension() == Level.NETHER ? Level.OVERWORLD : Level.NETHER;
            ServerLevel serverlevel = ((ServerLevel)pLevel).getServer().getLevel(resourcekey);
            if (serverlevel == null) {
                return;
            }

            pEntity.changeDimension(serverlevel, new NetherTeleporter(serverlevel,pEntity.blockPosition()));
        }
    }

}
