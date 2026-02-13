package com.ironsword.gtportal.common.registry;

import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.ironsword.gtportal.GTPortal;
import com.ironsword.gtportal.common.data.GTPItems;
import com.ironsword.gtportal.common.data.GTPMachines;
import com.lowdragmc.lowdraglib.LDLib;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import twilightforest.world.registration.TFGenerationSettings;

import static com.ironsword.gtportal.common.item.component.TestComponent.putDimensionNbt;

public class GTPCreativeModeTabs {
    public static RegistryEntry<CreativeModeTab> GTP_TAB = GTPRegistries.REGISTRATE.defaultCreativeTab(
            GTPortal.MODID,
            builder -> builder.displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(GTPortal.MODID,GTPRegistries.REGISTRATE))
                    .icon(GTPMachines.TEST_MACHINE::asStack)
                    .title(Component.translatable("gtportal.creativemodetab.main"))
                    .build()
    ).register();

    public static RegistryEntry<CreativeModeTab> DATA_STICK_TAB = GTPRegistries.REGISTRATE.defaultCreativeTab(
            "dim_data_sticks",
            builder -> builder
                    .displayItems((var1,var2)->{
                        var2.accept(GTPItems.TEST_ITEM.asStack());
                        var2.accept(putDimensionNbt(GTPItems.TEST_ITEM.asStack(), Level.OVERWORLD.location()));
                        var2.accept(putDimensionNbt(GTPItems.TEST_ITEM.asStack(), Level.NETHER.location()));
                        var2.accept(putDimensionNbt(GTPItems.TEST_ITEM.asStack(), Level.END.location()));
                        if (LDLib.isModLoaded("aether")){
                            var2.accept(putDimensionNbt(GTPItems.TEST_ITEM.asStack(), AetherDimensions.AETHER_LEVEL.location()));
                        }
                        if (LDLib.isModLoaded("twilightforest")){
                            var2.accept(putDimensionNbt(GTPItems.TEST_ITEM.asStack(), TFGenerationSettings.DIMENSION));
                        }
                    })
                    .icon(GTPItems.TEST_ITEM::asStack)
                    .title(Component.translatable("gtportal.creativemodetab.dim_data_sticks"))
                    .build()
    ).register();

    public static void init() {

    }
}
