package com.ironsword.gtportal.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IFluidRenderMulti;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;
import com.ironsword.gtportal.api.portal.PosData;
import com.ironsword.gtportal.common.item.RecorderItem;
import com.ironsword.gtportal.common.portal.teleporter.NetherTeleporter;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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

public class PortalControllerMachine extends WorkableElectricMultiblockMachine implements IFluidRenderMulti {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(PortalControllerMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private PosData destinationPos = null;

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

    @Override
    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack item = player.getItemInHand(hand);
        if (item.getItem() instanceof RecorderItem){
            CompoundTag posDataTag = item.getTagElement("posData");
            if (posDataTag!=null){
                destinationPos = PosData.fromNbt(posDataTag);
                player.displayClientMessage(Component.literal("Success!"),true);
                return InteractionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        if (this.destinationPos !=null){
            textList.add(Component.literal("Dimension: "+this.destinationPos.dimension().toString()));
            textList.add(Component.literal("Position: "+this.destinationPos.pos().getX()+", "+this.destinationPos.pos().getY()+", "+this.destinationPos.pos().getZ()));
        }else {
            textList.add(Component.literal("No Destination Set"));
        }
    }

    @Override
    public void saveCustomPersistedData(@NotNull CompoundTag tag, boolean forDrop) {
        super.saveCustomPersistedData(tag, forDrop);
        tag.put("destination",destinationPos.toNbt());
    }

    @Override
    public void loadCustomPersistedData(@NotNull CompoundTag tag) {
        super.loadCustomPersistedData(tag);
        if (tag.contains("destination")){
            this.destinationPos = PosData.fromNbt(tag.getCompound("destination"));
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
    public @NotNull Set<BlockPos> getFluidBlockOffsets() {
        return portalBlockOffsets;
    }

    @Override
    public void setFluidBlockOffsets(@NotNull Set<BlockPos> offsets) {
        this.portalBlockOffsets = offsets;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        teleportSubscription = subscribeServerTick(teleportSubscription,this::teleportEntities);
        IFluidRenderMulti.super.onStructureFormed();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        unsubscribe(teleportSubscription);
        teleportSubscription = null;
        IFluidRenderMulti.super.onStructureInvalid();
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
        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction clockWise = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction counterClockWise = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(),
                isFlipped());

        BlockPos startingPos = getPos().relative(up).relative(clockWise),
                endingPos = getPos().relative(up,3).relative(counterClockWise);
        getLevel().getEntities(null, maxBox(startingPos,endingPos)).forEach(e->{
            if (getLevel() instanceof ServerLevel && e.canChangeDimensions()){
                if (this.destinationPos !=null){
                    ServerLevel serverlevel = this.destinationPos.getLevel(((ServerLevel)getLevel()).getServer());
                    if (serverlevel == null) {
                        return;
                    }
                    if (serverlevel.dimension().location().equals(this.destinationPos.dimension())){
                        Vec3i pos = this.destinationPos.pos();
                        e.teleportTo(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5);
                    }else {
                        e.changeDimension(serverlevel, new NetherTeleporter(serverlevel,new BlockPos(this.destinationPos.pos())));
                    }
                }
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

    public class PortalControllerRecipeLogic extends RecipeLogic{

        public PortalControllerRecipeLogic(IRecipeLogicMachine machine) {
            super(machine);
        }
    }
}
