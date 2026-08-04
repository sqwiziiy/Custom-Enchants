package com.mentality.customenchants.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public final class AutoSmeltBreakContext {

    private static final ThreadLocal<Deque<Context>> SCOPES = ThreadLocal.withInitial(ArrayDeque::new);

    private AutoSmeltBreakContext() {
    }

    public static Scope open(ServerPlayer player, ServerLevel level, BlockPos pos, BlockState state,
                             BlockEntity blockEntity, ItemStack tool) {
        Context context = new Context(player, level, pos.immutable(), state, blockEntity, tool);
        SCOPES.get().push(context);
        return new Scope(context);
    }

    public static boolean consume(ServerLevel level, BlockPos pos, BlockState state,
                                  BlockEntity blockEntity, Entity breaker, ItemStack tool) {
        Deque<Context> scopes = SCOPES.get();
        Context context = scopes.peek();
        if (context == null || context.consumed
                || context.level != level
                || context.player != breaker
                || !context.pos.equals(pos)
                || !Objects.equals(context.state, state)
                || context.blockEntity != blockEntity
                || !sameTool(context.tool, tool)) {
            return false;
        }
        context.consumed = true;
        return true;
    }

    private static boolean sameTool(ItemStack expected, ItemStack actual) {
        if (expected == null || actual == null) return expected == actual;
        return ItemStack.isSameItemSameComponents(expected, actual);
    }

    public static final class Scope implements AutoCloseable {
        private final Context context;
        private boolean closed;

        private Scope(Context context) {
            this.context = context;
        }

        @Override
        public void close() {
            if (closed) return;
            closed = true;
            Deque<Context> scopes = SCOPES.get();
            if (scopes.peek() == context) scopes.pop();
            else scopes.remove(context);
            if (scopes.isEmpty()) SCOPES.remove();
        }
    }

    private static final class Context {
        private final ServerPlayer player;
        private final ServerLevel level;
        private final BlockPos pos;
        private final BlockState state;
        private final BlockEntity blockEntity;
        private final ItemStack tool;
        private boolean consumed;

        private Context(ServerPlayer player, ServerLevel level, BlockPos pos, BlockState state,
                        BlockEntity blockEntity, ItemStack tool) {
            this.player = player;
            this.level = level;
            this.pos = pos;
            this.state = state;
            this.blockEntity = blockEntity;
            this.tool = tool;
        }
    }
}
