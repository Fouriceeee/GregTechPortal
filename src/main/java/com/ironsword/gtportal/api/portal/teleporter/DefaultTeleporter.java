package com.ironsword.gtportal.api.portal.teleporter;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.ironsword.gtportal.api.machine.feature.ITeleportMachine;
import com.ironsword.gtportal.common.data.GTPPoiTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class DefaultTeleporter implements ITeleporter {
    public static final Map<ResourceLocation, ResourceKey<PoiType>> MAP = new HashMap<>(Map.of(
            Level.OVERWORLD.location(), GTPPoiTypes.OVERWORLD_PCM_POI.getKey(),
            Level.NETHER.location(), GTPPoiTypes.NETHER_PCM_POI.getKey(),
            Level.END.location(), GTPPoiTypes.END_PCM_POI.getKey()
    ));

    protected final ServerLevel level;
    protected final Vec3 offset;
    @Nullable
    protected final BlockPos coordinate;
    @Nullable
    protected final ITeleportMachine sourceMachine;

    public DefaultTeleporter(ServerLevel level, Vec3 offset, @Nullable Vec3i coordinate, @Nullable ITeleportMachine sourceMachine){
        this.level = level;
        this.offset = offset;
        this.coordinate = coordinate == null ? null : new BlockPos(coordinate);
        this.sourceMachine = sourceMachine;
    }

    @Override
    public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        PoiManager manager = destWorld.getPoiManager();

        if(coordinate != null){
            manager.ensureLoadedAndValid(destWorld, coordinate, 16);
            BlockEntity blockEntity = destWorld.getBlockEntity(coordinate);
            if (blockEntity instanceof MetaMachineBlockEntity machineEntity && machineEntity.getMetaMachine() instanceof ITeleportMachine machine && machine.canTeleport()){
                return new PortalInfo(machine.applyOffset(offset),Vec3.ZERO,entity.getYRot(), entity.getXRot());
            }
        }

        /*
         * Overworld and other dimensions : radius = 128
         * Nether : radius = 128 / 8 = 16
         */
        int searchRadius = (int) (128 / destWorld.dimensionType().coordinateScale());

        /*
         * calculate the destination position
         * horizontally (X and Z), multiply the scale directly
         * vertically (Y), clamp entity's position to the logical range first, then multiply the scale
         * for example, if entity is at y = 200 in Nether, when calculating the destination position, use y = 128 instead of 200
         */
        double horizontalScale = DimensionType.getTeleportationScale(this.level.dimensionType(), destWorld.dimensionType());
        double verticalScale = (double) destWorld.getHeight() / this.level.getHeight();
        int height = Math.min(entity.blockPosition().getY() - this.level.getMinBuildHeight(), this.level.getLogicalHeight());
        BlockPos destination = destWorld.getWorldBorder().clampToBounds(
                entity.blockPosition().getX() * horizontalScale,
                height * verticalScale + destWorld.getMinBuildHeight(),
                entity.blockPosition().getZ() * horizontalScale);

        /*
         * 1. search cached portal link
         */
        if (sourceMachine != null){
            TeleportCacheSavedData cache = TeleportCacheSavedData.get(destWorld);
            TeleportCacheSavedData.PortalData source = new TeleportCacheSavedData.PortalData(level.dimension().location(), sourceMachine.getPos());
            TeleportCacheSavedData.PortalData cachedTarget = cache.get(source);
            if (cachedTarget != null){
                ITeleportMachine machine = loadMachine(destWorld, cachedTarget);
                if (machine != null){
                    return new PortalInfo(machine.applyOffset(offset),Vec3.ZERO,entity.getYRot(), entity.getXRot());
                }
                cache.removeAllTo(cachedTarget);
            }
        }

        /*
         * 2. find a portal machine near the destination;
         * if found, teleport to it and save the link both ways
         */
        Optional<PortalInfo> machinePortal = findMachinePortal(entity, destWorld, destination, searchRadius);
        if (machinePortal.isPresent()){
            return machinePortal.get();
        }

        /*
         * 3. no portal machine is found, search a safe position to teleport.
         */
        return searchProperPosNearby(entity, destWorld, destination, searchRadius);
    }

    protected Optional<PortalInfo> findMachinePortal(Entity entity, ServerLevel destWorld, BlockPos center, int searchRadius){
        ResourceKey<PoiType> poiType = MAP.getOrDefault(level.dimension().location(), null);
        if (poiType == null) return Optional.empty();

        PoiManager manager = destWorld.getPoiManager();

        //load chunks
        manager.ensureLoadedAndValid(destWorld, center, searchRadius);

        List<PoiRecord> list = manager
                .getInSquare(holder -> holder.is(poiType) || holder.is(GTPPoiTypes.MULTI_PCM_POI.getKey()),
                        center,
                        searchRadius,
                        PoiManager.Occupancy.ANY)
                .filter(record -> destWorld.getWorldBorder().isWithinBounds(record.getPos()))
                .sorted(Comparator.<PoiRecord>comparingDouble(record -> record.getPos().distSqr(center)).thenComparingInt(record -> record.getPos().getY()))
                .toList();
        for(PoiRecord record: list){
            if(destWorld.getBlockEntity(record.getPos()) instanceof MetaMachineBlockEntity machineBlockEntity
                    && machineBlockEntity.getMetaMachine() instanceof ITeleportMachine teleportMachine
                    && teleportMachine.canTeleport()){
                cacheLink(destWorld, record.getPos(), teleportMachine);
                return Optional.of(new PortalInfo(teleportMachine.applyOffset(offset),Vec3.ZERO,entity.getYRot(), entity.getXRot()));
            }
        }
        return Optional.empty();
    }

    protected PortalInfo searchProperPosNearby(Entity entity, ServerLevel destWorld, BlockPos destination, int searchRadius){
        for(BlockPos.MutableBlockPos pos: BlockPos.spiralAround(destination, 16, Direction.EAST, Direction.SOUTH)){
            int startY = Math.min(destination.getY(), destWorld.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()));

            //search downwards
            for (int y = startY; y > destWorld.getMinBuildHeight(); --y){
                pos.setY(y);
                if (isPositionSafe(destWorld,pos)) {
                    return createPortalInfo(entity, pos);
                }
            }

            //search upwards
            for (int y = startY + 1; y < destWorld.getMaxBuildHeight() - 2; ++y){
                pos.setY(y);
                if (isPositionSafe(destWorld,pos)) {
                    return createPortalInfo(entity, pos);
                }
            }
        }

        BlockPos defaultPos = destWorld.getWorldBorder().isWithinBounds(destination)
                && destWorld.getMinBuildHeight() < destination.getY() - 1
                && destWorld.getMaxBuildHeight() > destination.getY() + 2
                ? destination : new BlockPos(destination.getX(), (int)(destWorld.getHeight() * 0.167f) + destWorld.getMinBuildHeight(), destination.getZ());

        if(!isPositionSafe(destWorld,defaultPos)){
            buildPlatForm(destWorld, Blocks.COBBLESTONE.defaultBlockState(), defaultPos);
        }
        return createPortalInfo(entity, defaultPos);
    }

    // ===== two-way cache helpers =====

    /**
     * Writes the link into the cache both ways:
     * <ul>
     *     <li>forward : source machine -> destination machine, so later trips from the source
     *     machine go to the same destination;</li>
     *     <li>reverse : destination machine -> source machine, so the return trip from the
     *     destination machine goes straight back.</li>
     * </ul>
     * The source machine is not looked up here: the machine itself passes its instance into the
     * teleporter when the teleport starts.
     */
    private void cacheLink(ServerLevel destWorld, BlockPos destControllerPos, ITeleportMachine destMachine){
        if (sourceMachine == null) return;
        TeleportCacheSavedData cache = TeleportCacheSavedData.get(destWorld);
        TeleportCacheSavedData.PortalData source = new TeleportCacheSavedData.PortalData(level.dimension().location(), sourceMachine.getPos());
        TeleportCacheSavedData.PortalData target = new TeleportCacheSavedData.PortalData(destWorld.dimension().location(), destControllerPos);

        //forward link: source machine -> dest machine
        cache.put(source, target);
        //reverse link: dest machine -> source machine
        cache.put(target, source);
    }

    /**
     * Loads the destination machine a cached link points to and checks whether it is still an
     * available portal machine. Returns null (and the caller should drop the cache) when the
     * machine is gone or not usable.
     */
    @Nullable
    private ITeleportMachine loadMachine(ServerLevel destWorld, TeleportCacheSavedData.PortalData targetMachine){
        if (!targetMachine.dimension().equals(destWorld.dimension().location())) return null;
        destWorld.getChunkAt(targetMachine.controllerPos());
        if (destWorld.getBlockEntity(targetMachine.controllerPos()) instanceof MetaMachineBlockEntity machineBlockEntity
                && machineBlockEntity.getMetaMachine() instanceof ITeleportMachine teleportMachine
                && teleportMachine.canTeleport()){
            return teleportMachine;
        }
        return null;
    }

    // ===== position utils (shared by fallback implementations) =====

    protected boolean isPositionSafe(ServerLevel destWorld, BlockPos checkPos) {
        if (destWorld.getBlockState(checkPos.below()).isAir() || destWorld.getBlockState(checkPos.below()).liquid()) return false;
        for (var pos:BlockPos.betweenClosed(checkPos,checkPos.above(2))){
            if (!destWorld.getWorldBorder().isWithinBounds(pos) || destWorld.getMinBuildHeight() >= pos.getY()) return false;
            BlockState state = destWorld.getBlockState(pos);
            if (!state.getBlock().isPossibleToRespawnInThis(state)){
                return false;
            }
        }

        return true;
    }

    protected static void buildPlatForm(ServerLevel destWorld, BlockState baseBlock, BlockPos pos){
        BlockPos.betweenClosed(pos.offset(-1,-1,-1),pos.offset(1,2,1)).forEach(blockPos -> {
            if (!destWorld.getBlockState(blockPos).hasBlockEntity()) {
                destWorld.setBlockAndUpdate(blockPos,blockPos.getY() == pos.getY() - 1 ? baseBlock : Blocks.AIR.defaultBlockState());
            }
        });
    }

    protected PortalInfo createPortalInfo(Entity entity, BlockPos pos){
        return new PortalInfo(new Vec3(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5),Vec3.ZERO,entity.getYRot(), entity.getXRot());
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        entity.setPortalCooldown();
        return ITeleporter.super.placeEntity(entity, currentWorld, destWorld, yaw, repositionEntity);
    }

    protected static PortalInfo makeTeleportMachinePortalInfo(Entity entity,BlockPos pos){
        return new PortalInfo(new Vec3(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5),Vec3.ZERO,entity.getYRot(), entity.getXRot());
    }
}
