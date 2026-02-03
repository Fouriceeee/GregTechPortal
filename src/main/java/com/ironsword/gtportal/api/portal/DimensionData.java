package com.ironsword.gtportal.api.portal;

import net.minecraft.Util;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record DimensionData(@Nonnull DimensionInfo info, @Nullable Vec3i pos) {
    public CompoundTag toNbt(){
        return Util.make(new CompoundTag(),tag ->{
            tag.putString("dim",info.getSerializedName());
            if (pos!=null){
                tag.put("pos",makePosTag());
            }
        });
    }

    public CompoundTag makePosTag(){
        return Util.make(new CompoundTag(), tag ->{
            tag.putInt("x",pos.getX());
            tag.putInt("y",pos.getY());
            tag.putInt("z",pos.getZ());
        });
    }

    public static DimensionData fromNbt(CompoundTag tag){
        DimensionInfo info = DimensionInfo.byName(tag.getString("dim"));
        Vec3i pos = null;
        if (tag.contains("pos")){
            CompoundTag posTag = tag.getCompound("pos");
            pos = new Vec3i(posTag.getInt("x"),posTag.getInt("y"),posTag.getInt("z"));
        }
        return new DimensionData(info,pos);
    }

    public String toString(){
        return "Dimension: "+info.getSerializedName()+(pos == null ? "" : " Position: "+pos.getX()+", "+pos.getY()+", "+pos.getZ());
    }

    public MutableComponent toDimension(){
        return Component.translatable("gtportal.machine.tooltip.dimension")
                .append(": ")
                .append(Component.translatable(info.getTranslateKey()));
    }

    public MutableComponent toPosition(){
        return hasPos() ? Component.translatable("gtportal.machine.tooltip.position").append(": "+pos.getX()+", "+pos.getY()+", "+pos.getZ()) : Component.empty();
    }

    public boolean hasPos(){
        return pos != null;
    }

    public Component toComponent(){
        return toDimension().append(hasPos() ? Component.literal(" ").append(toPosition()) : Component.empty());
    }

    public ServerLevel getLevel(MinecraftServer server) {
        return server.getLevel(ResourceKey.create(Registries.DIMENSION, info.getId()));
    }
}
