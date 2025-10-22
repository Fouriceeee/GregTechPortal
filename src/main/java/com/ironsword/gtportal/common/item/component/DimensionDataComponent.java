package com.ironsword.gtportal.common.item.component;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.ironsword.gtportal.api.portal.DimensionData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DimensionDataComponent implements IAddInformation {
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        if (stack.getOrCreateTag().contains("dim_data")){
            DimensionData dimData = DimensionData.fromNbt(stack.getOrCreateTag().getCompound("dim_data"));
            tooltipComponents.add(Component.literal(dimData.toString()));
        }
    }

    public static void changeDimensionData(ItemStack stack, DimensionData dimData){
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("dim_data")){
            tag.remove("dim_data");
        }
        tag.put("dim_data",dimData.toNbt());
    }
}
