package com.ironsword.gtportal.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.ironsword.gtportal.api.machine.feature.ITeleportMachine;
import com.ironsword.gtportal.common.machine.multiblock.logic.PortalLogic;
import com.ironsword.gtportal.utils.Utils;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static com.ironsword.gtportal.common.machine.multiblock.MultidimensionalPortalControllerMachine.MAP;

public class SingleDimensionPortalControllerMachine extends WorkableElectricMultiblockMachine implements ITeleportMachine {

    private final ResourceLocation dimension;

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(SingleDimensionPortalControllerMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

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
    public void onStructureInvalid() {
        super.onStructureInvalid();
        destroyPortalBlock();
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe == null) return false;
        if (!super.beforeWorking(recipe)) return false;

        if (getLevel() == null || getLevel().dimension().location().equals(dimension) || !recipe.data.getString("dimension").equals(dimension.toString())){
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

    @Override
    public void teleportEntities(){
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

            MAP.getOrDefault(dimension,MultidimensionalPortalControllerMachine.EMPTY).getSecond().teleport(e,(ServerLevel) getLevel(),serverLevel,getPos(),null);
        });
    }
}
