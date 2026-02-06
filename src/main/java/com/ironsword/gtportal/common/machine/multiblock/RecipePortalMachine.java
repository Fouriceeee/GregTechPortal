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
import com.ironsword.gtportal.api.portal.teleporter.GTPTeleporter;
import com.ironsword.gtportal.common.block.DimensionalPortalBlock;
import com.ironsword.gtportal.common.data.GTPBlocks;
import com.ironsword.gtportal.common.machine.multiblock.logic.RecipePortalLogic;
import com.ironsword.gtportal.utils.Utils;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import lombok.Getter;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class RecipePortalMachine extends WorkableElectricMultiblockMachine {
    public static final TeleportFunction EMPTY_FUNC = ((entity, currWorld, destWorld, coordinate) -> {});
    public static final Map<ResourceLocation, TeleportFunction> TELEPORTER_MAP = new HashMap<>(Map.of(
            Level.OVERWORLD.location(),((entity, currWorld, destWorld, coordinate) -> entity.changeDimension(destWorld,new GTPTeleporter(currWorld,coordinate,Blocks.COBBLESTONE))),
            Level.NETHER.location(),((entity, currWorld, destWorld, coordinate) -> entity.changeDimension(destWorld,new GTPTeleporter(currWorld,coordinate,Blocks.NETHERRACK))),
            Level.END.location(),((entity, currWorld, destWorld, coordinate) -> {
                if (coordinate == null){
                    entity.changeDimension(destWorld);
                }else {
                    entity.changeDimension(destWorld,new GTPTeleporter(currWorld,coordinate,Blocks.END_STONE));
                }
            })
    ));
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(RecipePortalMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    protected ResourceLocation cachedDimension = null;
    protected BlockPos cachedCoordinate = null;

    @Nullable
    protected EnergyContainerList inputEnergyContainers;

    protected TickableSubscription teleportSubscription;

    public RecipePortalMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static ManagedFieldHolder getManagedFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new RecipePortalLogic(this);
    }

    public RecipePortalLogic getRecipePortalLogic(){
        return (RecipePortalLogic) getRecipeLogic();
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
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        super.setWorkingEnabled(isWorkingAllowed);
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

    public Set<BlockPos> getOffsets() {
        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction clockWise = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction counterClockWise = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(),
                isFlipped());

        BlockPos pos = getPos();
        BlockPos center = pos;

        Set<BlockPos> offsets = new HashSet<>();

        for (int i=0;i<3;i++){
            center = center.relative(up);
            offsets.add(center.subtract(pos));
            offsets.add(center.relative(clockWise).subtract(pos));
            offsets.add(center.relative(counterClockWise).subtract(pos));
        }

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

        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction clockWise = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction counterClockWise = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(),
                isFlipped());

        BlockPos startingPos = getPos().relative(up).relative(clockWise),
                endingPos = getPos().relative(up,3).relative(counterClockWise);

        if (cachedDimension == null || cachedDimension.equals(getLevel().dimension().location()))
            return;

        ServerLevel serverLevel = ((ServerLevel) getLevel()).getServer().getLevel(ResourceKey.create(Registries.DIMENSION,cachedDimension));
        if (serverLevel == null)
            return;

        getLevel().getEntities(null, Utils.getMaxBox(startingPos,endingPos)).forEach(e->{
            if (!(e instanceof Entity) ||!e.canChangeDimensions())
                return;

            TELEPORTER_MAP.getOrDefault(cachedDimension,EMPTY_FUNC).teleport(e,(ServerLevel) getLevel(),serverLevel,cachedCoordinate);
        });
    }

    @FunctionalInterface
    public interface TeleportFunction{
        void teleport(Entity entity, ServerLevel currWorld, ServerLevel destWorld, @Nullable Vec3i coordinate);
    }
}



