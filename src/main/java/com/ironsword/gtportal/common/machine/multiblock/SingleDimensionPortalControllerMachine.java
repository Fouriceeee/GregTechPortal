package com.ironsword.gtportal.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.ironsword.gtportal.api.machine.feature.IBlockRenderMulti;
import com.ironsword.gtportal.common.machine.multiblock.logic.PortalLogic;
import com.ironsword.gtportal.utils.Utils;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static com.ironsword.gtportal.common.machine.multiblock.MultidimensionalPortalControllerMachine.MAP;

public class SingleDimensionPortalControllerMachine extends WorkableElectricMultiblockMachine implements IBlockRenderMulti {

    private final ResourceLocation dimension;

    @Getter
    @Setter
    @DescSynced
    @RequireRerender
    private @NotNull Set<BlockPos> blockOffsets = new HashSet<>();

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(SingleDimensionPortalControllerMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    protected TickableSubscription teleportSubscription;

    public SingleDimensionPortalControllerMachine(IMachineBlockEntity holder, ResourceLocation dimension, Object... args) {
        super(holder, args);
        this.dimension = dimension;
    }

    public static ManagedFieldHolder getManagedFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new PortalLogic(this);
    }

    @Override
    public @NotNull Set<BlockPos> saveOffsets() {
//        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
//
//        BlockPos pos = getPos();
//
//        return Set.of(pos.relative(up).subtract(pos),pos.relative(up,2).subtract(pos));

        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction clockwise = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction counterClockwise = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());

        BlockPos pos = getPos();
        BlockPos center = pos;

        Set<BlockPos> offsets = new HashSet<>();

        for (int i=0;i<3;i++){
            center = center.relative(up);
            offsets.add(center.subtract(pos));
            offsets.add(center.relative(clockwise).subtract(pos));
            offsets.add(center.relative(counterClockwise).subtract(pos));
        }

        return offsets;
    }

    public Set<BlockPos> getPortalPoses(){
        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction clockwise = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction counterClockwise = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());

        BlockPos center = getPos();

        Set<BlockPos> poses = new HashSet<>();

        for (int i=0;i<3;i++){
            center = center.relative(up);
            poses.add(center);
            poses.add(center.relative(clockwise));
            poses.add(center.relative(counterClockwise));
        }

        return poses;
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
        IBlockRenderMulti.super.onStructureFormed();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        destroyPortalBlock();
        unsubscribe(teleportSubscription);
        teleportSubscription = null;
        IBlockRenderMulti.super.onStructureInvalid();
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe == null) return false;
        if (!super.beforeWorking(recipe)) return false;

        if (getLevel().dimension().location().equals(dimension)){
            return false;
        }else {
            placePortalBlock();
            return true;
        }
    }

    @Override
    public void afterWorking() {
        fillAir();
        super.afterWorking();
    }

    protected void placePortalBlock(){
        if (getLevel() instanceof ServerLevel){
            for (var pos:getPortalPoses()){
                getLevel().setBlockAndUpdate(pos,MAP.getOrDefault(dimension,MultidimensionalPortalControllerMachine.EMPTY).getFirst().get().defaultBlockState().setValue(BlockStateProperties.AXIS,getFrontFacing().getAxis()));
            }
        }
    }

    protected void fillAir(){
        if (getLevel() instanceof ServerLevel){
            for (var pos:getPortalPoses()){
                getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    protected void destroyPortalBlock(){
        if (getLevel() instanceof ServerLevel){
            for (var pos:getPortalPoses()){
                getLevel().destroyBlock(pos,false);
            }
        }
    }

    protected void teleportEntities(){
        if (!(getLevel() instanceof ServerLevel)||!getRecipeLogic().isWorking())
            return;

        ServerLevel serverLevel = ((ServerLevel) getLevel()).getServer().getLevel(ResourceKey.create(Registries.DIMENSION,dimension));
        if (serverLevel == null)
            return;

        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction clockwise = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction counterClockwise = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());

        BlockPos startingPos = getPos().relative(up).relative(clockwise),
                endingPos = getPos().relative(up,3).relative(counterClockwise);

        getLevel().getEntities(null, Utils.getMaxBox(startingPos,endingPos)).forEach(e->{
            if (!(e instanceof Entity) ||!e.canChangeDimensions())
                return;

            MAP.getOrDefault(dimension,MultidimensionalPortalControllerMachine.EMPTY).getSecond().teleport(e,(ServerLevel) getLevel(),serverLevel,null);
        });
    }
}
