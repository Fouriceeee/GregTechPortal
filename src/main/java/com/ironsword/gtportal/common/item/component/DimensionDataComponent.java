package com.ironsword.gtportal.common.item.component;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class DimensionDataComponent implements IAddInformation {
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("dimension")){
            tooltipComponents.add(Component.literal("dimension-%s".formatted(tag.getString("dimension"))));
        }
        if (tag.contains("coordinate")){
            tooltipComponents.add(Component.literal("coordinate-%s".formatted(Arrays.toString(tag.getIntArray("coordinate")))));
        }
    }

    @Nullable
    public static ResourceLocation dimensionFromNbt(CompoundTag tag){
        return tag.contains("dimension") ? new ResourceLocation(tag.getString("dimension")) : null;
    }

    @Nullable
    public static Vec3i coordinateFromNbt(CompoundTag tag) {
        if (tag.contains("coordinate") || tag.getIntArray("coordinate").length == 3){
            var array = tag.getIntArray("coordinate");
            return new Vec3i(array[0],array[1],array[2]);
        }
        return null;
    }

    public static ItemStack putDimensionNbt(ItemStack item, ResourceLocation dimension){
        CompoundTag tag = item.getOrCreateTag();
        if (tag.contains("dimension")) {
            tag.remove("dimension");
        }
        tag.putString("dimension",dimension.toString());
        return item;
    }

    public static ItemStack putCoordinateNbt(ItemStack item, Vec3i coordinate){
        CompoundTag tag = item.getOrCreateTag();
        if (tag.contains("coordinate")) {
            tag.remove("coordinate");
        }
        tag.putIntArray("coordinate", new int[]{coordinate.getX(), coordinate.getY(), coordinate.getZ()});
        return item;
    }
}
