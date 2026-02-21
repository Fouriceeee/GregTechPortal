package com.ironsword.gtportal.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.ironsword.gtportal.GTPConfigHolder;
import com.ironsword.gtportal.api.machine.feature.IBlockRenderMulti;
import com.ironsword.gtportal.api.portal.teleporter.GTPTeleporter;
import com.ironsword.gtportal.common.data.GTPBlocks;
import com.ironsword.gtportal.common.item.component.DimensionDataComponent;
import com.ironsword.gtportal.common.machine.multiblock.logic.PortalLogic;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

public class MultidimensionalPortalControllerMachine extends WorkableElectricMultiblockMachine implements IBlockRenderMulti {
    public static final Pair<ResourceLocation,Vec3i> EMPTY_PAIR = Pair.of(null,null);
    public static final Pair<Supplier<? extends Block>,TeleportFunction> EMPTY = Pair.of(GTPBlocks.EMPTY_PORTAL_BLOCK::get,(entity, currWorld, destWorld, controllerPos,coordinate) -> {});
    public static final Map<ResourceLocation, Pair<Supplier<? extends Block>,TeleportFunction>> MAP = new HashMap<>(Map.of(
            Level.OVERWORLD.location(),Pair.of(
                    GTPBlocks.OVERWORLD_PORTAL_BLOCK::get,
                    (entity, currWorld, destWorld, contrllerPos,coordinate) ->
                            entity.changeDimension(destWorld,new GTPTeleporter(currWorld,contrllerPos,coordinate,Blocks.COBBLESTONE))),
            Level.NETHER.location(),Pair.of(
                    GTPBlocks.NETHER_PORTAL_BLOCK::get,
                    (entity, currWorld, destWorld,contrllerPos, coordinate) -> entity.changeDimension(destWorld,new GTPTeleporter(currWorld,contrllerPos,coordinate,Blocks.NETHERRACK))),
            Level.END.location(),Pair.of(
                    GTPBlocks.END_PORTAL_BLOCK::get,
                    (entity, currWorld, destWorld, contrllerPos,coordinate) -> {
                        if (coordinate == null){
                            entity.changeDimension(destWorld);
                        }else {
                            entity.changeDimension(destWorld,new GTPTeleporter(currWorld,contrllerPos,coordinate,Blocks.END_STONE));
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
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new PortalLogic(this);
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        if (cache.getFirst() == null) {
            textList.add(Component.translatable("gtportal.machine.tooltip.no_data"));
        }
        else {
            textList.add(Component.translatable("gtportal.machine.tooltip.dimension").append(": ").append(Component.translatable("gtportal.dimension.%s".formatted(cache.getFirst().getPath()))));
            if (cache.getSecond() != null){
                textList.add(Component.translatable("gtportal.machine.tooltip.coordinate").append(": ").append("[%d %d %d]".formatted(cache.getSecond().getX(),cache.getSecond().getY(),cache.getSecond().getZ())));
            }
        }


    }

    @Override
    public @NotNull Set<BlockPos> saveOffsets() {
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
        if (GTPConfigHolder.INSTANCE.portalBlockConfigs.generatePortalBlocks){
            destroyPortalBlock();
        }
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

//        ResourceLocation dimension = new ResourceLocation(recipe.data.getString("dimension"));
//        if (getLevel().dimension().location().equals(dimension)) return false;
//        cache = getFirstDimData(recipe);
//        return true;

        cache = getFirstDimData(recipe);

        if (cache.getFirst() == null || getLevel().dimension().location().equals(cache.getFirst())){
            return false;
        }
        else {
            if (GTPConfigHolder.INSTANCE.portalBlockConfigs.generatePortalBlocks){
                placePortalBlock();
            }
            return true;
        }
        //return cache.getFirst() != null || getLevel().dimension().location().equals(cache.getFirst());
    }

    @Override
    public void afterWorking() {
        clearCache();
        if (GTPConfigHolder.INSTANCE.portalBlockConfigs.generatePortalBlocks){
            fillAir();
        }
        super.afterWorking();
    }

    protected void placePortalBlock(){
        if (getLevel() instanceof ServerLevel){
            for (var pos:getPortalPoses()){
                getLevel().setBlockAndUpdate(pos,MAP.getOrDefault(cache.getFirst(),EMPTY).getFirst().get().defaultBlockState().setValue(BlockStateProperties.AXIS,getFrontFacing().getAxis()));
            }
        }
    }

    protected void fillAir(){
        if (getLevel() instanceof ServerLevel){
            for (var pos:getPortalPoses()){
                getLevel().setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
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

    protected Pair<ResourceLocation, Vec3i> getFirstDimData(@NotNull GTRecipe recipe){
        var itemInputs = recipe.inputs.getOrDefault(ItemRecipeCapability.CAP, Collections.emptyList());
        if (itemInputs.isEmpty()) return EMPTY_PAIR;
        int inputsSize = itemInputs.size();

        var itemHandlers = getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP);
        var itemInventory = itemHandlers.stream()
                .filter(IRecipeHandler::shouldSearchContent)
                .map(container -> container.getContents().stream()
                        .filter(ItemStack.class::isInstance)
                        .map(ItemStack.class::cast)
                        .filter(s -> !s.isEmpty())
                        .findFirst())
                .dropWhile(Optional::isEmpty)
                .limit(inputsSize)
                .map(o -> o.orElse(ItemStack.EMPTY))
                .toList();

        if (itemInventory.size() < inputsSize) return EMPTY_PAIR;

        for (int i = 0; i < inputsSize; i++){
            var itemStack = itemInventory.get(i);

            Ingredient recipeStack = ItemRecipeCapability.CAP.of(itemInputs.get(i).content);
            if (recipeStack.test(itemStack)){
                return DimensionDataComponent.dataFromItemStack(itemStack);
            }

        }

        return EMPTY_PAIR;

    }

    public void clearCache(){
        cache = EMPTY_PAIR;
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
        Direction clockwise = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction counterClockwise = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());

        BlockPos startingPos = getPos().relative(up).relative(clockwise),
                endingPos = getPos().relative(up,3).relative(counterClockwise);

        getLevel().getEntities(null, Utils.getMaxBox(startingPos,endingPos)).forEach(e->{
            if (!(e instanceof Entity) ||!e.canChangeDimensions())
                return;

            MAP.getOrDefault(dimension,EMPTY).getSecond().teleport(e,(ServerLevel) getLevel(),serverLevel,getPos(),cache.getSecond());
        });
    }

    @FunctionalInterface
    public interface TeleportFunction{
        void teleport(Entity entity, ServerLevel currWorld, ServerLevel destWorld, BlockPos controllerPos,@Nullable Vec3i coordinate);
    }
}



