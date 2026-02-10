package com.ironsword.gtportal.common.item.component;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestComponent implements IAddInformation {
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("dimension")){
            tooltipComponents.add(Component.literal("dimension-%s".formatted(tag.getString("dimension"))));
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
}
