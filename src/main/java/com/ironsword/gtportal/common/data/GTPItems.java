package com.ironsword.gtportal.common.data;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.ironsword.gtportal.common.item.component.DimensionDataComponent;
import com.ironsword.gtportal.common.item.component.DimensionDataRecordComponent;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.world.item.Item;

import static com.ironsword.gtportal.common.registry.GTPRegistries.REGISTRATE;

public class GTPItems {
    static {
        REGISTRATE.creativeModeTab(()-> GTPCreativeModeTabs.GTP_TAB);
    }

    public static final ItemEntry<ComponentItem> RECORDER = REGISTRATE.item("recorder", ComponentItem::create)
            .initialProperties(()->new Item.Properties().stacksTo(1))
            .lang("Recorder")
            .onRegister(attach(new DimensionDataRecordComponent()))
            .defaultModel()
            .register();
    public static final ItemEntry<ComponentItem> DIM_RECORDER = REGISTRATE.item("dimension_data_recorder",ComponentItem::create)
            .initialProperties(()->new Item.Properties().stacksTo(1))
            .lang("Dimension Data Recorder")
            .onRegister(attach(new DimensionDataComponent()))
            .defaultModel()
            .register();

    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent components) {
        return item -> item.attachComponents(components);
    }

    public static void init() {

    }
}
