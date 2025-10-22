package com.ironsword.gtportal.client.renderer;

import com.gregtechceu.gtceu.client.util.RenderUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Data;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.gregtechceu.gtceu.client.util.RenderUtil.getNormal;
import static com.gregtechceu.gtceu.client.util.RenderUtil.getVertices;
import static net.minecraft.util.FastColor.ARGB32.*;
import static net.minecraft.util.FastColor.ARGB32.alpha;

public class FakeBlockRenderer {

    private static final ResourceLocation ATLAS = TextureAtlas.LOCATION_BLOCKS;
    //InventoryMenu.BLOCK_ATLAS;

    public static final MapCodec<FakeBlockRenderer> CODEC = Properties.CODEC
            .xmap(FakeBlockRenderer::new, FakeBlockRenderer::getProperties);

    @Getter
    private final Properties properties;

    public FakeBlockRenderer(Properties properties) {
        this.properties = properties;
    }

    public Vector3f[] transformVertices(Vector3fc[] vertices, Direction face){
        float offsetX = properties.offsetX,offsetY = properties.offsetY,offsetZ = properties.offsetZ;

        var newVertices = new Vector3f[4];
        for (int i=0;i<4;i++){
            newVertices[i] = RenderUtil.transformVertex(vertices[i],face,offsetX,offsetY,offsetZ);
        }

        return newVertices;
    }

    public void drawBlocks(Set<BlockPos> offsets, Matrix4f pose, VertexConsumer consumer,
                           Block block, ResourceLocation texture, int combinedOverlay, int combinedLight){
        var blockClientInfo = IClientBlockExtensions.of(block);
        var sprite = Minecraft.getInstance().getTextureAtlas(ATLAS).apply(texture);
        float u0 = sprite.getU0(), v0 = sprite.getV0(), u1 = sprite.getU1(), v1 = sprite.getV1();
        int color = 0xFFFFFFFF;
        int r = red(color), g = green(color), b = blue(color), a = alpha(color);

        for (var pos:offsets){
            pose.translate(pos.getX(),pos.getY(),pos.getZ());
            for (var direction : Direction.values()){
                if (offsets.contains(pos.relative(direction))) continue;
                if (direction != Direction.UP && direction!= Direction.DOWN) direction = direction.getOpposite();

                drawFace(pose, consumer,
                        transformVertices(getVertices(direction), direction),
                        getNormal(direction),
                        u0, u1, v0, v1,
                        r, g, b, a,
                        combinedOverlay, combinedLight);
            }
            pose.translate(-pos.getX(), -pos.getY(), -pos.getZ());
        }

    }

    public void drawPlanes(Direction[] faces, Map<Direction, Collection<BlockPos>> directionalOffsets, Matrix4f pose,
                           VertexConsumer consumer, Block block, ResourceLocation texture,
                           int combinedOverlay, int combinedLight){
        for (var face : faces){
            if (!directionalOffsets.containsKey(face)) continue;
            drawPlane(face,directionalOffsets.get(face),pose,consumer,block,texture,combinedOverlay,combinedLight);
        }

    }

    public void drawPlane(Direction face, Collection<BlockPos> offsets, Matrix4f pose, VertexConsumer consumer,
                          Block block, ResourceLocation texture, int combinedOverlay, BlockPos origin){
        var blockClientInfo = IClientBlockExtensions.of(block);
        var sprite = Minecraft.getInstance().getTextureAtlas(ATLAS).apply(texture);
        float u0 = sprite.getU0(), v0 = sprite.getV0(), u1 = sprite.getU1(), v1 = sprite.getV1();
        int color = 0xFFFFFFFF;
        int r = red(color), g = green(color), b = blue(color), a = alpha(color);
        var normal = getNormal(face);
        var vertices = transformVertices(getVertices(face),face);

        BlockPos prevOffset = null;
        for (var offset : offsets){
            BlockPos currOffset = prevOffset == null ? offset : offset.subtract(prevOffset);
            pose.translate(currOffset.getX(),currOffset.getY(),currOffset.getZ());
            drawFace(pose,consumer,vertices,normal,
                    u0,u1,v0,v1,
                    r,g,b,a,
                    combinedOverlay,getBlockLight(block,origin.offset(currOffset)));
            prevOffset = offset;
        }
    }

    private static int getBlockLight(Block block,BlockPos pos) {
        if (Minecraft.getInstance().level == null) return 0;
        return LevelRenderer.getLightColor(Minecraft.getInstance().level,block.defaultBlockState(),pos);

    }

    public void drawPlane(Direction face, Collection<BlockPos> offsets, Matrix4f pose, VertexConsumer consumer,
                          Block block, ResourceLocation texture, int combinedOverlay, int combinedLight){
        var blockClientInfo = IClientBlockExtensions.of(block);
        var sprite = Minecraft.getInstance().getTextureAtlas(ATLAS).apply(texture);
        float u0 = sprite.getU0(), v0 = sprite.getV0(), u1 = sprite.getU1(), v1 = sprite.getV1();
        int color = 0xFFFFFFFF;
        int r = red(color), g = green(color), b = blue(color), a = alpha(color);
        var normal = getNormal(face);
        var vertices = transformVertices(getVertices(face),face);

        BlockPos prevOffset = null;
        for (var offset : offsets){
            BlockPos currOffset = prevOffset == null ? offset : offset.subtract(prevOffset);
            pose.translate(currOffset.getX(),currOffset.getY(),currOffset.getZ());
            drawFace(pose,consumer,vertices,normal,
                    u0,u1,v0,v1,
                    r,g,b,a,
                    combinedOverlay,combinedLight);
            prevOffset = offset;
        }
    }

