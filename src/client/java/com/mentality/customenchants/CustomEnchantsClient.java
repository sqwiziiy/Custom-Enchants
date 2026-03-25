package com.mentality.customenchants;

import com.mentality.customenchants.enchantment.DoubleJumpHandler;
import net.fabricmc.api.ClientModInitializer;

public class CustomEnchantsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		DoubleJumpHandler.register();
	}
}