package com.ironsword.gtportal;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;

@Config(id = GTPortal.MODID)
public class GTPConfigHolder {

    public static GTPConfigHolder INSTANCE;
    private static final Object LOCK = new Object();

    public static void init() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = Configuration.registerConfig(GTPConfigHolder.class, ConfigFormats.yaml()).getConfigInstance();
            }
        }
    }

    @Configurable
    public PortalGateConfigs portalGateConfigs = new PortalGateConfigs();

    public static class PortalGateConfigs {

        @Configurable
        @Configurable.Comment({"Whether to allow players to build vanilla Nether portal gate.","Default: false" })
        public boolean allowVanillaNetherPortalGate = false;

        @Configurable
        @Configurable.Comment({"Whether to allow worlds to generate vanilla End portal frame.","Default: false" })
        public boolean generateVanillaEndPortalFrame = false;

        @Configurable
        @Configurable.Comment({"Whether to allow players to build normal Twilight portal gate.","Default: false" })
        public boolean allowVanillaTwilightForestPortalGate = false;

        @Configurable
        @Configurable.Comment({"Whether to allow players to build normal Aether portal gate.","Default: false" })
        public boolean allowVanillaAetherPortalGate = false;
    }
}
