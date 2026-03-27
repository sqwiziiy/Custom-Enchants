package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.CustomEnchantsMod;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;

public class SecondWindClientHandler {

    public static final ResourceLocation SECOND_WIND_PACKET = new ResourceLocation(CustomEnchantsMod.MOD_ID, "second_wind");

    private static int effectTicksRemaining = 0;

    public static void register() {
        // Receive server packet to trigger visual effect
        ClientPlayNetworking.registerGlobalReceiver(SECOND_WIND_PACKET, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                effectTicksRemaining = 40; // 2 seconds of particles
                spawnBurstParticles(client);
            });
        });

        // Tick-based particle trail
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (effectTicksRemaining <= 0) return;
            effectTicksRemaining--;

            LocalPlayer player = client.player;
            if (player == null || client.level == null) return;

            // Spawn white smoke particles around the player
            if (effectTicksRemaining % 2 == 0) {
                double x = player.getX();
                double y = player.getY() + 0.5;
                double z = player.getZ();
                for (int i = 0; i < 3; i++) {
                    double offsetX = (player.getRandom().nextDouble() - 0.5) * 1.2;
                    double offsetY = player.getRandom().nextDouble() * 1.5;
                    double offsetZ = (player.getRandom().nextDouble() - 0.5) * 1.2;
                    client.level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            x + offsetX, y + offsetY, z + offsetZ,
                            0, 0.05, 0);
                }
            }
        });
    }

    private static void spawnBurstParticles(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null || client.level == null) return;

        double x = player.getX();
        double y = player.getY() + 0.5;
        double z = player.getZ();

        // Big initial burst of smoke particles
        for (int i = 0; i < 20; i++) {
            double offsetX = (player.getRandom().nextDouble() - 0.5) * 1.5;
            double offsetY = player.getRandom().nextDouble() * 2.0;
            double offsetZ = (player.getRandom().nextDouble() - 0.5) * 1.5;
            double speedX = (player.getRandom().nextDouble() - 0.5) * 0.15;
            double speedZ = (player.getRandom().nextDouble() - 0.5) * 0.15;
            client.level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    x + offsetX, y + offsetY, z + offsetZ,
                    speedX, 0.1, speedZ);
        }

        // Cloud particles at feet
        for (int i = 0; i < 8; i++) {
            double offsetX = (player.getRandom().nextDouble() - 0.5) * 0.8;
            double offsetZ = (player.getRandom().nextDouble() - 0.5) * 0.8;
            client.level.addParticle(ParticleTypes.CLOUD,
                    x + offsetX, player.getY() + 0.1, z + offsetZ,
                    0, 0.02, 0);
        }
    }

    public static int getEffectTicksRemaining() {
        return effectTicksRemaining;
    }
}
