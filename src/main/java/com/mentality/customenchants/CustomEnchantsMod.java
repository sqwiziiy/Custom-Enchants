package com.mentality.customenchants;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.enchantment.DoubleJumpServerHandler;
import com.mentality.customenchants.enchantment.DrillHandler;
import com.mentality.customenchants.enchantment.LumberjackHandler;
import com.mentality.customenchants.enchantment.MagnetHandler;
import com.mentality.customenchants.enchantment.VegetationHandler;
import com.mentality.customenchants.enchantment.SecondWindHandler;
import com.mentality.customenchants.enchantment.KineticDischargeHandler;
import com.mentality.customenchants.lifecycle.ServerStateLifecycle;
import com.mentality.customenchants.enchantment.SculkBloomHandler;
import com.mentality.customenchants.trade.LibrarianEnchantTrade;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomEnchantsMod implements ModInitializer {

    public static final String MOD_ID = "custom-enchants";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModConfig.load();
        // Enchantments are data-driven (data/custom-enchants/enchantment/*.json); no code registration.
        DoubleJumpServerHandler.register();
        DrillHandler.register();
        LumberjackHandler.register();
        MagnetHandler.register();
        VegetationHandler.register();
        SecondWindHandler.register();
        KineticDischargeHandler.register();
        SculkBloomHandler.register();
        ServerStateLifecycle.register();
        registerVillagerTrades();
        LOGGER.info("Mentalitys | Custom Enchantments initialized!");
    }

    private void registerVillagerTrades() {
        for (LibrarianEnchantTrade offer : LibrarianEnchantTrade.all()) {
            TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, offer.villagerLevel(),
                    factories -> factories.add(
                            (trader, random) -> offer.createOffer(trader.level().registryAccess())));
        }
    }
}
