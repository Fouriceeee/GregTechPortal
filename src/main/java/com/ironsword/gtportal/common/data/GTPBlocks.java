package com.ironsword.gtportal.common.data;

import com.ironsword.gtportal.common.block.PortalBlock;
import com.ironsword.gtportal.common.block.SimplePortalBlock;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;

import static com.ironsword.gtportal.common.registry.GTPRegistries.REGISTRATE;

public class GTPBlocks {
    static {
        REGISTRATE.creativeModeTab(()-> GTPCreativeModeTabs.GTP_TAB);
    }

    public static final BlockEntry<SimplePortalBlock> NETHER_PORTAL_BLOCK = REGISTRATE.block("nether_portal_block", SimplePortalBlock::new)
            .initialProperties(()-> Blocks.NETHER_PORTAL)
            .defaultBlockstate()
            .addLayer(()-> RenderType::translucent)
            .lang("Nether Portal Block")
            .simpleItem()
            .register();

    public static final BlockEntry<PortalBlock> PORTAL_BLOCK = REGISTRATE.block("portal_block", PortalBlock::new)
            .initialProperties(()-> Blocks.NETHER_PORTAL)
            .defaultBlockstate()
            .addLayer(()-> RenderType::translucent)
            .lang("Portal Block")
            .simpleItem()
            .register();

    public static void init() {}
}
