package com.ironsword.gtportal.common.data;

import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;

import static com.ironsword.gtportal.common.registry.GTPRegistries.REGISTRATE;

public class GTPBlockEntities {
    static {
        REGISTRATE.creativeModeTab(()-> GTPCreativeModeTabs.GTP_TAB);
    }

    public static void init() {}

}
