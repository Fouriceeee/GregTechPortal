package com.ironsword.gtportal.data;

import com.ironsword.gtportal.common.registry.GTPRegistries;
import com.tterrag.registrate.providers.ProviderType;

public class GTPDatagen {
    public static void initPre(){
        GTPRegistries.REGISTRATE.addDataGenerator(ProviderType.LANG,LangHandler::init);
    }
}
