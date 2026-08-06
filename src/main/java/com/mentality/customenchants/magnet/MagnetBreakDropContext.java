package com.mentality.customenchants.magnet;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;

/** Thread-scoped context for one real Block.playerDestroy drop pipeline. */
public final class MagnetBreakDropContext {
    private static final ThreadLocal<Deque<Context>> STACK = ThreadLocal.withInitial(ArrayDeque::new);

    private MagnetBreakDropContext() {
    }

    public static Scope open(ServerLevel level, ServerPlayer player, BlockPos pos, ItemStack tool) {
        Context context = new Context(level, player, pos.immutable(), tool == null ? null : tool.copy());
        STACK.get().push(context);
        return () -> {
            Deque<Context> stack = STACK.get();
            if (!stack.isEmpty() && stack.peek() == context) stack.pop();
            else stack.remove(context);
            if (stack.isEmpty()) STACK.remove();
        };
    }

    public static Context current(ServerLevel level) {
        Context context = STACK.get().peek();
        return context != null && context.level() == level ? context : null;
    }

    public interface Scope extends AutoCloseable {
        @Override
        void close();
    }

    public record Context(ServerLevel level, ServerPlayer player, BlockPos pos, ItemStack tool) {
    }
}
