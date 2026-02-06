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
import com.ironsword.gtportal.api.portal.DimensionData;
import com.ironsword.gtportal.api.portal.DimensionInfo;
import com.ironsword.gtportal.common.block.DimensionalPortalBlock;
import com.ironsword.gtportal.common.data.GTPBlocks;
import com.ironsword.gtportal.common.machine.multiblock.logic.PortalControllerLogic;
import com.ironsword.gtportal.common.machine.multiblock.part.DimensionDataHatchMachine;
import com.ironsword.gtportal.utils.Utils;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PortalControllerMachine extends WorkableElectricMultiblockMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(PortalControllerMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Getter
    @Setter
    protected DimensionData cachedDimensionData = null;

    protected TickableSubscription teleportSubscription;

    @Getter
    @Nullable
    protected EnergyContainerList inputEnergyContainers;

    public PortalControllerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static ManagedFieldHolder getManagedFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new PortalControllerLogic(this);
    }

    public PortalControllerLogic getPortalRecipeLogic(){
        return (PortalControllerLogic) super.getRecipeLogic();
    }

    public long getEnergyPerTick() {
        return cachedDimensionData == null ? 0L : cachedDimensionData.info().getTeleportEnergy();
    }

    public BlockPos getFrontPos(){
        return getPos().relative(getFrontFacing());
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        refreshDimensionData();
        if (cachedDimensionData != null){
            textList.add(cachedDimensionData.toDimension());
            if (cachedDimensionData.hasPos())
                textList.add(cachedDimensionData.toPosition());
            return;
        }
        textList.add(Component.translatable("gtportal.machine.tooltip.no_data"));
    }

    protected void refreshPortalBlock(){
        if (isWorkingEnabled()){
            setPortalBlock();
        }else{
            breakPortalBlock();
        }
    }

    public void refreshDimensionData(){
        for (var part:getParts()){
            if (part instanceof DimensionDataHatchMachine hatch){
                this.cachedDimensionData = hatch.readData();
                if (cachedDimensionData != null){
                    refreshPortalBlock();
                }else {
                    breakPortalBlock();
                }
                return;
            }
        }
        breakPortalBlock();
        this.cachedDimensionData = null;
    }

    public DimensionInfo getDimensionInfo(){
        refreshDimensionData();
        return cachedDimensionData == null ? DimensionInfo.EMPTY : cachedDimensionData.info();
    }

    @Override
    public void onUnload() {
        super.onUnload();
        unsubscribe(teleportSubscription);
        teleportSubscription = null;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        refreshDimensionData();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        teleportSubscription = subscribeServerTick(teleportSubscription,this::teleportEntities);

        refreshDimensionData();
        initializeAbilities();

        this.getPortalRecipeLogic().setDuration(20);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        breakPortalBlock();
        unsubscribe(teleportSubscription);
        teleportSubscription = null;
        this.inputEnergyContainers = null;
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        if (isWorkingAllowed){
            refreshDimensionData();
        }else {
            getRecipeLogic().setStatus(RecipeLogic.Status.IDLE);
        }
        super.setWorkingEnabled(isWorkingAllowed);
    }

    protected void initializeAbilities() {
        List<IEnergyContainer> energyContainers = new ArrayList<>();
        Long2ObjectMap<IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap",
                Long2ObjectMaps::emptyMap);
        for (IMultiPart part : getParts()) {
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
        getPortalRecipeLogic().setEnergyContainer(this.inputEnergyContainers);
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

    protected void setPortalBlock(){
        if (!(getLevel()instanceof ServerLevel)) return;
        for (var offset:getOffsets()){
            getLevel().setBlockAndUpdate(
                    getPos().offset(offset),
                    GTPBlocks.DIMENSIONAL_PORTAL_BLOCK.getDefaultState()
                            .setValue(BlockStateProperties.AXIS,getFrontFacing().getAxis())
                            .setValue(DimensionalPortalBlock.DIMENSIONS,cachedDimensionData.info())
            );
        }
    }

    public void breakPortalBlock(){
        if (!(getLevel()instanceof ServerLevel)) return;
        for (var offset:getOffsets()){
            BlockPos pos = getPos().offset(offset);
            if (getLevel().getBlockState(pos).is(GTPBlocks.DIMENSIONAL_PORTAL_BLOCK.get())){
                getLevel().destroyBlock(pos,false);
            }
        }
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

        if (cachedDimensionData == null || cachedDimensionData.info().getId().equals(getLevel().dimension().location())) return;

        ServerLevel serverlevel = cachedDimensionData.getLevel(((ServerLevel)getLevel()).getServer());
        if (serverlevel == null) return;

        getLevel().getEntities(null, Utils.getMaxBox(startingPos,endingPos)).forEach(e->{
            if (!(e instanceof Entity) ||!e.canChangeDimensions())
                return;

            cachedDimensionData.info().getTeleportFunc().apply(e,(ServerLevel) getLevel(),serverlevel,cachedDimensionData.pos());
        });
    }
}
