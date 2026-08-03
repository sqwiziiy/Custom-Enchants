package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.service.AdditionalBlockBreakService;
import com.mentality.customenchants.util.DrillBlockPlanner;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

public class DrillHandler {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            if (AdditionalBlockBreakService.isChainActive(serverPlayer)) return;
            if (!ModConfig.get().drillEnabled || player.isShiftKeyDown()) return;

            ItemStack tool = player.getMainHandItem();
            if (tool.isEmpty() || EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DRILL, tool) <= 0) return;
            drillAround(serverPlayer, pos);
        });
    }

    public static void drillAround(ServerPlayer player, BlockPos center) {
        if (player == null || center == null) return;
        Direction face = getMinedFace(player, center);
        AdditionalBlockBreakService.destroyPlanned(player, DrillBlockPlanner.plan(center, face));
    }

    private static Direction getMinedFace(ServerPlayer player, BlockPos brokenPos) {
        Vec3 eyePos = player.getEyePosition(1.0f);
        double dx = eyePos.x - (brokenPos.getX() + 0.5);
        double dy = eyePos.y - (brokenPos.getY() + 0.5);
        double dz = eyePos.z - (brokenPos.getZ() + 0.5);
        double absDx = Math.abs(dx);
        double absDy = Math.abs(dy);
        double absDz = Math.abs(dz);

        if (absDy >= absDx && absDy >= absDz) return dy > 0 ? Direction.UP : Direction.DOWN;
        if (absDx >= absDz) return dx > 0 ? Direction.EAST : Direction.WEST;
        return dz > 0 ? Direction.SOUTH : Direction.NORTH;
    }
}
