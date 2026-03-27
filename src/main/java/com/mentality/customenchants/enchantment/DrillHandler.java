package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class DrillHandler {

    private static boolean isDrilling = false;

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (isDrilling) return;
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            if (!ModConfig.get().drillEnabled) return;
            if (player.isShiftKeyDown()) return;

            ItemStack tool = player.getMainHandItem();
            if (tool.isEmpty()) return;
            if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DRILL, tool) <= 0) return;

            drillAround(serverPlayer, pos);
        });
    }

    public static void drillAround(ServerPlayer player, BlockPos center) {
        Direction face = getMinedFace(player, center);
        isDrilling = true;
        try {
            breakSurroundingBlocks(player, center, face);
        } finally {
            isDrilling = false;
        }
    }

    private static Direction getMinedFace(ServerPlayer player, BlockPos brokenPos) {
        Vec3 eyePos = player.getEyePosition(1.0f);
        double dx = eyePos.x - (brokenPos.getX() + 0.5);
        double dy = eyePos.y - (brokenPos.getY() + 0.5);
        double dz = eyePos.z - (brokenPos.getZ() + 0.5);

        double absDx = Math.abs(dx);
        double absDy = Math.abs(dy);
        double absDz = Math.abs(dz);

        if (absDy >= absDx && absDy >= absDz) {
            return dy > 0 ? Direction.UP : Direction.DOWN;
        } else if (absDx >= absDz) {
            return dx > 0 ? Direction.EAST : Direction.WEST;
        } else {
            return dz > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    private static void breakSurroundingBlocks(ServerPlayer player, BlockPos center, Direction face) {
        Direction[] offsets = getPerpendicularDirections(face);
        Direction dir1 = offsets[0];
        Direction dir2 = offsets[1];

        ServerLevel level = player.serverLevel();
        ItemStack tool = player.getMainHandItem();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; // center already broken

                BlockPos targetPos = center
                        .relative(dir1, i)
                        .relative(dir2, j);

                BlockState targetState = level.getBlockState(targetPos);
                if (targetState.isAir()) continue;
                if (targetState.getDestroySpeed(level, targetPos) < 0) continue; // unbreakable (bedrock etc)
                if (!tool.isCorrectToolForDrops(targetState)) continue;

                boolean smelted = AutoSmeltHandler.trySmeltBlock(level, player, targetPos, targetState, tool);
                if (!smelted) {
                    BlockEntity be = level.getBlockEntity(targetPos);
                    Block.dropResources(targetState, level, targetPos, be, player, tool);
                }
                level.removeBlock(targetPos, false);

                tool.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));

                if (tool.isEmpty()) return;
            }
        }
    }

    private static Direction[] getPerpendicularDirections(Direction face) {
        return switch (face) {
            case UP, DOWN -> new Direction[]{Direction.NORTH, Direction.EAST};
            case NORTH, SOUTH -> new Direction[]{Direction.EAST, Direction.UP};
            case EAST, WEST -> new Direction[]{Direction.NORTH, Direction.UP};
        };
    }
}
