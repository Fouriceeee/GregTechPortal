package com.ironsword.gtportal.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;
import com.ironsword.gtportal.api.portal.teleporter.GTPTeleporter;
import com.ironsword.gtportal.common.data.GTPBlocks;
import com.ironsword.gtportal.common.data.GTPTags;
import com.ironsword.gtportal.common.machine.multiblock.logic.TestPortalLogic;
import com.ironsword.gtportal.utils.Utils;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;

public class TestPortalMachine extends WorkableElectricMultiblockMachine {

    public static final Pair<Block,TeleportFunction> EMPTY = Pair.of(GTPBlocks.TEST_EMPTY_PORTAL_BLOCK.get(),(entity, currWorld, destWorld, coordinate) -> {});
    public static final Map<ResourceLocation, Pair<Block,TeleportFunction>> MAP = new HashMap<>(Map.of(
            Level.OVERWORLD.location(),Pair.of(
                    GTPBlocks.TEST_OVERWORLD_PORTAL_BLOCK.get(),
                    (entity, currWorld, destWorld, coordinate) ->
                            entity.changeDimension(destWorld,new GTPTeleporter(currWorld,coordinate,Blocks.COBBLESTONE))),
            Level.NETHER.location(),Pair.of(
                    GTPBlocks.TEST_NETHER_PORTAL_BLOCK.get(),
                    (entity, currWorld, destWorld, coordinate) -> entity.changeDimension(destWorld,new GTPTeleporter(currWorld,coordinate,Blocks.NETHERRACK))),
            Level.END.location(),Pair.of(
                    GTPBlocks.TEST_END_PORTAL_BLOCK.get(),
                    (entity, currWorld, destWorld, coordinate) -> {
                        if (coordinate == null){
                            entity.changeDimension(destWorld);
                        }else {
                            entity.changeDimension(destWorld,new GTPTeleporter(currWorld,coordinate,Blocks.END_STONE));
                        }
                    }
            )
    ));
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(TestPortalMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Nonnull
    protected Pair<ResourceLocation, Vec3i> cache = Pair.of(null,null);

    protected TickableSubscription teleportSubscription;

    public TestPortalMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static ManagedFieldHolder getManagedFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new TestPortalLogic(this);
    }

    public TestPortalLogic getRecipePortalLogic(){
        return (TestPortalLogic) getRecipeLogic();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onUnload() {
        super.onUnload();
        unsubscribe(teleportSubscription);
        teleportSubscription = null;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        teleportSubscription = subscribeServerTick(teleportSubscription,this::teleportEntities);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        unsubscribe(teleportSubscription);
        teleportSubscription = null;
        destroyBlock();
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        super.setWorkingEnabled(isWorkingAllowed);
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe == null) return false;
        if (!super.beforeWorking(recipe)) return false;

        ResourceLocation dimension = new ResourceLocation(recipe.data.getString("dimension"));
        if (getLevel().dimension().location().equals(dimension)) return false;
        cache = Pair.of(dimension,null);
        fillBlock(MAP.getOrDefault(cache.getFirst(),EMPTY).getFirst().defaultBlockState().setValue(BlockStateProperties.AXIS,getFrontFacing().getAxis()));
        return true;
    }

    @Override
    public void afterWorking() {
        fillBlock(Blocks.AIR.defaultBlockState());
        clearCache();
        super.afterWorking();
    }

    @Override
    public void onWaiting() {
        fillBlock(Blocks.AIR.defaultBlockState());
        super.onWaiting();
    }

    public void clearCache(){
        cache = Pair.of(null,null);
    }

    public Set<BlockPos> getBlockPoses(){
        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());

        return Set.of(getPos().relative(up),getPos().relative(up,2));
    }

    public BlockState getPortalBlockState(){
        var pair = MAP.get(cache.getFirst());
        return pair == null ? Blocks.AIR.defaultBlockState() : pair.getFirst().defaultBlockState().setValue(BlockStateProperties.AXIS,getFrontFacing().getAxis());
    }

    public void fillBlock(BlockState blockState){
        if (!(getLevel()instanceof ServerLevel)) return;
        for (var pos:getBlockPoses()){
            getLevel().setBlockAndUpdate(pos,blockState);
        }
    }

    //block true , check true  -> destroy
    //block false, check true  -> continue
    //block true , check false -> destroy
    //block false, check false -> destroy
    public void destroyBlock(Block block, boolean checkBlock){
        if (!(getLevel()instanceof ServerLevel)) return;
        for (var pos:getBlockPoses()){
            if (!checkBlock || getLevel().getBlockState(pos).is(block)){
                getLevel().destroyBlock(pos,false);
            }
        }
    }

    public void destroyBlock(){
        if (!(getLevel()instanceof ServerLevel)) return;
        for (var pos:getBlockPoses()){
            if (getLevel().getBlockState(pos).is(GTPTags.PORTAL)){
                getLevel().destroyBlock(pos,false);
            }
        }
    }

    public void destroyBlock(boolean checkBlock){
        this.destroyBlock(GTPBlocks.DIMENSIONAL_PORTAL_BLOCK.get(),checkBlock);
    }

    protected void teleportEntities(){
        if (!(getLevel() instanceof ServerLevel)||!getRecipeLogic().isWorking())
            return;

        ResourceLocation dimension = cache.getFirst();

        if (dimension == null || dimension.equals(getLevel().dimension().location()))
            return;

        ServerLevel serverLevel = ((ServerLevel) getLevel()).getServer().getLevel(ResourceKey.create(Registries.DIMENSION,dimension));
        if (serverLevel == null)
            return;

        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());

        BlockPos startingPos = getPos().relative(up),
                endingPos = getPos().relative(up,2);

        getLevel().getEntities(null, Utils.getMaxBox(startingPos,endingPos)).forEach(e->{
            if (!(e instanceof Entity) ||!e.canChangeDimensions())
                return;

            MAP.getOrDefault(dimension,EMPTY).getSecond().teleport(e,(ServerLevel) getLevel(),serverLevel,cache.getSecond());
        });
    }

    @FunctionalInterface
    public interface TeleportFunction{
        void teleport(Entity entity, ServerLevel currWorld, ServerLevel destWorld, @Nullable Vec3i coordinate);
    }
}



