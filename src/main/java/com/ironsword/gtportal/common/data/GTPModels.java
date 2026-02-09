package com.ironsword.gtportal.common.data;

import com.ironsword.gtportal.GTPortal;
import com.ironsword.gtportal.common.block.TestPortalBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder;

public class GTPModels {

    public static NonNullBiConsumer<DataGenContext<Block, TestPortalBlock>, RegistrateBlockstateProvider> createPortalBlockModel(String id, ResourceLocation texture){
        return (ctx,prov)->{
          prov.getVariantBuilder(ctx.getEntry())
                  .forAllStates(state->{
                      Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
                      ModelBuilder<?> model = prov.models()
                              .withExistingParent(
                                      "block/portal/%s_%s".formatted(id,axis.getName()),
                                      GTPortal.id("block/portal/parent/portal_%s".formatted(axis.getName())))
                              .texture("portal",texture)
                              .texture("particle",texture);
                      return ConfiguredModel.builder().modelFile(model).build();
                  });
        };
    }
}
