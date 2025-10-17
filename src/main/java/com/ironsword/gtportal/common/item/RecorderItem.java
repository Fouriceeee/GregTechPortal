package com.ironsword.gtportal.common.item;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

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

    public record PosData(ResourceLocation dimension, Vec3i pos){
        public CompoundTag toNbt(){
            return Util.make(new CompoundTag(),tag->{
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
}
