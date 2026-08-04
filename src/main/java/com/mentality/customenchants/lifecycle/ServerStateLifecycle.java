package com.mentality.customenchants.lifecycle;

import com.mentality.customenchants.enchantment.DoubleJumpServerHandler;
import com.mentality.customenchants.enchantment.KineticDischargeHandler;
import com.mentality.customenchants.enchantment.MagnetHandler;
import com.mentality.customenchants.enchantment.SecondWindHandler;
import com.mentality.customenchants.enchantment.SkyRageEnchantment;
import com.mentality.customenchants.enchantment.VegetationHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

/** One lifecycle registration for all transient server state owned by the mod. */
public final class ServerStateLifecycle {
    private ServerStateLifecycle() {
    }

    public static void register() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.player;
            DoubleJumpServerHandler.clear(player.getUUID());
            SecondWindHandler.clear(player.getUUID());
            KineticDischargeHandler.clear(player.getUUID());
            MagnetHandler.clear(player.getUUID());
            SkyRageEnchantment.clear(player.getUUID());
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            VegetationHandler.clearState();
            DoubleJumpServerHandler.clearAll();
            SecondWindHandler.clearAll();
            KineticDischargeHandler.clearAll();
            MagnetHandler.clearAll();
            SkyRageEnchantment.clearAll();
        });
    }
}
