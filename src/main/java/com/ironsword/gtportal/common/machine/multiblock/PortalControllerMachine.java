package com.ironsword.gtportal.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.ironsword.gtportal.api.machine.feature.IBlockRenderMulti;
import com.ironsword.gtportal.api.portal.DimensionData;
import com.ironsword.gtportal.api.portal.PosData;
import com.ironsword.gtportal.api.portal.teleporter.Teleporter;
import com.ironsword.gtportal.common.data.GTPRecipes;
import com.ironsword.gtportal.common.item.component.DimensionDataComponent;
import com.ironsword.gtportal.common.machine.multiblock.logic.PortalControllerLogic;
import com.ironsword.gtportal.common.machine.multiblock.part.DimensionDataHatchMachine;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PortalControllerMachine extends WorkableElectricMultiblockMachine implements IBlockRenderMulti {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(PortalControllerMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Getter
    @Setter
    @DescSynced
    @RequireRerender
    private Set<BlockPos> portalBlockOffsets = new HashSet<>();

    private DimensionData cachedDimensionData = null;

    private TickableSubscription teleportSubscription;

    @Getter
    @Nullable
    private EnergyContainerList inputEnergyContainers;

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

    @Override
    public PortalControllerLogic getRecipeLogic() {
        return (PortalControllerLogic) super.getRecipeLogic();
    }

    public long getEnergyPerTick() {
        return 32L;
    }

    public BlockPos getFrontPos(){
        return getPos().relative(getFrontFacing());
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        refreshDimensionData();

        DimensionData data = cachedDimensionData;
        if (data != null){
            textList.add(Component.literal("Dimension: "+data.dimension()));
            if (data.pos() != null){
                textList.add(Component.literal("Position: "+data.pos().getX()+", "+data.pos().getY()+", "+data.pos().getZ()));
            }
            return;
        }
        textList.add(Component.literal("No Destination Set"));
    }

    public void refreshDimensionData(DimensionData data){
        this.cachedDimensionData = data;
    }

    public void refreshDimensionData(){
        this.cachedDimensionData = getDimensionData();
    }

    private DimensionData getDimensionData(){
        for(var part:getParts()){
            if (part instanceof DimensionDataHatchMachine dimDataHatch)
                return dimDataHatch.getDimensionData();
        }
        return null;
    }

    @Override
    public void onUnload() {
        super.onUnload();
        unsubscribe(teleportSubscription);
        teleportSubscription = null;
    }


    @Override
    public @NotNull Set<BlockPos> getBlockOffsets() {
        return portalBlockOffsets;
    }

    @Override
    public void setBlockOffsets(@NotNull Set<BlockPos> offsets) {
        this.portalBlockOffsets = offsets;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        teleportSubscription = subscribeServerTick(teleportSubscription,this::teleportEntities);
        IBlockRenderMulti.super.onStructureFormed();

        initializeAbilities();

        this.getRecipeLogic().setDuration(20);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        unsubscribe(teleportSubscription);
        teleportSubscription = null;
        IBlockRenderMulti.super.onStructureInvalid();
        this.inputEnergyContainers = null;
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
        getRecipeLogic().setEnergyContainer(this.inputEnergyContainers);
    }

    @Override
    public @NotNull Set<BlockPos> saveOffsets() {
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


    private void teleportEntities(){
        if (!(getLevel() instanceof ServerLevel))
            return;

        if(!getRecipeLogic().isWorking()){
            return;
        }

        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction clockWise = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction counterClockWise = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(),
                isFlipped());

        BlockPos startingPos = getPos().relative(up).relative(clockWise),
                endingPos = getPos().relative(up,3).relative(counterClockWise);

        getLevel().getEntities(null, maxBox(startingPos,endingPos)).forEach(e->{
            if (!(e instanceof Entity))
                return;

            DimensionData data = getDimensionData();
            if (data == null || data.dimension().equals(getLevel().dimension().location())) return;

            if (!e.canChangeDimensions()) return;

            ServerLevel serverlevel = data.getLevel(((ServerLevel)getLevel()).getServer());
            if (serverlevel == null) return;

            if (data.dimension().equals(GTPRecipes.END)){
                e.changeDimension(serverlevel);
            }
            else {
                e.changeDimension(serverlevel, new Teleporter(cachedDimensionData.pos()));
            }
        });
    }

    private static AABB maxBox(Vec3i pos1, Vec3i pos2) {
        return new AABB(
                Math.min(pos1.getX(), pos2.getX()),
                Math.min(pos1.getY(), pos2.getY()),
                Math.min(pos1.getZ(), pos2.getZ()),
                Math.max(pos1.getX(), pos2.getX()) + 1,
                Math.max(pos1.getY(), pos2.getY()) + 1,
                Math.max(pos1.getZ(), pos2.getZ()) + 1
        );
    }
}
