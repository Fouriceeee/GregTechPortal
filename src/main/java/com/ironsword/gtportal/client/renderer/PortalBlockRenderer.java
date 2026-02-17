package com.ironsword.gtportal.client.renderer;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.client.util.RenderUtil;
import com.ironsword.gtportal.GTPConfigHolder;
import com.ironsword.gtportal.api.machine.feature.IBlockRenderMulti;
import com.ironsword.gtportal.common.machine.multiblock.MultidimensionalPortalControllerMachine;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PortalBlockRenderer extends DynamicRender<IBlockRenderMulti, PortalBlockRenderer> {

    public static final Codec<PortalBlockRenderer> CODEC = RecordCodecBuilder.create(instance->instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("fixed_block").forGetter(PortalBlockRenderer::getFixedBlock)
    ).apply(instance, PortalBlockRenderer::new));
    public static final DynamicRenderType<IBlockRenderMulti, PortalBlockRenderer> TYPE = new DynamicRenderType<>(CODEC);

    private final boolean fixedBlock;

    private @Nullable Block cachedBlock;
    private @Nullable ResourceLocation cachedRecipe;

    public PortalBlockRenderer(Optional<Block> block){
        if (block.isPresent()){
            fixedBlock = true;
            cachedBlock = block.get();
        }else {
            fixedBlock = false;
        }
    }

    @Override
    public int getViewDistance() {
        return 32;
    }

    private Optional<Block> getFixedBlock(){
        if (fixedBlock) return Optional.ofNullable(cachedBlock);
        else return Optional.empty();
    }

    @Override
    public DynamicRenderType<IBlockRenderMulti, PortalBlockRenderer> getType() {
        return TYPE;
    }

    @Override
    public void render(IBlockRenderMulti machine, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        if(GTPConfigHolder.INSTANCE.portalBlockConfigs.generatePortalBlocks) {
            return;
        }

        if (!machine.isFormed() || machine.getRenderBlockOffsets() == null){
            return;
        }
        if (!fixedBlock){
            var lastRecipe = machine.getRecipeLogic().getLastRecipe();
            if (lastRecipe == null){
                cachedRecipe = null;
                cachedBlock = null;
            }else if (machine.self().getOffsetTimer() % 20 == 0 || lastRecipe.id != cachedRecipe){
                cachedRecipe = lastRecipe.id;
                if (machine.getRecipeLogic().isWorking()){
                    cachedBlock = MultidimensionalPortalControllerMachine.MAP.getOrDefault(new ResourceLocation(lastRecipe.data.getString("dimension")), MultidimensionalPortalControllerMachine.EMPTY).getFirst().get();
                }else {
                    cachedBlock = null;
                }
            }
        }else {
            if (!machine.getRecipeLogic().isWorking()){
                return;
            }
        }
        if (cachedBlock == null){
            return;
        }

        BlockState state = cachedBlock.defaultBlockState().setValue(BlockStateProperties.AXIS,((MetaMachine) machine).getFrontFacing().getAxis());

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

    @Override
    public boolean shouldRenderOffScreen(IBlockRenderMulti machine) {
        return true;
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
}
