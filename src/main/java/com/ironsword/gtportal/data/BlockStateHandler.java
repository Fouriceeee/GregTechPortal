package com.ironsword.gtportal.data;

import com.gregtechceu.gtceu.api.registry.registrate.provider.GTBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;

public class BlockStateHandler {
    public static void init(GTBlockstateProvider provider){
        BlockModelProvider models = provider.models();
        final ModelFile BLOCK = models.getExistingFile(new ResourceLocation("minecraft:block/block"));
        models.getBuilder("block/portal/parent/portal_x").parent(BLOCK)
                .element().from(6,0,0).to(10,16,16)
                .face(Direction.EAST).texture("#portal").end()
                .face(Direction.WEST).texture("#portal").end()
                .end();
        models.getBuilder("block/portal/parent/portal_y").parent(BLOCK)
                .element().from(0,6,0).to(16,10,16)
                .face(Direction.UP).texture("#portal").end()
                .face(Direction.DOWN).texture("#portal").end()
                .end();
        models.getBuilder("block/portal/parent/portal_z").parent(BLOCK)
                .element().from(0,0,6).to(16,16,10)
                .face(Direction.NORTH).texture("#portal").end()
                .face(Direction.SOUTH).texture("#portal").end()
                .end();
    }
}
