package com.ironsword.gtportal.common.data;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.ironsword.gtportal.common.item.component.TestComponent;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.world.item.Item;

import static com.ironsword.gtportal.common.registry.GTPRegistries.REGISTRATE;

public class GTPItems {
    static {
        REGISTRATE.creativeModeTab(()-> GTPCreativeModeTabs.GTP_TAB);
    }

//    public static final ItemEntry<ComponentItem> DIM_DATA_RECORDER = REGISTRATE.item("dim_data_recorder", ComponentItem::create)
//            .initialProperties(()->new Item.Properties().stacksTo(1))
//            .lang("Dimension Data Recorder")
//            .onRegister(attach(new DimensionDataRecordComponent()))
//            .defaultModel()
//            .register();
//    public static final ItemEntry<ComponentItem> DIM_DATA_STICK = REGISTRATE.item("dim_data_stick",ComponentItem::create)
//            .initialProperties(()->new Item.Properties().stacksTo(1))
//            .lang("Dimension Data Stick")
//            .onRegister(attach(new DimensionDataComponent()))
//            .defaultModel()
//            .register();

    public static final ItemEntry<ComponentItem> TEST_ITEM = REGISTRATE.item("test_item",ComponentItem::create)
            .initialProperties(()->new Item.Properties().stacksTo(1))
            .lang("Test Item")
            .onRegister(attach(new TestComponent()))
            .defaultModel()
            .register();

    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent components) {
        return item -> item.attachComponents(components);
    }

    public static void init() {
        //REGISTRATE.creativeModeTab().get().column()
    }
}
