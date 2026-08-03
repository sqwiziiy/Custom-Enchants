package com.mentality.customenchants.net;

import com.mentality.customenchants.CustomEnchantsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Empty client&rarr;server payload signalling a client-detected double-jump activation.
 * The server independently validates the activation; the payload carries no trusted state.
 */
public record DoubleJumpPayload() implements CustomPacketPayload {

    public static final DoubleJumpPayload INSTANCE = new DoubleJumpPayload();

    public static final Type<DoubleJumpPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CustomEnchantsMod.MOD_ID, "double_jump"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DoubleJumpPayload> CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<DoubleJumpPayload> type() {
        return TYPE;
    }
}
