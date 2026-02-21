package com.ironsword.gtportal.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import twilightforest.world.TFTeleporter;

import java.util.function.Predicate;

@Mixin(TFTeleporter.class)
public interface TFTeleportAccessor {

    @Invoker
    public static PortalInfo callMoveToSafeCoords(ServerLevel world, Entity entity, BlockPos pos){
        throw new AssertionError();
    }

    @Invoker
    public static @Nullable BlockPos callFindPortalCoords(ServerLevel world, Vec3 loc, Predicate<BlockPos> predicate){
        throw new AssertionError();
    }

    @Invoker
    public static boolean callIsIdealForPortal(ServerLevel world, BlockPos pos){
        throw new AssertionError();
    }

    @Invoker
    public static boolean callIsOkayForPortal(ServerLevel world, BlockPos pos){
        throw new AssertionError();
    }

    @Invoker
    public static void callLoadSurroundingArea(ServerLevel world, Vec3 pos){
        throw new AssertionError();
    }
}
