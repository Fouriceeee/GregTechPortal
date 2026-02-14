package com.ironsword.gtportal.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.ironsword.gtportal.api.machine.feature.IBlockRenderMulti;
import com.ironsword.gtportal.api.portal.teleporter.GTPTeleporter;
import com.ironsword.gtportal.common.data.GTPBlocks;
import com.ironsword.gtportal.common.machine.multiblock.logic.MultidimensionalPortalLogic;
import com.ironsword.gtportal.utils.Utils;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

public class MultidimensionalPortalControllerMachine extends WorkableElectricMultiblockMachine implements IBlockRenderMulti {

    public static final Pair<Supplier<? extends Block>,TeleportFunction> EMPTY = Pair.of(GTPBlocks.EMPTY_PORTAL_BLOCK,(entity, currWorld, destWorld, coordinate) -> {});
    public static final Map<ResourceLocation, Pair<Supplier<? extends Block>,TeleportFunction>> MAP = new HashMap<>(Map.of(
            Level.OVERWORLD.location(),Pair.of(
                    GTPBlocks.OVERWORLD_PORTAL_BLOCK,
                    (entity, currWorld, destWorld, coordinate) ->
                            entity.changeDimension(destWorld,new GTPTeleporter(currWorld,coordinate,Blocks.COBBLESTONE))),
            Level.NETHER.location(),Pair.of(
                    GTPBlocks.NETHER_PORTAL_BLOCK,
                    (entity, currWorld, destWorld, coordinate) -> entity.changeDimension(destWorld,new GTPTeleporter(currWorld,coordinate,Blocks.NETHERRACK))),
            Level.END.location(),Pair.of(
                    GTPBlocks.END_PORTAL_BLOCK,
                    (entity, currWorld, destWorld, coordinate) -> {
                        if (coordinate == null){
                            entity.changeDimension(destWorld);
                        }else {
                            entity.changeDimension(destWorld,new GTPTeleporter(currWorld,coordinate,Blocks.END_STONE));
                        }
                    }
            )
    ));

    @Getter
    @Setter
    @DescSynced
    @RequireRerender
    private @NotNull Set<BlockPos> blockOffsets = new HashSet<>();

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(MultidimensionalPortalControllerMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Nonnull
    @Getter
    protected Pair<ResourceLocation, Vec3i> cache = Pair.of(null,null);

    protected TickableSubscription teleportSubscription;

    public MultidimensionalPortalControllerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static ManagedFieldHolder getManagedFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
    }

    @Override
    public @NotNull Set<BlockPos> saveOffsets() {
        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());

        BlockPos pos = getPos();

        return Set.of(pos.relative(up).subtract(pos),pos.relative(up,2).subtract(pos));
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
        unsubscribe(teleportSubscription);
        teleportSubscription = null;
        IBlockRenderMulti.super.onStructureInvalid();
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
        return true;
    }

    @Override
    public void afterWorking() {
        clearCache();
        super.afterWorking();
    }

    public void clearCache(){
        cache = Pair.of(null,null);
    }

    public Set<BlockPos> getBlockPoses(){
        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());

        return Set.of(getPos().relative(up),getPos().relative(up,2));
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



