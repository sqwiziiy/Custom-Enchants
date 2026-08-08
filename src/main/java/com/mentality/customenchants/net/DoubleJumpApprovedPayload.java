package com.mentality.customenchants.net;

import com.mentality.customenchants.CustomEnchantsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Server authorization for the local client's Double Jump movement. */
public record DoubleJumpApprovedPayload(long sequence, double verticalVelocity,
                                        double horizontalImpulseX, double horizontalImpulseZ)
        implements CustomPacketPayload {

    public static final Type<DoubleJumpApprovedPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(CustomEnchantsMod.MOD_ID, "double_jump_approved"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DoubleJumpApprovedPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG, DoubleJumpApprovedPayload::sequence,
                    ByteBufCodecs.DOUBLE, DoubleJumpApprovedPayload::verticalVelocity,
                    ByteBufCodecs.DOUBLE, DoubleJumpApprovedPayload::horizontalImpulseX,
                    ByteBufCodecs.DOUBLE, DoubleJumpApprovedPayload::horizontalImpulseZ,
                    DoubleJumpApprovedPayload::new);

    @Override
    public Type<DoubleJumpApprovedPayload> type() {
        return TYPE;
    }
}
