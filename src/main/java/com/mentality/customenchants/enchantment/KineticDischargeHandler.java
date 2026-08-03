package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.kinetic.KineticDischargeTargetPolicy;
import com.mentality.customenchants.kinetic.KineticDischargeWearTracker;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class KineticDischargeHandler {

    /** Per-player tracking: was the player fall-flying on the previous tick? */
    private static final Map<UUID, PlayerState> states = new HashMap<>();

    private record PlayerState(boolean wasGliding, Vec3 lastVelocity, ResourceKey<Level> dimension,
                               int activationBaselineDamage, ItemStack trackedElytra) {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!ModConfig.get().kineticDischargeEnabled) return;

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.isDeadOrDying() || player.isSpectator()) {
                    states.remove(player.getUUID());
                    continue;
                }

                ItemStack elytra = player.getItemBySlot(EquipmentSlot.CHEST);
                int level = elytra.isEmpty()
                        ? 0
                        : EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.KINETIC_DISCHARGE, elytra);

                if (level <= 0) {
                    states.remove(player.getUUID());
                    continue;
                }

                PlayerState previous = states.get(player.getUUID());
                boolean sameDimension = previous != null && previous.dimension().equals(player.level().dimension());
                boolean wasFlying = sameDimension && previous.wasGliding();
                Vec3 lastVel = sameDimension ? previous.lastVelocity() : Vec3.ZERO;
                boolean isFlying = player.isFallFlying();
                int baselineDamage = wasFlying ? previous.activationBaselineDamage() : elytra.getDamageValue();
                ItemStack trackedElytra = wasFlying ? previous.trackedElytra() : elytra;
                if (isFlying && !wasFlying) {
                    baselineDamage = elytra.getDamageValue();
                    trackedElytra = elytra;
                }

                // Level III passive: counteract vanilla elytra durability consumption (90% of ticks).
                // Refund only wear above the damage snapshot taken at this flight cycle's start.
                if (level == 3 && isFlying && player.tickCount % 10 == 0) {
                    if (player.getRandom().nextFloat() < 0.90f && trackedElytra == elytra
                            && elytra.getDamageValue() > baselineDamage) {
                        elytra.setDamageValue(KineticDischargeWearTracker.refundOneNewWear(
                                elytra.getDamageValue(), baselineDamage));
                    }
                }

                // Detect transition: was flying → now grounded = landing.
                // Use total 3D vector length so fireworks are reliably required.
                if (wasFlying && !isFlying && player.onGround()) {
                    double speed = lastVel.length();
                    if (speed >= ModConfig.get().kineticDischargeMinSpeed) {
                        triggerShockwave(player, level, elytra, speed);
                    }
                }

                states.put(player.getUUID(), new PlayerState(isFlying, player.getDeltaMovement(),
                        player.level().dimension(), baselineDamage, trackedElytra));
            }
        });
    }

    private static void triggerShockwave(ServerPlayer player, int level, ItemStack elytra, double landingSpeed) {
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
                e -> KineticDischargeTargetPolicy.isEligible(player, e, radius)
        );

        for (LivingEntity target : targets) {
            if (!KineticDischargeTargetPolicy.withinRadius(target.getX() - player.getX(),
                    target.getY() - player.getY(), target.getZ() - player.getZ(), radius)) continue;

            // Knock the entity away from the player
            double dx = target.getX() - player.getX();
            double dz = target.getZ() - player.getZ();
            if (!KineticDischargeTargetPolicy.finiteHorizontalVector(dx, dz)) continue;
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist < 0.001) { dx = 1.0; dz = 0.0; dist = 1.0; }
            target.knockback(knockbackStrength, -dx / dist, -dz / dist);

            // Level III bonus: deal speed-scaled additional damage (up to +15% at high speed).
            if (level == 3) {
                float baseDamage = ModConfig.get().kineticDischargeDamageL3;
                float minSpeed   = ModConfig.get().kineticDischargeMinSpeed;
                // Full +15% bonus is reached 2.5 blocks/tick above the activation threshold.
                float excess      = (float) Math.max(0, landingSpeed - minSpeed);
                float bonusFactor = Math.min(0.15f, excess / 2.5f * 0.15f);
                float actualDamage = baseDamage * (1.0f + bonusFactor);
                target.hurt(player.damageSources().playerAttack(player), actualDamage);
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

    public static void clear(UUID playerId) {
        states.remove(playerId);
    }

    public static void clearAll() {
        states.clear();
    }
}
