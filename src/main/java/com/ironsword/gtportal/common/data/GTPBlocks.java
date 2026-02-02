package com.ironsword.gtportal.common.data;

import com.aetherteam.aether.Aether;
import com.ironsword.gtportal.GTPortal;
import com.ironsword.gtportal.api.portal.DimensionInfo;
import com.ironsword.gtportal.common.block.DimensionalPortalBlock;
import com.ironsword.gtportal.common.block.PortalBlock;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

import static com.ironsword.gtportal.common.registry.GTPRegistries.REGISTRATE;

public class GTPBlocks {
    static {
        REGISTRATE.creativeModeTab(()-> GTPCreativeModeTabs.GTP_TAB);
    }

    public static final BlockEntry<PortalBlock> PORTAL_BLOCK = REGISTRATE
            .block("portal_block",PortalBlock::new)
            .initialProperties(()-> Blocks.NETHER_PORTAL)
            .addLayer(()-> RenderType::translucent)
            .lang("Portal Block")
            .blockstate((ctx,prov)->
                prov.getVariantBuilder(ctx.getEntry())
                        .forAllStates(state->{
                            ModelFile parent = prov.models().getExistingFile(prov.modLoc("block/portals/portal_"+state.getValue(BlockStateProperties.AXIS).getName()));
                            ModelBuilder<?> model = prov.models().getBuilder("block/portal_"+state.getValue(BlockStateProperties.AXIS).getName()).parent(parent);
                            model.texture("portal","block/"+ctx.getName()).texture("particle","block/"+ctx.getName());
                            return ConfiguredModel.builder().modelFile(model).build();
                        }))
            .register();

    public static final BlockEntry<DimensionalPortalBlock> DIMENSIONAL_PORTAL_BLOCK = REGISTRATE
            .block("dimensional_portal_block",DimensionalPortalBlock::new)
            .initialProperties(()->Blocks.NETHER_PORTAL)
            .addLayer(()-> RenderType::translucent)
            .lang("Dimensional Portal Block")
            .blockstate((ctx,prov)->{
                prov.getVariantBuilder(ctx.getEntry())
                        .forAllStates(state -> {
                            Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
                            DimensionInfo info = state.getValue(DimensionalPortalBlock.DIMENSIONS);

                            ModelBuilder<?> model = prov.models().getBuilder(info.getSerializedName()+"_portal_"+axis.getName()).parent(prov.models().getExistingFile(new ResourceLocation("minecraft:block/block")));
                            switch (axis.getName()){
                                case "x":
                                    model.element().from(6,0,0).to(10,16,16)
                                            .face(Direction.EAST).uvs(0,0,16,16).texture("#portal").end()
                                            .face(Direction.WEST).uvs(0,0,16,16).texture("#portal").end()
                                            .end();
                                    break;
                                case "y":
                                    model.element().from(0,6,0).to(16,10,16)
                                            .face(Direction.UP).uvs(0,0,16,16).texture("#portal").end()
                                            .face(Direction.DOWN).uvs(0,0,16,16).texture("#portal").end()
                                            .end();
                                    break;
                                case "z":
                                    model.element().from(0,0,6).to(16,16,10)
                                            .face(Direction.NORTH).uvs(0,0,16,16).texture("#portal").end()
                                            .face(Direction.SOUTH).uvs(0,0,16,16).texture("#portal").end()
                                            .end();
                                    break;
                                default:
                                    break;
                            }
                            model.texture("portal",info.getTexture()).texture("particle",info.getTexture());
                            return ConfiguredModel.builder().modelFile(model).build();
                        });
            })
            .register();

    public static void init() {}


}
