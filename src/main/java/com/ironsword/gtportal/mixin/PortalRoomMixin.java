package com.ironsword.gtportal.mixin;

import com.ironsword.gtportal.GTPConfigHolder;
import com.ironsword.gtportal.common.data.GTPBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StrongholdPieces.PortalRoom.class)
public abstract class PortalRoomMixin extends StructurePiece {

    protected PortalRoomMixin(StructurePieceType pType, int pGenDepth, BoundingBox pBoundingBox) {
        super(pType, pGenDepth, pBoundingBox);
    }

    @Inject(
            method = "postProcess",
            at = @At("TAIL")
    )
    private void injectPostProcess(WorldGenLevel pLevel, StructureManager pStructureManager, ChunkGenerator pGenerator, RandomSource pRandom, BoundingBox pBox, ChunkPos pChunkPos, BlockPos pPos,CallbackInfo cir){
        if (GTPConfigHolder.INSTANCE.portalGateConfigs.generateVanillaEndPortalFrame) {
            return;
        }

        BlockState state = GTPBlocks.BROKEN_END_PORTAL_FRAME.getDefaultState();
        this.placeBlock(pLevel, state, 4, 3, 8, pBox);
        this.placeBlock(pLevel, state, 5, 3, 8, pBox);
        this.placeBlock(pLevel, state, 6, 3, 8, pBox);
        this.placeBlock(pLevel, state, 4, 3, 12, pBox);
        this.placeBlock(pLevel, state, 5, 3, 12, pBox);
        this.placeBlock(pLevel, state, 6, 3, 12, pBox);
        this.placeBlock(pLevel, state, 3, 3, 9, pBox);
        this.placeBlock(pLevel, state, 3, 3, 10, pBox);
        this.placeBlock(pLevel, state, 3, 3, 11, pBox);
        this.placeBlock(pLevel, state, 7, 3, 9, pBox);
        this.placeBlock(pLevel, state, 7, 3, 10, pBox);
        this.placeBlock(pLevel, state, 7, 3, 11, pBox);

        BlockState air = Blocks.AIR.defaultBlockState();
        this.placeBlock(pLevel, air, 4, 3, 9, pBox);
        this.placeBlock(pLevel, air, 5, 3, 9, pBox);
        this.placeBlock(pLevel, air, 6, 3, 9, pBox);
        this.placeBlock(pLevel, air, 4, 3, 10, pBox);
        this.placeBlock(pLevel, air, 5, 3, 10, pBox);
        this.placeBlock(pLevel, air, 6, 3, 10, pBox);
        this.placeBlock(pLevel, air, 4, 3, 11, pBox);
        this.placeBlock(pLevel, air, 5, 3, 11, pBox);
        this.placeBlock(pLevel, air, 6, 3, 11, pBox);
    }
}
