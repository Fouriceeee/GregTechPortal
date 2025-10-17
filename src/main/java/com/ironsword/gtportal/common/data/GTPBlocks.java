package com.ironsword.gtportal.common.data;

import com.ironsword.gtportal.common.portal.PortalBlock;
import com.ironsword.gtportal.common.portal.SimplePortalBlock;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.ironsword.gtportal.common.registry.GTPRegistries;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;

import static com.ironsword.gtportal.common.registry.GTPRegistries.REGISTRATE;

public class GTPBlocks {
    static {
        REGISTRATE.creativeModeTab(()-> GTPCreativeModeTabs.GTP_TAB);
    }

    public static final BlockEntry<SimplePortalBlock> PORTAL_BLOCK = REGISTRATE.block("portal_block", SimplePortalBlock::new)
            .initialProperties(()-> Blocks.NETHER_PORTAL)
            .defaultBlockstate()
            .lang("Portal Block")
            .simpleItem()
            .register();

    public static void init() {}
}
