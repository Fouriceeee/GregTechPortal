package com.ironsword.gtportal.common.block;

import com.ironsword.gtportal.api.portal.DimensionInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DimensionalPortalBlock extends Block{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    private static final VoxelShape
            X = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D),
            Y = Block.box(0.0D, 6.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Z = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    public static final EnumProperty<DimensionInfo> DIMENSIONS = EnumProperty.create("dimension", DimensionInfo.class,DimensionInfo.values());

    public DimensionalPortalBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X).setValue(DIMENSIONS,DimensionInfo.EMPTY));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(AXIS)) {
            case X -> X;
            case Y -> Y;
            case Z -> Z;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AXIS).add(DIMENSIONS);
    }
}
