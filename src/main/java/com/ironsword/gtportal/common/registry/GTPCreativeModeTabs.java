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

import static com.ironsword.gtportal.common.item.component.DimensionDataComponent.putDimensionNbt;

public class GTPCreativeModeTabs {
    public static RegistryEntry<CreativeModeTab> GTP_TAB = GTPRegistries.REGISTRATE.defaultCreativeTab(
            GTPortal.MODID,
            builder -> builder.displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(GTPortal.MODID,GTPRegistries.REGISTRATE))
                    .icon(GTPMachines.MULTIDIMENSIONAL_PORTAL_CONTROLLER::asStack)
                    .title(Component.translatable("gtportal.creativemodetab.main"))
                    .build()
    ).register();

    public static RegistryEntry<CreativeModeTab> DATA_STICK_TAB = GTPRegistries.REGISTRATE.defaultCreativeTab(
            "dimension_data_sticks",
            builder -> builder
                    .displayItems((var1,var2)->{
                        var2.accept(GTPItems.DIMENSION_DATA_STICK.asStack());
                        var2.accept(putDimensionNbt(GTPItems.DIMENSION_DATA_STICK.asStack(), Level.OVERWORLD.location()));
                        var2.accept(putDimensionNbt(GTPItems.DIMENSION_DATA_STICK.asStack(), Level.NETHER.location()));
                        var2.accept(putDimensionNbt(GTPItems.DIMENSION_DATA_STICK.asStack(), Level.END.location()));
                        if (LDLib.isModLoaded("aether")){
                            var2.accept(putDimensionNbt(GTPItems.DIMENSION_DATA_STICK.asStack(), AetherDimensions.AETHER_LEVEL.location()));
                        }
                        if (LDLib.isModLoaded("twilightforest")){
                            var2.accept(putDimensionNbt(GTPItems.DIMENSION_DATA_STICK.asStack(), TFGenerationSettings.DIMENSION));
                        }
                    })
                    .icon(GTPItems.DIMENSION_DATA_STICK::asStack)
                    .title(Component.translatable("gtportal.creativemodetab.dimension_data_sticks"))
                    .build()
    ).register();

    public static void init() {

    }
}
