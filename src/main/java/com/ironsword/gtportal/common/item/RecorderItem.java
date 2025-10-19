package com.ironsword.gtportal.common.item;

import com.ironsword.gtportal.api.portal.PosData;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RecorderItem extends Item {
    public RecorderItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack recorder = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer.isShiftKeyDown()){
            PosData data = new PosData(pLevel.dimension().location(),pPlayer.blockPosition());

            if (recorder.getTagElement("posData")!=null){
                recorder.removeTagKey("posData");
            }
            recorder.addTagElement("posData",data.toNbt());
            pPlayer.displayClientMessage(Component.literal(data.toString()),true);

            return InteractionResultHolder.success(recorder);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        CompoundTag data = pStack.getTagElement("posData");
        if (data!=null) {
            pTooltipComponents.add(Component.literal(PosData.fromNbt(data).toString()));
        }
    }


}
