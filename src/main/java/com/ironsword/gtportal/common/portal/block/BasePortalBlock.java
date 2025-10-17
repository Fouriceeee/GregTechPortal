package com.ironsword.gtportal.common.portal.block;

import com.ironsword.gtportal.common.portal.teleporter.NetherTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.ITeleporter;

public abstract class BasePortalBlock extends Block {
    protected final ResourceKey<Level> DIMENSION;

    public BasePortalBlock(Properties pProperties, ResourceKey<Level> dimension) {
        super(pProperties);
        this.DIMENSION = dimension;
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if(pLevel instanceof ServerLevel && pEntity.canChangeDimensions()){
            if (pLevel.dimension() == DIMENSION){return;}

            ServerLevel serverlevel = ((ServerLevel)pLevel).getServer().getLevel(DIMENSION);
            if (serverlevel == null) {
                return;
            }

            //pEntity.changeDimension(serverlevel, new NetherTeleporter(serverlevel,pEntity.blockPosition()));
        }
    }
}
