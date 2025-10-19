package com.ironsword.gtportal.common.block;

import com.ironsword.gtportal.api.portal.PosData;
import com.ironsword.gtportal.common.data.GTPBlockEntities;
import com.ironsword.gtportal.common.item.RecorderItem;
import com.ironsword.gtportal.common.blockentity.PortalBlockEntity;
import com.ironsword.gtportal.common.portal.teleporter.SimplePosTelepoter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class PortalBlock extends BaseEntityBlock {


    public PortalBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack item = pPlayer.getItemInHand(pHand);
        if (item.getItem() instanceof RecorderItem){
            if (pLevel.getBlockEntity(pPos) instanceof PortalBlockEntity portalBlockEntity){
                CompoundTag posDataTag = item.getTagElement("posData");
                if (posDataTag!=null){
                    portalBlockEntity.setRecordedPos(PosData.fromNbt(posDataTag));
                    pPlayer.displayClientMessage(Component.literal("Success!"),true);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if(pLevel instanceof ServerLevel serverLevel && pEntity.canChangeDimensions()){
            if (pLevel.getBlockEntity(pPos) instanceof PortalBlockEntity portalBlockEntity){
                PosData posData = portalBlockEntity.getRecordedPos();
                if (posData==null){
                    return;
                }
                ServerLevel serverlevel = portalBlockEntity.getRecordedPos().getLevel(serverLevel.getServer());
                if (serverlevel == null) {
                    return;
                }
                if (serverlevel.dimension().location().equals(posData.dimension())){
                    Vec3i pos = posData.pos();
                    pEntity.teleportTo(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5);
                }else {
                    pEntity.changeDimension(serverlevel, new SimplePosTelepoter(serverlevel,new BlockPos(posData.pos())));
                }


            }
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PortalBlockEntity(GTPBlockEntities.PORTAL_BLOCK.get(), pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
