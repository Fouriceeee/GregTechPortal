package com.ironsword.gtportal.forge;

import com.ironsword.gtportal.GTPConfigHolder;
import com.ironsword.gtportal.GTPortal;
import com.ironsword.gtportal.utils.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GTPortal.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeCommonEventListener {
    @SubscribeEvent
    public static void onPortalIgnition(BlockEvent.PortalSpawnEvent event) {
        if (GTPConfigHolder.INSTANCE.portalGateConfigs.allowVanillaNetherPortalGate){
            return;
        }

        if (event.getLevel() instanceof Level level){
            Utils.displayMessageInBoxes(level,event.getPos(),5,Component.translatable("gtportal.clientmessage.banned_structure"));
        }
        event.setCanceled(true);
    }
}
