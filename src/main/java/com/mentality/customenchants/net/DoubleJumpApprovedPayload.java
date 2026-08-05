package com.mentality.customenchants.net;

import com.mentality.customenchants.CustomEnchantsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Server authorization for the local client's vertical Double Jump prediction. */
public record DoubleJumpApprovedPayload(double verticalVelocity) implements CustomPacketPayload {
    public static final Type<DoubleJumpApprovedPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CustomEnchantsMod.MOD_ID, "double_jump_approved"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DoubleJumpApprovedPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, DoubleJumpApprovedPayload::verticalVelocity, DoubleJumpApprovedPayload::new);

    @Override
    public Type<DoubleJumpApprovedPayload> type() {
        return TYPE;
    }
}
