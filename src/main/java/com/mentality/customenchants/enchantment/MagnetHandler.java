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
    private static final Set<UUID> BATCH_CAPTURE = new HashSet<>();
    private static final long REQUEST_EXPIRY_TICKS = 20L;
    private static final boolean DEBUG = Boolean.getBoolean("customenchants.debug.magnet");

    public static void collectNearby(ServerLevel level, ServerPlayer player, BlockPos pos) {
        collectNearby(level, player, pos, Set.of());
    }

    private static void collectNearby(ServerLevel level, ServerPlayer player, BlockPos pos, Set<UUID> existingItems) {
        long now = level.getServer().getTickCount();
        synchronized (PENDING) {
            PENDING.add(new PendingPickup(player.getUUID(), level.dimension(), List.of(pos.immutable()),
                    now, now + 1L, now + REQUEST_EXPIRY_TICKS, new HashSet<>(existingItems), new HashSet<>()));
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
                boolean collected = false;
                for (BlockPos pos : request.positions()) {
                    AABB area = new AABB(pos).inflate(radius);
                    for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, area)) {
                    if (request.existingItems().contains(itemEntity.getUUID()) || request.processedItems().contains(itemEntity.getUUID())) continue;
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
                    request.processedItems().add(itemEntity.getUUID());
                    debug("pickup result tick={} uuid={} before={} after={} removedAfter={}",
                            now, itemEntity.getUUID(), before, itemEntity.getItem(), itemEntity.isRemoved());
                }
                }
                request = request.withDueTick(now + 1L);
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

            synchronized (PENDING) {
                if (BATCH_CAPTURE.contains(serverPlayer.getUUID())) return;
            }

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
            BATCH_CAPTURE.remove(playerId);
        }
    }

    public static void clearAll() {
        synchronized (PENDING) {
            PENDING.clear();
            PRE_BREAK_ITEMS.clear();
            BATCH_CAPTURE.clear();
        }
    }

    /** Test seam that executes the same pending-request processor against a live test server. */
    public static void processPendingForTest(MinecraftServer server, ServerPlayer testPlayer) {
        process(server, testPlayer);
    }

    public static Set<UUID> takePreBreakItems(ServerPlayer player) {
        synchronized (PENDING) {
            return PRE_BREAK_ITEMS.remove(player.getUUID());
        }
    }

    public static void beginBatch(ServerPlayer player) {
        synchronized (PENDING) {
            BATCH_CAPTURE.add(player.getUUID());
        }
    }

    public static void completeBatch(ServerLevel level, ServerPlayer player,
                                     List<BlockPos> successfulPositions, Set<UUID> existingItems) {
        synchronized (PENDING) {
            BATCH_CAPTURE.remove(player.getUUID());
            if (successfulPositions == null || successfulPositions.isEmpty()) return;
            long now = level.getServer().getTickCount();
            Set<UUID> known = new HashSet<>(existingItems == null ? Set.of() : existingItems);
            for (BlockPos position : successfulPositions) {
                PENDING.add(new PendingPickup(player.getUUID(), level.dimension(), List.of(position.immutable()),
                        now, now + 1L, now + REQUEST_EXPIRY_TICKS, new HashSet<>(known), new HashSet<>()));
            }
        }
        debug("batch break request player={} positions={} existingItems={}", player.getUUID(),
                successfulPositions.size(), existingItems == null ? 0 : existingItems.size());
    }

    private record PendingPickup(UUID playerId, ResourceKey<Level> dimension, List<BlockPos> positions,
                                 long startTick, long dueTick, long expiryTick, Set<UUID> existingItems,
                                 Set<UUID> processedItems) {
        private PendingPickup withDueTick(long nextDueTick) {
            return new PendingPickup(playerId, dimension, positions, startTick, nextDueTick, expiryTick,
                    existingItems, processedItems);
        }
    }

    private static void debug(String message, Object... args) {
        if (DEBUG || Boolean.getBoolean("customenchants.debug.magnet")) {
            CustomEnchantsMod.LOGGER.info("[Magnet debug] " + message, args);
        }
    }
}
