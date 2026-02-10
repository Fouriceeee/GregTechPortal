package com.ironsword.gtportal.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.ironsword.gtportal.api.portal.teleporter.GTPTeleporter;
import com.ironsword.gtportal.common.data.GTPBlocks;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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

    @Nullable
    protected EnergyContainerList inputEnergyContainers;

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
        initAbilities();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        unsubscribe(teleportSubscription);
        teleportSubscription = null;
        this.inputEnergyContainers = null;
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        super.setWorkingEnabled(isWorkingAllowed);
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        return super.beforeWorking(recipe);
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
    }

    protected void initAbilities(){
        List<IEnergyContainer> energyContainers = new ArrayList<>();
        Long2ObjectMap<IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap",
                Long2ObjectMaps::emptyMap);
        for (IMultiPart part:getParts()){
            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.BOTH);
            if (io == IO.NONE || io == IO.OUT) continue;
            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                if (!handlerList.isValid(io)) continue;
                handlerList.getCapability(EURecipeCapability.CAP).stream()
                        .filter(IEnergyContainer.class::isInstance)
                        .map(IEnergyContainer.class::cast)
                        .forEach(energyContainers::add);
            }
        }
        this.inputEnergyContainers = new EnergyContainerList(energyContainers);
        getRecipePortalLogic().setEnergyContainer(this.inputEnergyContainers);
    }

    public void refreshCache(){
//        for (var part:getParts()){
//            if (part instanceof TestHatchMachine hatch){
//                cache = hatch.readNbtFromItem();
//            }
//        }
//        clearCache();
    }

    protected void clearCache(){
        cache = Pair.of(null,null);
    }

    public Set<BlockPos> getOffsets() {
        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction clockWise = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction counterClockWise = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(),
                isFlipped());

        BlockPos pos = getPos();
        BlockPos center = pos;

        Set<BlockPos> offsets = new HashSet<>();

        offsets.add(center.relative(up).subtract(pos));
        offsets.add(center.relative(up,2).subtract(pos));

//        for (int i=0;i<3;i++){
//            center = center.relative(up);
//            offsets.add(center.subtract(pos));
//            offsets.add(center.relative(clockWise).subtract(pos));
//            offsets.add(center.relative(counterClockWise).subtract(pos));
//        }

        return offsets;
    }

    protected void fillBlock(BlockState blockState){
        if (!(getLevel()instanceof ServerLevel)) return;
        for (var offset:getOffsets()){
            getLevel().setBlockAndUpdate(
                    getPos().offset(offset),
                    blockState
            );
        }
    }

    //block true , check true  -> destroy
    //block false, check true  -> continue
    //block true , check false -> destroy
    //block false, check false -> destroy
    protected void destroyBlock(Block block, boolean checkBlock){
        if (!(getLevel()instanceof ServerLevel)) return;
        for (var offset:getOffsets()){
            BlockPos pos = getPos().offset(offset);
            if (!checkBlock || getLevel().getBlockState(pos).is(block)){
                getLevel().destroyBlock(pos,false);
            }
        }
    }

    protected void destroyBlock(boolean checkBlock){
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
        Direction clockWise = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction counterClockWise = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(),
                isFlipped());
//        BlockPos startingPos = getPos().relative(up).relative(clockWise),
//                endingPos = getPos().relative(up,3).relative(counterClockWise);
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



