package com.mentality.customenchants.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;

/** Scoped snapshot of the direct player's weapon for lethal damage resolution. */
public final class KillingWeaponContext {
    private static final ThreadLocal<Deque<Snapshot>> SCOPES = ThreadLocal.withInitial(ArrayDeque::new);

    private KillingWeaponContext() {
    }

    public static Scope open(LivingEntity target, DamageSource source) {
        Player player = null;
        ItemStack weapon = ItemStack.EMPTY;
        if (source != null && source.getEntity() instanceof Player candidate
                && KillingWeaponPolicy.directPlayerHit(true, source.getDirectEntity() == candidate)) {
            player = candidate;
            weapon = candidate.getMainHandItem().copy();
        }
        Snapshot snapshot = new Snapshot(target, player, weapon);
        SCOPES.get().push(snapshot);
        return () -> {
            Deque<Snapshot> stack = SCOPES.get();
            if (!stack.isEmpty() && stack.peek() == snapshot) stack.pop();
            else stack.remove(snapshot);
            if (stack.isEmpty()) SCOPES.remove();
        };
    }

    public static Snapshot current(LivingEntity target) {
        Snapshot snapshot = SCOPES.get().peek();
        return snapshot != null && snapshot.target() == target ? snapshot : Snapshot.NONE;
    }

    public record Snapshot(LivingEntity target, Player player, ItemStack weapon) {
        public static final Snapshot NONE = new Snapshot(null, null, ItemStack.EMPTY);

        public boolean isDirectPlayerHit() {
            return target != null && player != null && !weapon.isEmpty();
        }
    }

    public interface Scope extends AutoCloseable {
        @Override
        void close();
    }
}
