package com.ironsword.gtportal.common.registry;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.ironsword.gtportal.GTPortal;
import com.ironsword.gtportal.api.portal.DimensionInfo;
import com.ironsword.gtportal.common.data.GTPItems;
import com.ironsword.gtportal.common.data.GTPMachines;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class GTPCreativeModeTabs {
    public static RegistryEntry<CreativeModeTab> GTP_TAB = GTPRegistries.REGISTRATE.defaultCreativeTab(
            GTPortal.MODID,
            builder -> builder.displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(GTPortal.MODID,GTPRegistries.REGISTRATE))
                    .icon(GTPMachines.PORTAL_CONTROLLER::asStack)
                    .title(Component.translatable("gtportal.creativemodetab.main"))
                    .build()
    ).register();

    public static RegistryEntry<CreativeModeTab> DATA_STICK_TAB = GTPRegistries.REGISTRATE.defaultCreativeTab(
            "dim_data_sticks",
            builder -> builder
                    .displayItems((var1,var2)->{
                        var2.accept(GTPItems.DIM_DATA_STICK.asStack());
                        for (var info: DimensionInfo.values()){
                           if (info.equals(DimensionInfo.EMPTY)) continue;
                           ItemStack item = GTPItems.DIM_DATA_STICK.asStack();
                           CompoundTag tag = item.getOrCreateTag();
                            tag.put("dim_data",info.toNbt());
                            var2.accept(item);
                        }})
                    .icon(GTPItems.DIM_DATA_STICK::asStack)
                    .title(Component.translatable("gtportal.creativemodetab.dim_data_sticks"))
                    .build()
    ).register();

    public static void init() {

    }
}
