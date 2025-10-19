package com.ironsword.gtportal.common.blockentity;

import com.ironsword.gtportal.api.portal.PosData;
import com.ironsword.gtportal.common.item.RecorderItem;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PortalBlockEntity extends BlockEntity {

    @Getter
    @Setter
    private PosData recordedPos;

    public PortalBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
}
