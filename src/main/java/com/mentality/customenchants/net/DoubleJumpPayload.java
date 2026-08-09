package com.mentality.customenchants.net;

import com.mentality.customenchants.CustomEnchantsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Client-to-server Double Jump activation request.
 * The sprint flag is only a movement hint; the server still validates every activation.
 */
public record DoubleJumpPayload(boolean sprinting, float yawDegrees) implements CustomPacketPayload {

    public static final Type<DoubleJumpPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(CustomEnchantsMod.MOD_ID, "double_jump"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DoubleJumpPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, DoubleJumpPayload::sprinting,
                    ByteBufCodecs.FLOAT, DoubleJumpPayload::yawDegrees,
                    DoubleJumpPayload::new);

    @Override
    public Type<DoubleJumpPayload> type() {
        return TYPE;
    }
}
