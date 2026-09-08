package com.ironsword.gtportal.api.portal.teleporter;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TeleportCacheSavedData extends SavedData {

    public static final String NAME = "gtportal_teleport_cache";

    public record PortalData(ResourceLocation dimension, BlockPos controllerPos) {}

    private final Map<PortalData, PortalData> _cache = new HashMap<>();

    public static TeleportCacheSavedData get(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(TeleportCacheSavedData::load, TeleportCacheSavedData::new, NAME);
    }

    private static TeleportCacheSavedData load(CompoundTag tag) {
        TeleportCacheSavedData data = new TeleportCacheSavedData();
        data.read(tag);
        return data;
    }

    private void read(CompoundTag tag) {
        _cache.clear();
        ListTag caches = tag.getList("caches", Tag.TAG_COMPOUND);
        for (Tag cacheTag : caches) {
            CompoundTag cache = (CompoundTag) cacheTag;

            CompoundTag source = cache.getCompound("source");
            ResourceLocation sourceDimension = ResourceLocation.tryParse(source.getString("dimension"));

            CompoundTag target = cache.getCompound("target");
            ResourceLocation targetDimension = ResourceLocation.tryParse(target.getString("dimension"));

            if (sourceDimension == null || targetDimension == null) continue;
            _cache.put(
                    new PortalData(sourceDimension, BlockPos.of(source.getLong("position"))),
                    new PortalData(targetDimension, BlockPos.of(target.getLong("position"))));
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag caches = new ListTag();
        _cache.forEach((source, destination) -> {
            CompoundTag cache = new CompoundTag();

            CompoundTag sourceTag = new CompoundTag();
            sourceTag.putString("dimension", source.dimension().toString());
            sourceTag.putLong("position", source.controllerPos().asLong());
            cache.put("source", sourceTag);

            CompoundTag targetTag = new CompoundTag();
            targetTag.putString("dimension", destination.dimension().toString());
            targetTag.putLong("position", destination.controllerPos().asLong());
            cache.put("target", targetTag);

            caches.add(cache);
        });
        tag.put("caches", caches);
        return tag;
    }

    @Nullable
    public TeleportCacheSavedData.PortalData get(PortalData sourceMachine) {
        return _cache.get(sourceMachine);
    }

    public void put(PortalData sourceMachine, PortalData targetMachine) {
        _cache.put(sourceMachine, targetMachine);
        setDirty();
    }

    public void removeAllTo(PortalData staleTarget) {
        if (_cache.values().removeIf(target -> target.equals(staleTarget))) {
            setDirty();
        }
    }
}
