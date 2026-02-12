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
import net.minecraft.world.level.Level;

import static com.ironsword.gtportal.common.item.component.TestComponent.putDimensionNbt;

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
                        var2.accept(GTPItems.TEST_ITEM.asStack());
                        var2.accept(putDimensionNbt(GTPItems.TEST_ITEM.asStack(),Level.OVERWORLD.location()));
                        var2.accept(putDimensionNbt(GTPItems.TEST_ITEM.asStack(),Level.NETHER.location()));
                        var2.accept(putDimensionNbt(GTPItems.TEST_ITEM.asStack(),Level.END.location()));
                    })
                    .icon(GTPItems.DIM_DATA_STICK::asStack)
                    .title(Component.translatable("gtportal.creativemodetab.dim_data_sticks"))
                    .build()
    ).register();

    public static void init() {

    }
}
