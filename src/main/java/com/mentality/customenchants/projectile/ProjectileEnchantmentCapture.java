package com.mentality.customenchants.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;

/** Scoped shot creation state; stack-based to remain safe under nested callbacks. */
public final class ProjectileEnchantmentCapture {
    private static final ThreadLocal<Deque<Entry>> CURRENT = ThreadLocal.withInitial(ArrayDeque::new);

    private ProjectileEnchantmentCapture() {}

    public static Scope open(Entity owner, ItemStack weapon) {
        Entry entry = new Entry(owner, ProjectileEnchantmentContext.fromWeapon(weapon, owner));
        CURRENT.get().push(entry);
        return () -> {
            Deque<Entry> stack = CURRENT.get();
            if (!stack.isEmpty() && stack.peek() == entry) stack.pop();
            else stack.remove(entry);
            if (stack.isEmpty()) CURRENT.remove();
        };
    }

    public static void attachIfMatching(Entity owner, ProjectileEnchantmentContextHolder projectile) {
        Deque<Entry> stack = CURRENT.get();
        if (!stack.isEmpty() && stack.peek().owner == owner) {
            projectile.customEnchants$setProjectileContext(stack.peek().context);
        }
    }

    public interface Scope extends AutoCloseable { @Override void close(); }
    private record Entry(Entity owner, ProjectileEnchantmentContext context) {}
}
