package com.ironsword.gtportal.common.data;

import com.ironsword.gtportal.common.item.RecorderItem;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

import static com.ironsword.gtportal.common.registry.GTPRegistries.REGISTRATE;

public class GTPItems {
    static {
        REGISTRATE.creativeModeTab(()-> GTPCreativeModeTabs.GTP_TAB);
    }

    public static final ItemEntry<RecorderItem> RECORDER = REGISTRATE.item("recorder", RecorderItem::new)
            .initialProperties(()->new Item.Properties().stacksTo(1))
            .lang("Recorder")
            .defaultModel()
            .register();

    public static void init() {}
}
