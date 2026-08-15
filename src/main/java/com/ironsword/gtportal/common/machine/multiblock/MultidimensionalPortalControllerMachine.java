package com.ironsword.gtportal.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.RecipeElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.ironsword.gtportal.api.machine.feature.ITeleportMachine;
import com.ironsword.gtportal.api.portal.teleporter.EndTeleporter;
import com.ironsword.gtportal.api.portal.teleporter.GTPTeleporter;
import com.ironsword.gtportal.common.data.GTPBlocks;
import com.ironsword.gtportal.common.item.component.DimensionDataComponent;
import com.ironsword.gtportal.common.machine.multiblock.logic.PortalLogic;
import com.ironsword.gtportal.utils.PhyUtils;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

public class MultidimensionalPortalControllerMachine extends RecipeElectricMultiblockMachine implements ITeleportMachine {
    public static final Pair<ResourceLocation,Vec3i> EMPTY_PAIR = Pair.of(null,null);
    public static final Pair<Supplier<? extends Block>,TeleportFunction> EMPTY = Pair.of(GTPBlocks.EMPTY_PORTAL_BLOCK,(entity,offset,  currWorld, destWorld, controllerPos, coordinate) -> {});
    public static final Map<ResourceLocation, Pair<Supplier<? extends Block>,TeleportFunction>> MAP = new HashMap<>(Map.of(
            Level.OVERWORLD.location(),Pair.of(
                    GTPBlocks.OVERWORLD_PORTAL_BLOCK,
                    (entity,offset, currWorld, destWorld, contrllerPos,coordinate) ->
                            entity.changeDimension(destWorld,new GTPTeleporter(offset,currWorld,contrllerPos,coordinate,Blocks.COBBLESTONE))),
            Level.NETHER.location(),Pair.of(
                    GTPBlocks.NETHER_PORTAL_BLOCK,
                    (entity,offset, currWorld, destWorld,contrllerPos, coordinate) ->
                            entity.changeDimension(destWorld,new GTPTeleporter(offset,currWorld,contrllerPos,coordinate,Blocks.NETHERRACK))),
            Level.END.location(),Pair.of(
                    GTPBlocks.END_PORTAL_BLOCK,
                    (entity, offset,currWorld, destWorld, contrllerPos,coordinate) ->
                            entity.changeDimension(destWorld,new EndTeleporter(offset,currWorld,contrllerPos,coordinate,Blocks.OBSIDIAN)))
    ));

    @Nonnull
    @Getter
    @Persisted
    protected Pair<ResourceLocation, Vec3i> cache = Pair.of(null,null);
    @Nullable
    private AABB portalBlockAABB;

    public MultidimensionalPortalControllerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
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

    private void cachePortalBlockBox(){
        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction clockwise = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction counterClockwise = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());

        BlockPos startingPos = getPos().relative(up).relative(clockwise),
                endingPos = getPos().relative(up,3).relative(counterClockwise);

        portalBlockAABB = PhyUtils.getPortalBlockBox(startingPos,endingPos,getFrontFacing().getAxis());
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
    public void onStructureFormed() {
        super.onStructureFormed();
        cachePortalBlockBox();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        destroyPortalBlock();
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        super.setWorkingEnabled(isWorkingAllowed);
    }

    @Override
    public Component beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe == null) return Component.translatable("gtportal.machine.tooltip.no_data");
        Component result = super.beforeWorking(recipe);
        if (result != null) return result;

//        ResourceLocation dimension = new ResourceLocation(recipe.data.getString("dimension"));
//        if (getLevel().dimension().location().equals(dimension)) return false;
//        cache = getFirstDimData(recipe);
//        return true;

        cache = getFirstDimData(recipe);

        if (cache.getFirst() == null || getLevel().dimension().location().equals(cache.getFirst())){
            return Component.translatable("gtportal.machine.tooltip.no_data");
        }
        else {
            placePortalBlock();
            return null;
        }
        //return cache.getFirst() != null || getLevel().dimension().location().equals(cache.getFirst());
    }

    @Override
    public void afterWorking() {
        clearCache();
        fillAir();
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

            Ingredient recipeStack = itemInputs.get(i).toVanillaIngredient();
            if (recipeStack.test(itemStack)){
                return DimensionDataComponent.dataFromItemStack(itemStack);
            }

        }

        return EMPTY_PAIR;

    }

    public void clearCache(){
        cache = EMPTY_PAIR;
    }

    public Vec3 getEntityRelativeOffset(Entity entity){
        Vec3 offset = getPos().getCenter().vectorTo(entity.position());

        Direction
                front  = getFrontFacing(),
                up     = PhyUtils.getMachineUpFacing(front,getUpwardsFacing()),
                right  = PhyUtils.getMachineRightFacing(front,up);

        return new Vec3(
                offset.get(front.getAxis()) * front.getAxisDirection().getStep(),
                offset.get(up.getAxis()) * up.getAxisDirection().getStep(),
                offset.get(right.getAxis()) * right.getAxisDirection().getStep()
        );
    }

    @Deprecated
    public Vec3 applyRelativeOffset(Vec3 offset){
        return applyOffset(offset);
    }

    @Override
    public Vec3 applyOffset(Vec3 offset) {
        Vec3 center = getPos().getCenter();

        Direction
                front  = getFrontFacing(),
                up     = PhyUtils.getMachineUpFacing(front,getUpwardsFacing()),
                right  = PhyUtils.getMachineRightFacing(front,up);

        return center.relative(front,offset.x).relative(up,offset.y).relative(right,offset.z);
    }

    @Override
    public void teleportEntities(){
        if (!(getLevel() instanceof ServerLevel)||!getRecipeLogic().isWorking())
            return;

        ResourceLocation dimension = cache.getFirst();

        if (dimension == null || dimension.equals(getLevel().dimension().location()))
            return;

        ServerLevel serverLevel = ((ServerLevel) getLevel()).getServer().getLevel(ResourceKey.create(Registries.DIMENSION,dimension));
        if (serverLevel == null)
            return;

        if (portalBlockAABB == null){
            cachePortalBlockBox();
        }

        getLevel().getEntities(null, portalBlockAABB).forEach(e->{
            if (!(e instanceof Entity) ||!e.canChangeDimensions() || e.isOnPortalCooldown())
                return;

            MAP.getOrDefault(dimension,EMPTY).getSecond().teleport(e,getEntityRelativeOffset(e),(ServerLevel) getLevel(),serverLevel,getPos(),cache.getSecond());
        });
    }

    @Override
    public boolean canTeleport() {
        return isActive();
    }

    @FunctionalInterface
    public interface TeleportFunction{
        void teleport(Entity entity,Vec3 offset, ServerLevel currWorld, ServerLevel destWorld, BlockPos controllerPos,@Nullable Vec3i coordinate);
    }
}



