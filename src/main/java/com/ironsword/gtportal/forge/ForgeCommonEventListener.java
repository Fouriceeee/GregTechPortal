package com.ironsword.gtportal.forge;

import com.ironsword.gtportal.GTPortal;
import com.ironsword.gtportal.common.machine.multiblock.PortalControllerMachine;
import com.ironsword.gtportal.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GTPortal.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeCommonEventListener {
    @SubscribeEvent
    public static void onPortalIgnition(BlockEvent.PortalSpawnEvent event) {
        if (event.getLevel() instanceof Level level){
            Utils.displayMessageInBoxes(level,event.getPos(),5,Component.literal("门不能从这一侧打开"));
        }
        event.setCanceled(true);
    }
}
