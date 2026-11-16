package com.mentality.customenchants.state;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/** A block position whose identity is scoped to a dimension. */
public record WorldPositionKey(ResourceKey<Level> dimension, long position) {
}
