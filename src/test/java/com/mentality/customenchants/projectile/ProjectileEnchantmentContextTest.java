package com.mentality.customenchants.projectile;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ProjectileEnchantmentContextTest {
    @Test
    void levelsAreClampedAndNbtRoundTripsAllIndependentFields() {
        ProjectileEnchantmentContext original = new ProjectileEnchantmentContext(-4, 9, 2, 1, "trident");
        CompoundTag parent = new CompoundTag();
        original.save(parent);

        ProjectileEnchantmentContext loaded = ProjectileEnchantmentContext.load(parent);
        assertEquals(0, loaded.skyRage());
        assertEquals(3, loaded.vulnerability());
        assertEquals(2, loaded.shadowBlade());
        assertEquals(1, loaded.glowStrike());
        assertEquals("trident", loaded.weaponType());
    }

    @Test
    void missingOrUnknownSchemaFailsClosed() {
        assertSame(ProjectileEnchantmentContext.EMPTY, ProjectileEnchantmentContext.load(new CompoundTag()));
        CompoundTag parent = new CompoundTag();
        CompoundTag context = new CompoundTag();
        context.putInt("version", 999);
        context.putInt("sky_rage", 3);
        parent.put(ProjectileEnchantmentContext.NBT_KEY, context);
        assertSame(ProjectileEnchantmentContext.EMPTY, ProjectileEnchantmentContext.load(parent));
    }

    @Test
    void unknownWeaponTypeIsRejected() {
        ProjectileEnchantmentContext context = new ProjectileEnchantmentContext(1, 1, 1, 1, "client-injected");
        assertEquals("none", context.weaponType());
    }
}
