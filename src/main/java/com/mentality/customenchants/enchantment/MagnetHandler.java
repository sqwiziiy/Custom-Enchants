package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.CustomEnchantsMod;
import com.mentality.customenchants.magnet.MagnetPickupPolicy;
import com.mentality.customenchants.mixin.ItemEntityPickupDelayAccessor;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MagnetHandler {

    private static final List<PendingPickup> PENDING = new ArrayList<>();
    private static final Map<UUID, Set<UUID>> PRE_BREAK_ITEMS = new HashMap<>();
    private static final long REQUEST_EXPIRY_TICKS = 20L;
    private static final boolean DEBUG = Boolean.getBoolean("customenchants.debug.magnet");

    public static void collectNearby(ServerLevel level, ServerPlayer player, BlockPos pos) {
        collectNearby(level, player, pos, Set.of());
    }

    private static void collectNearby(ServerLevel level, ServerPlayer player, BlockPos pos, Set<UUID> existingItems) {
        long now = level.getServer().getTickCount();
        synchronized (PENDING) {
            PENDING.add(new PendingPickup(player.getUUID(), level.dimension(), pos.immutable(),
                    now, now + 1L, now + REQUEST_EXPIRY_TICKS, new HashSet<>(existingItems)));
        }
        debug("break request player={} tick={} pos={} existingItems={} expiry={}",
                player.getUUID(), now, pos, existingItems.size(), now + REQUEST_EXPIRY_TICKS);
    }

    private static void process(MinecraftServer server) {
        process(server, null);
    }

    private static void process(MinecraftServer server, ServerPlayer testPlayer) {
        long now = server.getTickCount();
        synchronized (PENDING) {
            Iterator<PendingPickup> iterator = PENDING.iterator();
            while (iterator.hasNext()) {
                PendingPickup request = iterator.next();
                if (now < request.dueTick()) continue;
                if (now > request.expiryTick()) {
                    debug("request expired player={} startTick={} expiry={}", request.playerId(), request.startTick(), request.expiryTick());
                    iterator.remove();
                    continue;
                }
                ServerPlayer player = server.getPlayerList().getPlayer(request.playerId());
                if (player == null && testPlayer != null && testPlayer.getUUID().equals(request.playerId())) {
                    player = testPlayer;
                }
                ServerLevel level = server.getLevel(request.dimension());
                if (player == null || level == null || player.level() != level || !player.isAlive()) {
                    iterator.remove();
                    continue;
                }
                int radius = ModConfig.get().magnetRadius;
                AABB area = new AABB(request.pos()).inflate(radius);
                boolean collected = false;
                for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, area)) {
                    if (request.existingItems().contains(itemEntity.getUUID())
                            || itemEntity.getAge() > now - request.startTick() + 2L) continue;
                    boolean eligible = MagnetPickupPolicy.eligibleCurrentDrop(itemEntity, player, radius);
                    debug("candidate tick={} uuid={} stack={} age={} pickupDelay={} eligible={} removedBefore={}",
                            now, itemEntity.getUUID(), itemEntity.getItem(), itemEntity.getAge(),
                            ((ItemEntityPickupDelayAccessor) itemEntity).customEnchants$getPickupDelay(), eligible, itemEntity.isRemoved());
                    debug("candidate details sameLevel={} alive={} empty={} distanceSq={} playerPos={} itemPos={} radius={}",
                            itemEntity.level() == player.level(), itemEntity.isAlive(), itemEntity.getItem().isEmpty(),
                            itemEntity.distanceToSqr(player), player.position(), itemEntity.position(), radius);
                    if (!eligible) continue;

                    // This is the newly spawned drop associated with this break. Vanilla's
                    // initial delay is not meaningful for Magnet; use the normal playerTouch
                    // inventory path after removing only this linked entity's delay.
                    itemEntity.setPickUpDelay(0);
                    ItemStack before = itemEntity.getItem().copy();
                    itemEntity.playerTouch(player);
                    collected = collected || itemEntity.isRemoved() || !ItemStack.matches(before, itemEntity.getItem());
                    debug("pickup result tick={} uuid={} before={} after={} removedAfter={}",
                            now, itemEntity.getUUID(), before, itemEntity.getItem(), itemEntity.isRemoved());
                }
                if (collected) iterator.remove();
                else request = request.withDueTick(now + 1L);
            }
        }
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(MagnetHandler::process);
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayer serverPlayer) || !(world instanceof ServerLevel level)
                    || !ModConfig.get().magnetEnabled) return true;
            int radius = ModConfig.get().magnetRadius;
            Set<UUID> existing = new HashSet<>();
            for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(radius))) {
                existing.add(item.getUUID());
            }
            synchronized (PENDING) {
                PRE_BREAK_ITEMS.put(serverPlayer.getUUID(), existing);
            }
            return true;
        });
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            if (!(world instanceof ServerLevel serverLevel)) return;
            if (!ModConfig.get().magnetEnabled) return;

            ItemStack tool = player.getMainHandItem();
            if (tool.isEmpty()) return;
            if (EnchantmentAccess.getLevel(tool, ModEnchantments.MAGNET) <= 0) return;

            Set<UUID> existing;
            synchronized (PENDING) {
                existing = PRE_BREAK_ITEMS.remove(serverPlayer.getUUID());
            }
            collectNearby(serverLevel, serverPlayer, pos, existing == null ? Set.of() : existing);
        });
    }

    public static void clear(UUID playerId) {
        synchronized (PENDING) {
            PENDING.removeIf(request -> request.playerId().equals(playerId));
            PRE_BREAK_ITEMS.remove(playerId);
        }
    }

    public static void clearAll() {
        synchronized (PENDING) {
            PENDING.clear();
            PRE_BREAK_ITEMS.clear();
        }
    }

    /** Test seam that executes the same pending-request processor against a live test server. */
    public static void processPendingForTest(MinecraftServer server, ServerPlayer testPlayer) {
        process(server, testPlayer);
    }

    private record PendingPickup(UUID playerId, ResourceKey<Level> dimension, BlockPos pos,
                                 long startTick, long dueTick, long expiryTick, Set<UUID> existingItems) {
        private PendingPickup withDueTick(long nextDueTick) {
            return new PendingPickup(playerId, dimension, pos, startTick, nextDueTick, expiryTick, existingItems);
        }
    }

    private static void debug(String message, Object... args) {
        if (DEBUG || Boolean.getBoolean("customenchants.debug.magnet")) {
            CustomEnchantsMod.LOGGER.info("[Magnet debug] " + message, args);
        }
    }
}
