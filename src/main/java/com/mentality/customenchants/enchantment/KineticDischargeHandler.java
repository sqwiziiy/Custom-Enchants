package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KineticDischargeHandler {

    /** Per-player tracking: was the player fall-flying on the previous tick? */
    private static final Map<UUID, Boolean> wasGliding = new HashMap<>();

    /** Per-player tracking: velocity on the previous tick (to measure landing speed). */
    private static final Map<UUID, Vec3> lastVelocity = new HashMap<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!ModConfig.get().kineticDischargeEnabled) return;

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.isDeadOrDying() || player.isSpectator()) continue;

                ItemStack elytra = player.getItemBySlot(EquipmentSlot.CHEST);
                int level = elytra.isEmpty()
                        ? 0
                        : EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.KINETIC_DISCHARGE, elytra);

                if (level <= 0) {
                    wasGliding.remove(player.getUUID());
                    lastVelocity.remove(player.getUUID());
                    continue;
                }

                boolean wasFlying = wasGliding.getOrDefault(player.getUUID(), false);
                Vec3 lastVel    = lastVelocity.getOrDefault(player.getUUID(), Vec3.ZERO);
                boolean isFlying = player.isFallFlying();

                // Level III passive: counteract vanilla elytra durability consumption (90% of ticks).
                // Vanilla damages the elytra by 1 every 10 ticks during flight; we repair 1 back
                // 90 % of those ticks, resulting in net ~-90 % durability drain.
                if (level == 3 && isFlying && player.tickCount % 10 == 0) {
                    if (player.getRandom().nextFloat() < 0.90f && elytra.getDamageValue() > 0) {
                        elytra.setDamageValue(elytra.getDamageValue() - 1);
                    }
                }

                // Detect transition: was flying → now grounded = landing
                if (wasFlying && !isFlying && player.onGround()) {
                    double speed = lastVel.horizontalDistance();
                    if (speed >= ModConfig.get().kineticDischargeMinSpeed) {
                        triggerShockwave(player, level, elytra);
                    }
                }

                wasGliding.put(player.getUUID(), isFlying);
                lastVelocity.put(player.getUUID(), player.getDeltaMovement());
            }
        });
    }

    private static void triggerShockwave(ServerPlayer player, int level, ItemStack elytra) {
        double radius = switch (level) {
            case 1  -> 3.0;
            case 2  -> 5.0;
            default -> 7.0; // level 3
        };

        float knockbackStrength = switch (level) {
            case 1  -> ModConfig.get().kineticDischargeKnockbackL1;
            case 2  -> ModConfig.get().kineticDischargeKnockbackL2;
            default -> ModConfig.get().kineticDischargeKnockbackL3;
        };

        // Collect all nearby living entities, excluding the player themselves
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class,
                new AABB(player.position(), player.position()).inflate(radius),
                e -> e != player && e.isAlive()
        );

        for (LivingEntity target : targets) {
            if (target.distanceToSqr(player) > radius * radius) continue;

            // Knock the entity away from the player
            double dx = target.getX() - player.getX();
            double dz = target.getZ() - player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist < 0.001) { dx = 1.0; dz = 0.0; dist = 1.0; }
            target.knockback(knockbackStrength, -dx / dist, -dz / dist);

            // Level III bonus: deal additional damage
            if (level == 3) {
                target.hurt(player.damageSources().playerAttack(player),
                        ModConfig.get().kineticDischargeDamageL3);
            }
        }

        // Visual feedback: central explosion + expanding ring of particles
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.EXPLOSION_EMITTER,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    1, 0, 0, 0, 0
            );
            int ringCount = 20;
            for (int i = 0; i < ringCount; i++) {
                double angle = (Math.PI * 2.0 / ringCount) * i;
                double px = player.getX() + radius * Math.cos(angle);
                double pz = player.getZ() + radius * Math.sin(angle);
                serverLevel.sendParticles(
                        ParticleTypes.POOF,
                        px, player.getY() + 0.1, pz,
                        3, 0.2, 0.1, 0.2, 0.05
                );
            }
        }

        // Elytra durability cost per shockwave activation.
        // At level III: 90 % chance to skip the cost (enchantment bonus).
        boolean consumeDurability = level < 3 || player.getRandom().nextFloat() < 0.10f;
        if (consumeDurability && !elytra.isEmpty()) {
            elytra.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.CHEST));
        }
    }
}
