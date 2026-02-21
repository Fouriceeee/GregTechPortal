package com.ironsword.gtportal.api.portal.teleporter;

import com.ironsword.gtportal.mixin.accessor.TFTeleportAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import twilightforest.world.TFTeleporter;
import twilightforest.world.registration.TFGenerationSettings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;

public class TwilightTeleporter extends GTPTeleporter{
    private Entity entity;

    public TwilightTeleporter(ServerLevel world, BlockPos controllerPos,@Nullable Vec3i pos, Block block, Entity entity) {
        super(world,controllerPos, pos, block);
        this.entity = entity;
    }

    @Override
    protected BlockPos getScaledPos(ServerLevel destWorld, BlockPos currentPos) {
        ServerLevel tfDim = destWorld.getServer().getLevel(TFGenerationSettings.DIMENSION_KEY);
        double scale = tfDim == null ? 0.125D : tfDim.dimensionType().coordinateScale();
        scale = destWorld.dimension().equals(TFGenerationSettings.DIMENSION_KEY) ? 1F / scale : scale;
        return destWorld.getWorldBorder().clampToBounds(currentPos.getX() * scale, currentPos.getY(), currentPos.getZ() * scale);
    }

    @Override
    protected BlockPos searchDestPos(ServerLevel destWorld, BlockPos scaledPos) {

        PortalInfo info = TFTeleportAccessor.callMoveToSafeCoords(destWorld,entity,scaledPos);
        TFTeleportAccessor.callLoadSurroundingArea(destWorld,info.pos);

        BlockPos spot = TFTeleportAccessor.callFindPortalCoords(destWorld,info.pos,
                (blockPos)-> TFTeleportAccessor.callIsIdealForPortal(destWorld, blockPos));
        if (spot != null){
            return spot.above();
        }
        spot = TFTeleportAccessor.callFindPortalCoords(destWorld,info.pos,
                (blockPos)-> TFTeleportAccessor.callIsOkayForPortal(destWorld, blockPos));
        if (spot != null) {
            return spot.above();
        }
        return super.searchDestPos(destWorld,scaledPos);

        //旧的反射写法
//        Class<?> clazz = TFTeleporter.class;
//        try {
//            Method
//                    moveToSafeCoords = clazz.getDeclaredMethod("moveToSafeCoords", ServerLevel.class, Entity.class, BlockPos.class),
//                    findPortalCoords = clazz.getDeclaredMethod("findPortalCoords", ServerLevel.class, Vec3.class, Predicate.class),
//                    isIdealForPortal = clazz.getDeclaredMethod("isIdealForPortal", ServerLevel.class, BlockPos.class),
//                    isOkayForPortal = clazz.getDeclaredMethod("isOkayForPortal", ServerLevel.class, BlockPos.class),
//                    loadSurroundingArea = clazz.getDeclaredMethod("loadSurroundingArea", ServerLevel.class, Vec3.class);
//            moveToSafeCoords.setAccessible(true);
//            findPortalCoords.setAccessible(true);
//            isIdealForPortal.setAccessible(true);
//            isOkayForPortal.setAccessible(true);
//            loadSurroundingArea.setAccessible(true);
//
//            PortalInfo info = (PortalInfo) moveToSafeCoords.invoke(null,destWorld,entity,scaledPos);
//            loadSurroundingArea.invoke(null,destWorld,info.pos);
//
//            Predicate<BlockPos> idealPredicate = (blockPos)-> {try {
//                    return (boolean) isIdealForPortal.invoke(null, destWorld, blockPos);} catch (IllegalAccessException | InvocationTargetException e) {throw new RuntimeException(e);}};
//            Predicate<BlockPos> okayPredicate = (blockPos)-> {try {
//                    return (boolean) isOkayForPortal.invoke(null, destWorld, blockPos);} catch (IllegalAccessException | InvocationTargetException e) {throw new RuntimeException(e);}};
//
//            BlockPos spot = (BlockPos) findPortalCoords.invoke(null,destWorld,info.pos,idealPredicate);
//            if (spot != null)
//                return spot.above();
//
//            spot = (BlockPos) findPortalCoords.invoke(null,destWorld,info.pos,okayPredicate);
//            if (spot!= null)
//                return spot.above();
//
//        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {throw new RuntimeException(e);}
//        return super.searchDestPos(destWorld,scaledPos);
    }
}
