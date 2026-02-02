package com.ironsword.gtportal.client.renderer;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.client.util.RenderUtil;
import com.ironsword.gtportal.api.machine.feature.IBlockRenderMulti;
import com.ironsword.gtportal.api.portal.DimensionInfo;
import com.ironsword.gtportal.common.block.DimensionalPortalBlock;
import com.ironsword.gtportal.common.block.PortalBlock;
import com.ironsword.gtportal.common.machine.multiblock.PortalControllerMachine;
import com.ironsword.gtportal.data.BlockStateGen;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.*;

//不打算用了，controller已经改成放置方块的逻辑了（但也不舍得删
public class PortalBlockRender extends DynamicRender<IBlockRenderMulti,PortalBlockRender> {

    public static final Codec<PortalBlockRender> CODEC = RecordCodecBuilder.create(instance->instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("block").forGetter(PortalBlockRender::getBlock)
    ).apply(instance,PortalBlockRender::new));
    public static final DynamicRenderType<IBlockRenderMulti,PortalBlockRender> TYPE = new DynamicRenderType<>(CODEC);

    @Getter
    private final Optional<Block> block;

    public PortalBlockRender(Optional<Block> block){
        this.block = block;
    }

    @Override
    public int getViewDistance() {
        return 32;
    }

    @Override
    public AABB getRenderBoundingBox(IBlockRenderMulti machine) {
        AABB box = super.getRenderBoundingBox(machine);
        var offsets = machine.getRenderBlockOffsets();
        for (var offset : offsets) {
            box = box.minmax(new AABB(offset));
        }
        return box.inflate(getViewDistance());
    }

    @Override
    public DynamicRenderType<IBlockRenderMulti, PortalBlockRender> getType() {
        return TYPE;
    }

    @Override
    public void render(IBlockRenderMulti machine, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        if (!machine.isFormed() ||
            machine.getRenderBlockOffsets() == null ||
            !machine.getRecipeLogic().isWorking() ||
            !(machine instanceof PortalControllerMachine) ||
            !(block.get() instanceof DimensionalPortalBlock)
        ) return;
        PortalControllerMachine portal = (PortalControllerMachine) machine;

        if (portal.getDimensionInfo().equals(DimensionInfo.EMPTY))
            return;

        BlockState state = block.get().defaultBlockState();

        state = state
                .setValue(BlockStateProperties.AXIS, portal.getFrontFacing().getAxis());
        state = state
                .setValue(DimensionalPortalBlock.DIMENSIONS, portal.getDimensionInfo());

        BlockPos prevOffset = null;
        for (BlockPos offset : machine.getRenderBlockOffsets()) {
            poseStack.pushPose();

            BlockPos currOffset = prevOffset == null ? offset : offset.subtract(prevOffset);
            poseStack.translate(currOffset.getX(), currOffset.getY(), currOffset.getZ());

            BlockPos pos = machine.self().getPos().offset(currOffset);

            RenderUtil.drawBlock(machine.self().getLevel(), pos, state, buffer, poseStack);

            poseStack.popPose();
        }
    }
}
