package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.magnet.MagnetPickupPolicy;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class MagnetHandler {

    private static final List<PendingPickup> PENDING = new ArrayList<>();
    private static final long REQUEST_EXPIRY_TICKS = 2L;

    public static void collectNearby(ServerLevel level, ServerPlayer player, BlockPos pos) {
        long now = level.getServer().getTickCount();
        synchronized (PENDING) {
            PENDING.add(new PendingPickup(player.getUUID(), level.dimension(), pos.immutable(), now + 1L,
                    now + REQUEST_EXPIRY_TICKS));
        }
    }

    private static void process(MinecraftServer server) {
        long now = server.getTickCount();
        synchronized (PENDING) {
            Iterator<PendingPickup> iterator = PENDING.iterator();
            while (iterator.hasNext()) {
                PendingPickup request = iterator.next();
                if (now < request.dueTick()) continue;
                iterator.remove();
                if (now > request.expiryTick()) continue;
                ServerPlayer player = server.getPlayerList().getPlayer(request.playerId());
                ServerLevel level = server.getLevel(request.dimension());
                if (player == null || level == null || player.level() != level || !player.isAlive()) continue;
                int radius = ModConfig.get().magnetRadius;
                for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class,
                        new AABB(request.pos()).inflate(radius))) {
                    if (MagnetPickupPolicy.eligible(itemEntity, player, radius)) itemEntity.playerTouch(player);
                }
            }
        }
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(MagnetHandler::process);
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            if (!(world instanceof ServerLevel serverLevel)) return;
            if (!ModConfig.get().magnetEnabled) return;

            ItemStack tool = player.getMainHandItem();
            if (tool.isEmpty()) return;
            if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MAGNET, tool) <= 0) return;

            collectNearby(serverLevel, serverPlayer, pos);
        });
    }

    public static void clear(UUID playerId) {
        synchronized (PENDING) { PENDING.removeIf(request -> request.playerId().equals(playerId)); }
    }

    public static void clearAll() {
        synchronized (PENDING) { PENDING.clear(); }
    }

    private record PendingPickup(UUID playerId, ResourceKey<Level> dimension, BlockPos pos,
                                 long dueTick, long expiryTick) { }
}
