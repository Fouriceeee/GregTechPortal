package com.ironsword.gtportal.data;

import com.gregtechceu.gtceu.api.registry.registrate.provider.GTBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.generators.BlockModelProvider;

public class BlockStateGen {
    public static void init(GTBlockstateProvider provider){
        BlockModelProvider models = provider.models();

        models.withExistingParent("block/portals/portal_x","block/block")
                .element().from(6,0,0).to(10,16,16)
                .face(Direction.EAST).uvs(0,0,16,16).texture("#portal").end()
                .face(Direction.WEST).uvs(0,0,16,16).texture("#portal").end()
                .end();
        models.withExistingParent("block/portals/portal_y","block/block")
                .element().from(0,6,0).to(16,10,16)
                .face(Direction.UP).uvs(0,0,16,16).texture("#portal").end()
                .face(Direction.DOWN).uvs(0,0,16,16).texture("#portal").end()
                .end();
        models.withExistingParent("block/portals/portal_z","block/block")
                .element().from(0,0,6).to(16,16,10)
                .face(Direction.NORTH).uvs(0,0,16,16).texture("#portal").end()
                .face(Direction.SOUTH).uvs(0,0,16,16).texture("#portal").end()
                .end();
    }
}
