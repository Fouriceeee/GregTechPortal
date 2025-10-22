package com.ironsword.gtportal.client.renderer;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.ironsword.gtportal.api.machine.feature.IBlockRenderMulti;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.RenderTypeHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BlockAreaRender extends DynamicRender<IBlockRenderMulti,BlockAreaRender> {

    public static final List<RelativeDirection> DEFAULT_FACES = List.of(RelativeDirection.UP,RelativeDirection.DOWN,
            RelativeDirection.LEFT,RelativeDirection.RIGHT,RelativeDirection.FRONT,RelativeDirection.BACK);

    public static final Codec<BlockAreaRender> CODEC = RecordCodecBuilder.create(instance->instance.group(
            FakeBlockRenderer.CODEC.forGetter(BlockAreaRender::getBlockRenderer),
            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("fixed_block").forGetter(BlockAreaRender::getFixedBlock),
            RelativeDirection.CODEC.listOf().optionalFieldOf("draw_faces",DEFAULT_FACES).forGetter(BlockAreaRender::getDrawFaces)
    ).apply(instance,BlockAreaRender::new));
    public static final DynamicRenderType<IBlockRenderMulti,BlockAreaRender> TYPE = new DynamicRenderType<>(CODEC);


    @Getter
    private final FakeBlockRenderer blockRenderer;
    private final boolean fixedBlock;
    @Getter
    private final List<RelativeDirection> drawFaces;

    private @Nullable Block cachedBlock;
    private @Nullable ResourceLocation cachedRecipe;

    public BlockAreaRender(FakeBlockRenderer blockRenderer,Optional<Block> fixedBlock, List<RelativeDirection> drawFaces) {
        this.blockRenderer = blockRenderer;
        if (fixedBlock.isPresent()){
            this.fixedBlock = true;
            this.cachedBlock = fixedBlock.get();
        }else{
            this.fixedBlock = false;
        }
        this.drawFaces = drawFaces;
    }


    @Override
    public DynamicRenderType<IBlockRenderMulti, BlockAreaRender> getType() {
        return TYPE;
    }

    @Override
    public void render(IBlockRenderMulti machine, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        if (!machine.isFormed()||machine.getRenderBlockOffsets()==null){
            return;
        }
        if (!fixedBlock){
            var lastRecipe = machine.getRecipeLogic().getLastRecipe();
            if (lastRecipe == null){
                cachedBlock = null;
                cachedRecipe = null;
            }else if (machine.self().getOffsetTimer()%20==0 || lastRecipe.id != cachedRecipe){
                cachedRecipe = lastRecipe.id;
                if (machine.isActive()){
                    cachedBlock = getRecipeBlockToRender();
                }else {
                    cachedBlock = null;
                }
            }
        }
        if (cachedBlock == null)return;

        var blockRenderType = ItemBlockRenderTypes.getRenderType(cachedBlock.defaultBlockState(),false);
        var consumer = buffer.getBuffer(blockRenderType);

        for (RelativeDirection face:this.drawFaces){
            poseStack.pushPose();
            var pose = poseStack.last().pose();

            var dir = face.getRelative(machine.self().getFrontFacing(),machine.self().getUpwardsFacing(),machine.self().isFlipped());
            if (dir.getAxis()!= Direction.Axis.Y) dir = dir.getOpposite();

            blockRenderer.drawPlane(dir,machine.getRenderBlockOffsets(),pose,consumer,cachedBlock,BuiltInRegistries.BLOCK.getKey(cachedBlock),packedOverlay,machine.self().getPos());

            poseStack.popPose();

        }
    }

    private Optional<Block> getFixedBlock(){
        if (fixedBlock) return Optional.ofNullable(cachedBlock);
        else return Optional.empty();

    }

    ///////
    //WIP//
    ///////
    private Block getRecipeBlockToRender(){
        return null;
    }

    @Override
    public boolean shouldRenderOffScreen(IBlockRenderMulti machine) {
        return true;
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
}
