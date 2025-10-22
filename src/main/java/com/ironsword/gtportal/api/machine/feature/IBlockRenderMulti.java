package com.ironsword.gtportal.api.machine.feature;

import com.gregtechceu.gtceu.api.machine.feature.IMachineFeature;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IWorkableMultiController;
import net.minecraft.core.BlockPos;

import java.util.Set;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface IBlockRenderMulti extends IWorkableMultiController, IMachineFeature {

    @ApiStatus.NonExtendable
    default Set<BlockPos> getRenderBlockOffsets(){
        Set<BlockPos> offsets = getBlockOffsets();
        if(offsets.isEmpty() && this.isFormed()){
            offsets = saveOffsets();
            setBlockOffsets(offsets);
        }
        return offsets;
    }

    @ApiStatus.OverrideOnly
    @NotNull
    Set<BlockPos> getBlockOffsets();

    @ApiStatus.Internal
    void setBlockOffsets(@NotNull Set<BlockPos> offsets);

    @Override
    default void onStructureFormed(){
        saveOffsets();
    }

    @Override
    default void onStructureInvalid(){
        getBlockOffsets().clear();
    }

    @NotNull
    Set<BlockPos> saveOffsets();
}
