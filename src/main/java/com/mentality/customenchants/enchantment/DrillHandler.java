package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.service.AdditionalBlockBreakService;
import com.mentality.customenchants.util.DrillBlockPlanner;
import com.mentality.customenchants.util.DrillFaceResolver;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class DrillHandler {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            if (AdditionalBlockBreakService.isChainActive(serverPlayer)) return;
            if (!ModConfig.get().drillEnabled || player.isShiftKeyDown()) return;

            ItemStack tool = player.getMainHandItem();
            if (tool.isEmpty() || EnchantmentAccess.getLevel(tool, ModEnchantments.DRILL) <= 0) return;
            drillAround(serverPlayer, pos);
        });
    }

    public static void drillAround(ServerPlayer player, BlockPos center) {
        if (player == null || center == null) return;
        Direction face = DrillFaceResolver.resolve(player.getEyePosition(1.0f), player.getLookAngle(), center);
        if (face == null) return;
        AdditionalBlockBreakService.destroyPlanned(player, DrillBlockPlanner.plan(center, face));
    }
}