    public void drawFace(Direction face, Matrix4f pose, VertexConsumer consumer, Block block,
                         ResourceLocation texture,int combinedOverlay, int combinedLight){

        var blockClientInfo = IClientBlockExtensions.of(block);

        var sprite = Minecraft.getInstance().getTextureAtlas(ATLAS).apply(texture);

        float u0 = sprite.getU0(), v0 = sprite.getV0(), u1 = sprite.getU1(), v1 = sprite.getV1();

        int color = 0xFFFFFFFF;

        int r = red(color), g = green(color), b = blue(color), a = alpha(color);

        var normal = getNormal(face);

        var vertices = transformVertices(getVertices(face),face);

        drawFace(pose,consumer,vertices,normal,
                u0,u1,v0,v1,
                r,g,b,a,
                combinedOverlay,combinedLight);
    }

    public void drawFace(Matrix4f pose, VertexConsumer consumer, Vector3f[] vertices, Vector3fc normal,
                          float u0, float u1, float v0, float v1,
                          int r, int g, int b, int a,
                          int combinedOverlay, int combinedLight){
        if (properties.isOverwriteLight()) combinedLight = properties.getLight();

        RenderUtil.vertex(pose,consumer,vertices[0].x,vertices[0].y,vertices[0].z,
                r,g,b,a,
                u0,v1,
                combinedOverlay,combinedLight,normal.x(),normal.y(),normal.z());

        RenderUtil.vertex(pose,consumer,vertices[1].x,vertices[1].y,vertices[1].z,
                r,g,b,a,
                u0,v0,
                combinedOverlay,combinedLight,normal.x(),normal.y(),normal.z());

        RenderUtil.vertex(pose,consumer,vertices[2].x,vertices[2].y,vertices[2].z,
                r,g,b,a,
                u1,v0,
                combinedOverlay,combinedLight,normal.x(),normal.y(),normal.z());

        RenderUtil.vertex(pose,consumer,vertices[3].x,vertices[3].y,vertices[3].z,
                r,g,b,a,
                u1,v1,
                combinedOverlay,combinedLight,normal.x(),normal.y(),normal.z());
    }


    @Data
    public static class Properties {

        public static final MapCodec<Properties> CODEC = RecordCodecBuilder.mapCodec(instance->instance.group(
                Codec.FLOAT.optionalFieldOf("offset_x",0.0f).forGetter(Properties::getOffsetX),
                Codec.FLOAT.optionalFieldOf("offset_y",0.0f).forGetter(Properties::getOffsetY),
                Codec.FLOAT.optionalFieldOf("offset_z",0.0f).forGetter(Properties::getOffsetZ),
                Codec.BOOL.optionalFieldOf("overwrite_light",false).forGetter(Properties::isOverwriteLight),
                Codec.intRange(0, LightEngine.MAX_LEVEL).optionalFieldOf("block_light",0).forGetter(Properties::getBlockLight),
                Codec.intRange(0, LightEngine.MAX_LEVEL).optionalFieldOf("sky_light",0).forGetter(Properties::getSkyLight)
        ).apply(instance,Properties::of));


        private float offsetX = 0;
        private float offsetY = 0;
        private float offsetZ = 0;
        private boolean overwriteLight = false;
        private int light = 0;

        public static Properties of(float offsetX, float offsetY, float offsetZ,boolean overwriteLight,int light){
            Properties p = new Properties();
            p.setOffsetX(offsetX);
            p.setOffsetY(offsetY);
            p.setOffsetZ(offsetZ);
            p.setOverwriteLight(overwriteLight);
            p.setLight(light);
            return p;
        }

        private int getBlockLight(){
            return LightTexture.block(this.light);
        }

        private int getSkyLight(){
            return LightTexture.sky(this.light);
        }

        private static Properties of(float offsetX, float offsetY, float offsetZ, boolean overwriteLight, int blockLight, int skyLight){
            return of(offsetX,offsetY,offsetZ,overwriteLight,LightTexture.pack(blockLight,skyLight));
        }
    }

    public static class Builder{

        private final Properties properties;

        public Builder() {
            this.properties = new Properties();
        }

        public static Builder create(){
            return new Builder();
        }

        public Builder setOffset(Vector3f offset) {
            return setOffset(offset.x, offset.y, offset.z);
        }

        public Builder setOffset(float offsetX, float offsetY, float offsetZ) {
            properties.setOffsetX(offsetX);
            properties.setOffsetY(offsetY);
            properties.setOffsetZ(offsetZ);
            return this;
        }

        public Builder setForcedLight(int light) {
            properties.setLight(light);
            properties.setOverwriteLight(true);
            return this;
        }

        public Builder setForcedLight(int block, int sky) {
            properties.setLight(LightTexture.pack(block, sky));
            properties.setOverwriteLight(true);
            return this;
        }

        public FakeBlockRenderer getRenderer(){
            return new FakeBlockRenderer(properties);
        }
    }
}
