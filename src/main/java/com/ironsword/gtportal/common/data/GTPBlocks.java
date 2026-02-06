package com.ironsword.gtportal.common.data;

import com.aetherteam.aether.Aether;
import com.gregtechceu.gtceu.common.data.models.GTModels;
import com.ironsword.gtportal.GTPortal;
import com.ironsword.gtportal.api.portal.DimensionInfo;
import com.ironsword.gtportal.common.block.BrokenEndPortalFrameBlock;
import com.ironsword.gtportal.common.block.DimensionalPortalBlock;
import com.ironsword.gtportal.common.registry.GTPCreativeModeTabs;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder;

import static com.ironsword.gtportal.common.registry.GTPRegistries.REGISTRATE;

public class GTPBlocks {

    public static final ResourceLocation
            EMPTY_TEXTURE = new ResourceLocation(GTPortal.MODID,"block/portals/empty_portal"),
            OVERWORLD_TEXTURE = new ResourceLocation(GTPortal.MODID,"block/portals/overworld_portal"),
            NETHER_TEXTURE = new ResourceLocation(GTPortal.MODID,"block/portals/nether_portal"),
            END_TEXTURE = new ResourceLocation(GTPortal.MODID,"block/portals/end_portal"),
            AETHER_TEXTURE = new ResourceLocation(Aether.MODID,"block/miscellaneous/aether_portal"),
            TWILIGHT_TEXTURE = new ResourceLocation("minecraft","block/nether_portal");


    static {
        REGISTRATE.creativeModeTab(()-> GTPCreativeModeTabs.GTP_TAB);
    }

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

    public static final BlockEntry<BrokenEndPortalFrameBlock> BROKEN_END_PORTAL_FRAME = REGISTRATE
            .block("broken_end_portal_frame",BrokenEndPortalFrameBlock::new)
            .initialProperties(()->Blocks.END_STONE)
            .lang("Broken End Portal Frame")
            .blockstate((ctx,prov)->{
                ModelBuilder<?> model = prov.models().getBuilder("broken_end_portal_frame").parent(prov.models().getExistingFile(new ResourceLocation("minecraft:block/block")));
                model.element()
                        .from(0,0,0).to(16,13,16)
                        .face(Direction.DOWN).uvs(0,0,16,16).texture("#bottom").cullface(Direction.DOWN).end()
                        .face(Direction.UP).uvs(0,0,16,16).texture("#top").end()
                        .face(Direction.NORTH).uvs(0,3,16,16).texture("#side").cullface(Direction.NORTH).end()
                        .face(Direction.SOUTH).uvs(0,3,16,16).texture("#side").cullface(Direction.SOUTH).end()
                        .face(Direction.WEST).uvs(0,3,16,16).texture("#side").cullface(Direction.WEST).end()
                        .face(Direction.EAST).uvs(0,3,16,16).texture("#side").cullface(Direction.EAST).end()
                        .end();
                model.texture("bottom",new ResourceLocation(GTPortal.MODID,"block/broken_end_portal_frame/bottom"))
                        .texture("top",new ResourceLocation(GTPortal.MODID,"block/broken_end_portal_frame/top"))
                        .texture("side",new ResourceLocation(GTPortal.MODID,"block/broken_end_portal_frame/side"))
                        .texture("particle",new ResourceLocation(GTPortal.MODID,"block/broken_end_portal_frame/bottom"));
                prov.getVariantBuilder(ctx.getEntry()).partialState().setModels(ConfiguredModel.builder().modelFile(model).build());
            })
            .simpleItem()
            .register();

    public static final BlockEntry<Block> PORTAL_FRAME = REGISTRATE
            .block("portal_frame",Block::new)
            .initialProperties(()->Blocks.OBSIDIAN)
            .addLayer(()->RenderType::solid)
            .lang("Portal Frame")
            .exBlockstate(GTModels.cubeAllModel(GTPortal.id("block/portal_frame")))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> SIMPLE_OVERWORLD_PORTAL_FRAME = REGISTRATE
            .block("simple_overworld_portal_frame",Block::new)
            .initialProperties(()->Blocks.OBSIDIAN)
            .addLayer(()->RenderType::solid)
            .lang("Simple Overworld Portal Frame")
            .exBlockstate(GTModels.cubeAllModel(GTPortal.id("block/simple_overworld_portal_frame")))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> SIMPLE_NETHER_PORTAL_FRAME = REGISTRATE
            .block("simple_nether_portal_frame",Block::new)
            .initialProperties(()->Blocks.OBSIDIAN)
            .addLayer(()->RenderType::solid)
            .lang("Simple Nether Portal Frame")
            .exBlockstate(GTModels.cubeAllModel(GTPortal.id("block/simple_nether_portal_frame")))
            .simpleItem()
            .register();

    public static void init() {}


}
