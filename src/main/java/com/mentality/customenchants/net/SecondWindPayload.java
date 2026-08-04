package com.mentality.customenchants.net;

import com.mentality.customenchants.CustomEnchantsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Empty server&rarr;client payload triggering the Second Wind client-side visual burst. */
public record SecondWindPayload() implements CustomPacketPayload {

    public static final SecondWindPayload INSTANCE = new SecondWindPayload();

    public static final Type<SecondWindPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(CustomEnchantsMod.MOD_ID, "second_wind"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SecondWindPayload> CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<SecondWindPayload> type() {
        return TYPE;
    }
}
