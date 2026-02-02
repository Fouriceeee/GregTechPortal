package com.ironsword.gtportal.common.block;

import com.ironsword.gtportal.api.portal.DimensionInfo;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class DimensionalPortalBlock extends PortalBlock{
    public static final EnumProperty<DimensionInfo> DIMENSIONS = EnumProperty.create("dimension", DimensionInfo.class,DimensionInfo.values());

    public DimensionalPortalBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DIMENSIONS,DimensionInfo.EMPTY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(BlockStateProperties.AXIS)
                .add(DIMENSIONS);
    }
}
