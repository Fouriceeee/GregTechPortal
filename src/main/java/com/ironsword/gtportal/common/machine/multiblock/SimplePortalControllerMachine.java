package com.ironsword.gtportal.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.ironsword.gtportal.api.portal.DimensionInfo;
import com.ironsword.gtportal.common.block.DimensionalPortalBlock;
import com.ironsword.gtportal.common.data.GTPBlocks;
import com.ironsword.gtportal.utils.Utils;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimplePortalControllerMachine extends WorkableElectricMultiblockMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(SimplePortalControllerMachine.class,
            WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

    protected final DimensionInfo INFO;

    protected TickableSubscription teleportSubscription;

    public static ManagedFieldHolder getManagedFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public SimplePortalControllerMachine(DimensionInfo info,IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        INFO = info;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        textList.add(INFO.toDimension());
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

    protected void refreshPortalBlock(){
        if (isWorkingEnabled()){
            setPortalBlock();
        }else{
            breakPortalBlock();
        }
    }

    protected void setPortalBlock(){
        if (!(getLevel()instanceof ServerLevel)) return;
        for (var offset:getOffsets()){
            getLevel().setBlockAndUpdate(
                    getPos().offset(offset),
                    GTPBlocks.DIMENSIONAL_PORTAL_BLOCK.getDefaultState()
                            .setValue(BlockStateProperties.AXIS,getFrontFacing().getAxis())
                            .setValue(DimensionalPortalBlock.DIMENSIONS,INFO)
            );
        }
    }

    protected void breakPortalBlock(){
        if (!(getLevel()instanceof ServerLevel)) return;
        for (var offset:getOffsets()){
            BlockPos pos = getPos().offset(offset);
            if (getLevel().getBlockState(pos).is(GTPBlocks.DIMENSIONAL_PORTAL_BLOCK.get())){
                getLevel().destroyBlock(pos,false);
            }
        }
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
        refreshPortalBlock();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        breakPortalBlock();
        unsubscribe(teleportSubscription);
        teleportSubscription = null;
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        super.setWorkingEnabled(isWorkingAllowed);
        refreshPortalBlock();
    }

    protected void teleportEntities(){
        if (!(getLevel() instanceof ServerLevel)||!isWorkingEnabled())
            return;

        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction clockWise = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction counterClockWise = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(),
                isFlipped());

        BlockPos startingPos = getPos().relative(up).relative(clockWise),
                endingPos = getPos().relative(up,3).relative(counterClockWise);

        if (INFO.getId().equals(getLevel().dimension().location())) return;

        ServerLevel serverlevel = INFO.getLevel(((ServerLevel)getLevel()).getServer());
        if (serverlevel == null) return;

        getLevel().getEntities(null, Utils.getMaxBox(startingPos,endingPos)).forEach(e->{
            if (!(e instanceof Entity) ||!e.canChangeDimensions())
                return;

            INFO.getTeleportFunc().apply(e,(ServerLevel) getLevel(),serverlevel,null);
        });
    }

}
