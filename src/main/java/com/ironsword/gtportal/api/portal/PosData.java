package com.ironsword.gtportal.api.portal;

import com.ironsword.gtportal.common.item.RecorderItem;
import net.minecraft.Util;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public record PosData(ResourceLocation dimension, Vec3i pos){
    public CompoundTag toNbt(){
        return Util.make(new CompoundTag(), tag->{
            tag.putString("dimension",dimension.toString());
            tag.putInt("x",pos.getX());
            tag.putInt("y",pos.getY());
            tag.putInt("z",pos.getZ());
        });
    }

    public String toString(){
        return "Dimension: "+dimension.toString()+" Position: "+pos.getX()+", "+pos.getY()+", "+pos.getZ();
    }

    public static PosData fromNbt(CompoundTag tag){
        ResourceLocation dimension = new ResourceLocation(tag.getString("dimension"));
        Vec3i pos = new Vec3i(tag.getInt("x"),tag.getInt("y"),tag.getInt("z"));
        return new PosData(dimension,pos);
    }

    public ServerLevel getLevel(MinecraftServer server) {
        return server.getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
    }
}