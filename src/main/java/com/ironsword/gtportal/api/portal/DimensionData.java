package com.ironsword.gtportal.api.portal;

import net.minecraft.Util;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record DimensionData(@Nonnull ResourceLocation dimension, @Nullable Vec3i pos) {
    public CompoundTag toNbt(){
        return Util.make(new CompoundTag(),tag ->{
            tag.putString("dim",dimension.toString());
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
        ResourceLocation dimension = new ResourceLocation(tag.getString("dim"));
        Vec3i pos = null;
        if (tag.contains("pos")){
            CompoundTag posTag = tag.getCompound("pos");
            pos = new Vec3i(posTag.getInt("x"),posTag.getInt("y"),posTag.getInt("z"));
        }
        return new DimensionData(dimension,pos);
    }

    public String toString(){
        return "Dimension: "+dimension.toString()+(pos == null ? "" : " Position: "+pos.getX()+", "+pos.getY()+", "+pos.getZ());
    }

    public ServerLevel getLevel(MinecraftServer server) {
        return server.getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
    }
}
