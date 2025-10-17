package com.ironsword.gtportal.common.registry;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.ironsword.gtportal.GTPortal;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class GTPCreativeModeTabs {
    public static RegistryEntry<CreativeModeTab> GTP_TAB = GTPRegistries.REGISTRATE.defaultCreativeTab(
            GTPortal.MODID,
            builder -> builder.displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(GTPortal.MODID,GTPRegistries.REGISTRATE))
                    .icon(()->new ItemStack(Blocks.OBSIDIAN))
                    .title(Component.literal("GTPortal"))
                    .build()
    ).register();

    public static void init() {}
}
