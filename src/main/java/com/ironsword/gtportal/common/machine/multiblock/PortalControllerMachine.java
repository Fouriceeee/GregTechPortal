package com.ironsword.gtportal.common.machine.multiblock;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.ironsword.gtportal.api.machine.feature.IBlockRenderMulti;
import com.ironsword.gtportal.api.portal.DimensionData;
import com.ironsword.gtportal.api.portal.PosData;
import com.ironsword.gtportal.common.item.component.DimensionDataComponent;
import com.ironsword.gtportal.common.machine.multiblock.part.DimensionDataHatchMachine;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

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

    private TickableSubscription teleportSubscription;

    public PortalControllerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static ManagedFieldHolder getManagedFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public BlockPos getFrontPos(){
        return getPos().relative(getFrontFacing());
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        DimensionData data = getDimensionData();
        if (data != null){
            textList.add(Component.literal("Dimension: "+data.dimension().toString()));
            if (data.pos() != null){
                textList.add(Component.literal("Position: "+data.pos().getX()+", "+data.pos().getY()+", "+data.pos().getZ()));
            }
            return;
        }
        textList.add(Component.literal("No Destination Set"));
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
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        unsubscribe(teleportSubscription);
        teleportSubscription = null;
        IBlockRenderMulti.super.onStructureInvalid();
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
//        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
//        Direction clockWise = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
//        Direction counterClockWise = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(),
//                isFlipped());
//
//        BlockPos startingPos = getPos().relative(up).relative(clockWise),
//                endingPos = getPos().relative(up,3).relative(counterClockWise);
//        getLevel().getEntities(null, maxBox(startingPos,endingPos)).forEach(e->{
//            if (getLevel() instanceof ServerLevel && e.canChangeDimensions()){
//                if (this.destinationPos !=null){
//                    ServerLevel serverlevel = this.destinationPos.getLevel(((ServerLevel)getLevel()).getServer());
//                    if (serverlevel == null) {
//                        return;
//                    }
//                    if (serverlevel.dimension().location().equals(this.destinationPos.dimension())){
//                        Vec3i pos = this.destinationPos.pos();
//                        e.teleportTo(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5);
//                    }else {
//                        e.changeDimension(serverlevel, new NetherTeleporter(serverlevel,new BlockPos(this.destinationPos.pos())));
//                    }
//                }
//            }
//        });
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

//    public class PortalControllerRecipeLogic extends RecipeLogic{
//
//        public PortalControllerRecipeLogic(IRecipeLogicMachine machine) {
//            super(machine);
//        }
//    }
}
