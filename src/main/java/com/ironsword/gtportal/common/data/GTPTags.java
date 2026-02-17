package com.ironsword.gtportal.common.data;

import com.ironsword.gtportal.GTPortal;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class GTPTags {

    public static final TagKey<Block> PORTAL_BLOCKS = TagKey.create(BuiltInRegistries.BLOCK.key(), GTPortal.id("portal_blocks"));
}
