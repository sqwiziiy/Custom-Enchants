package com.mentality.customenchants.shield;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;

/** Per-hurt-call evidence of the vanilla shield-block decision. */
public final class ShieldBlockContext {
    private static final ThreadLocal<Deque<Call>> CALLS = ThreadLocal.withInitial(ArrayDeque::new);
    private ShieldBlockContext() {}

    public static Scope open(LivingEntity defender, DamageSource source, float amount) {
        Call call = new Call(defender, source, amount, defender.getUseItem());
        CALLS.get().push(call);
        return () -> {
            Deque<Call> stack = CALLS.get();
            if (!stack.isEmpty() && stack.peek() == call) stack.pop(); else stack.remove(call);
            if (stack.isEmpty()) CALLS.remove();
        };
    }

    public static void recordVanillaBlock(LivingEntity defender, DamageSource source, boolean blocked) {
        Call call = CALLS.get().peek();
        if (call != null && call.defender == defender && call.source == source && blocked) {
            call.vanillaBlocked = true;
        }
    }

    public static Evidence current(LivingEntity defender, DamageSource source) {
        Call call = CALLS.get().peek();
        if (call == null || call.defender != defender || call.source != source) return Evidence.NONE;
        return new Evidence(call.defender, call.shield, call.source, call.source.getDirectEntity(),
                call.source.getEntity(), call.amount, call.vanillaBlocked);
    }

    public record Evidence(LivingEntity defender, ItemStack shield, DamageSource source,
                           Entity directEntity, Entity causingEntity, float amount, boolean vanillaBlocked) {
        public static final Evidence NONE = new Evidence(null, ItemStack.EMPTY, null, null, null, 0, false);
        public boolean isValid() { return defender != null && source != null && !shield.isEmpty(); }
    }

    public interface Scope extends AutoCloseable { @Override void close(); }
    private static final class Call {
        private final LivingEntity defender;
        private final DamageSource source;
        private final float amount;
        private final ItemStack shield;
        private boolean vanillaBlocked;
        private Call(LivingEntity defender, DamageSource source, float amount, ItemStack shield) {
            this.defender = defender;
            this.source = source;
            this.amount = amount;
            this.shield = shield;
        }
    }
}
