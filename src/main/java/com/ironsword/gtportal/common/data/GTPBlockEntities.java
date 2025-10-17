package com.ironsword.gtportal.common.data;

import com.ironsword.gtportal.common.blockentity.PortalBlockEntity;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.ironsword.gtportal.common.registry.GTPRegistries.REGISTRATE;

public class GTPBlockEntities {
    static {
        REGISTRATE.creativeModeTab(()-> GTPCreativeModeTabs.GTP_TAB);
    }

    public static final BlockEntityEntry<PortalBlockEntity> PORTAL_BLOCK = REGISTRATE.blockEntity("portal_block_entity", PortalBlockEntity::new)
            .validBlock(GTPBlocks.PORTAL_BLOCK)
            .register();

    public static void init() {}

}
