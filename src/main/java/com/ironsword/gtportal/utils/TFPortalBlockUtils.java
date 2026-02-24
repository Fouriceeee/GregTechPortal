package com.ironsword.gtportal.utils;

import com.ironsword.gtportal.GTPConfigHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;
import twilightforest.TFConfig;
import twilightforest.block.TFPortalBlock;
import twilightforest.data.tags.BlockTagGenerator;
import twilightforest.init.TFBlocks;
import twilightforest.util.LandmarkUtil;
import twilightforest.world.TFTeleporter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static twilightforest.block.TFPortalBlock.DISALLOW_RETURN;

public class TFPortalBlockUtils {

    public static boolean tryToCreatePortal(Level level, BlockPos pos, ItemEntity catalyst, @Nullable Player player) {
        BlockState state = level.getBlockState(pos);

        if (canFormPortal(state) && level.getBlockState(pos.below()).isFaceSturdy(level, pos, Direction.UP)) {
            Map<BlockPos, Boolean> blocksChecked = new HashMap<>();
            blocksChecked.put(pos, true);

            MutableInt size = new MutableInt(0);

            if (recursivelyValidatePortal(level, pos, blocksChecked, size, state) && size.intValue() >= 4) {

                if (TFConfig.COMMON_CONFIG.checkPortalDestination.get()) {
                    boolean checkProgression = LandmarkUtil.isProgressionEnforced(catalyst.level());
                    if (!TFTeleporter.isSafeAround(level, pos, catalyst, checkProgression)) {
                        // TODO: "failure" effect - particles?
                        if (player != null) {
                            player.displayClientMessage(Component.translatable("misc.twilightforest.portal_unsafe"), true);
                        }
                        return false;
                    }
                }

                if (!GTPConfigHolder.INSTANCE.portalGateConfigs.allowVanillaTwilightForestPortalGate){
                    if (player != null){
                        player.displayClientMessage(Component.translatable("gtportal.clientmessage.banned_structure"),true);
                    }
                    return false;
                }


                catalyst.getItem().shrink(1);
                causeLightning(level, pos, TFConfig.COMMON_CONFIG.portalLightning.get());

                for (Map.Entry<BlockPos, Boolean> checkedPos : blocksChecked.entrySet()) {
                    if (checkedPos.getValue()) {
                        level.setBlock(checkedPos.getKey(), TFBlocks.TWILIGHT_PORTAL.get().defaultBlockState(), 2);
                    }
                }

                return true;
            }
        }

        return false;
    }

    private static void causeLightning(Level level, BlockPos pos, boolean fake) {
        LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        bolt.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        bolt.setVisualOnly(fake);
        level.addFreshEntity(bolt);

        if (fake && level instanceof ServerLevel) {
            double range = 3.0D;
            List<Entity> list = level.getEntitiesOfClass(Entity.class, new AABB(pos).inflate(range));

            for (Entity victim : list) {
                if (!ForgeEventFactory.onEntityStruckByLightning(victim, bolt)) {
                    victim.thunderHit((ServerLevel) level, bolt);
                }
            }
        }
    }

    private static boolean canFormPortal(BlockState state) {
        return state.is(BlockTagGenerator.PORTAL_POOL) || state.getBlock() instanceof TFPortalBlock && state.getValue(DISALLOW_RETURN);
    }

    private static boolean recursivelyValidatePortal(Level level, BlockPos pos, Map<BlockPos, Boolean> blocksChecked, MutableInt portalSize, BlockState poolBlock) {
        if (portalSize.incrementAndGet() > TFConfig.COMMON_CONFIG.maxPortalSize.get()) return false;

        boolean isPoolProbablyEnclosed = true;

        for (int i = 0; i < 4 && portalSize.intValue() <= TFConfig.COMMON_CONFIG.maxPortalSize.get(); i++) {
            BlockPos positionCheck = pos.relative(Direction.from2DDataValue(i));

            if (!blocksChecked.containsKey(positionCheck)) {
                BlockState state = level.getBlockState(positionCheck);

                if (state == poolBlock && level.getBlockState(positionCheck.below()).isFaceSturdy(level, pos, Direction.UP)) {
                    blocksChecked.put(positionCheck, true);
                    if (isPoolProbablyEnclosed) {
                        isPoolProbablyEnclosed = recursivelyValidatePortal(level, positionCheck, blocksChecked, portalSize, poolBlock);
                    }

                } else if (isGrassOrDirt(state) && isNatureBlock(level.getBlockState(positionCheck.above()))) {
                    blocksChecked.put(positionCheck, false);

                } else return false;
            }
        }

        return isPoolProbablyEnclosed;
    }

    private static boolean isGrassOrDirt(BlockState state) {
        return state.is(BlockTagGenerator.PORTAL_EDGE);
    }

    private static boolean isNatureBlock(BlockState state) {
        return state.is(BlockTagGenerator.PORTAL_DECO);
    }

}
