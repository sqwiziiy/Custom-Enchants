package com.mentality.customenchants.projectile;

import net.minecraft.nbt.CompoundTag;

public final class ProjectileEnchantmentContextNbt {
    private ProjectileEnchantmentContextNbt() {}
    public static void save(CompoundTag tag, ProjectileEnchantmentContext context) { context.save(tag); }
    public static ProjectileEnchantmentContext load(CompoundTag tag) { return ProjectileEnchantmentContext.load(tag); }
}
