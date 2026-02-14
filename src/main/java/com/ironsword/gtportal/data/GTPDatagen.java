package com.ironsword.gtportal.data;

import com.gregtechceu.gtceu.api.registry.registrate.provider.GTBlockstateProvider;
import com.ironsword.gtportal.common.registry.GTPRegistries;
import com.tterrag.registrate.providers.ProviderType;

public class GTPDatagen {
    public static void initPre(){
        GTPRegistries.REGISTRATE.addDataGenerator(ProviderType.BLOCKSTATE,p->BlockStateHandler.init((GTBlockstateProvider) p));
        GTPRegistries.REGISTRATE.addDataGenerator(ProviderType.LANG,LangHandler::init);
    }

    public static void initPost(){
        //GTPRegistries.REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, BlockTagLoader::init);
    }
}
