package com.ironsword.gtportal.client;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import com.ironsword.gtportal.GTPortal;
import com.ironsword.gtportal.client.renderer.PortalBlockRenderer;
import com.ironsword.gtportal.common.CommonProxy;

public class ClientProxy extends CommonProxy {

    public ClientProxy(){
        super();
        init();
    }

    public static void init(){
        DynamicRenderManager.register(GTPortal.id("portal_block"), PortalBlockRenderer.TYPE);
    }
}
