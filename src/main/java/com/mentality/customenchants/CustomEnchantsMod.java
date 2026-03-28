package com.mentality.customenchants;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.enchantment.DoubleJumpServerHandler;
import com.mentality.customenchants.enchantment.DrillHandler;
import com.mentality.customenchants.enchantment.LumberjackHandler;
import com.mentality.customenchants.enchantment.MagnetHandler;
import com.mentality.customenchants.enchantment.AutoSmeltHandler;
import com.mentality.customenchants.enchantment.VegetationHandler;
import com.mentality.customenchants.enchantment.SecondWindHandler;
import com.mentality.customenchants.enchantment.ModEnchantments;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomEnchantsMod implements ModInitializer {

    public static final String MOD_ID = "custom-enchants";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModConfig.load();
        ModEnchantments.register();
        DoubleJumpServerHandler.register();
        DrillHandler.register();
        LumberjackHandler.register();
        MagnetHandler.register();
        AutoSmeltHandler.register();
        VegetationHandler.register();
        SecondWindHandler.register();
        registerVillagerTrades();
        LOGGER.info("Mentalitys | Custom Enchantments initialized!");
    }

    private void registerVillagerTrades() {
        ModConfig config = ModConfig.get();

        // Glow Strike I — Novice Librarian (tier 1)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 1, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.GLOW_STRIKE, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 10),
                        book,
                        12, 5, 0.2f);
            });
        });

        // Glow Strike II — Journeyman Librarian (tier 3)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 3, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.GLOW_STRIKE, 2));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 28),
                        book,
                        6, 15, 0.2f);
            });
        });

        // Glow Strike III — Master Librarian (tier 5)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.GLOW_STRIKE, 3));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 48),
                        book,
                        3, 30, 0.2f);
            });
        });

        // Double Jump I — Expert Librarian (tier 4), rare
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.DOUBLE_JUMP, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 38),
                        book,
                        3, 25, 0.2f);
            });
        });

        // Drill I — Master Librarian (tier 5), very rare
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.DRILL, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 50),
                        book,
                        2, 30, 0.2f);
            });
        });

        // Poison Blade I — Novice Librarian (tier 1)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 1, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.POISON_BLADE, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 12),
                        book,
                        12, 5, 0.2f);
            });
        });

        // Poison Blade II — Journeyman Librarian (tier 3)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 3, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.POISON_BLADE, 2));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 30),
                        book,
                        6, 15, 0.2f);
            });
        });

        // Poison Blade III — Master Librarian (tier 5)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.POISON_BLADE, 3));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 50),
                        book,
                        3, 30, 0.2f);
            });
        });

        // Lumberjack I — Apprentice Librarian (tier 2)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 2, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.LUMBERJACK, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 14),
                        book,
                        12, 5, 0.2f);
            });
        });

        // Lumberjack II — Expert Librarian (tier 4)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.LUMBERJACK, 2));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 32),
                        book,
                        6, 15, 0.2f);
            });
        });

        // Lumberjack III — Master Librarian (tier 5)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.LUMBERJACK, 3));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 52),
                        book,
                        3, 30, 0.2f);
            });
        });

        // Shadow Blade I — Apprentice Librarian (tier 2)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 2, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.SHADOW_BLADE, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 16),
                        book,
                        12, 5, 0.2f);
            });
        });

        // Shadow Blade II — Expert Librarian (tier 4)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.SHADOW_BLADE, 2));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 34),
                        book,
                        6, 15, 0.2f);
            });
        });

        // Shadow Blade III — Master Librarian (tier 5)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.SHADOW_BLADE, 3));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 54),
                        book,
                        3, 30, 0.2f);
            });
        });

        // Magnet I — Journeyman Librarian (tier 3)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 3, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.MAGNET, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 24),
                        book,
                        6, 15, 0.2f);
            });
        });

        // Auto Smelt I — Expert Librarian (tier 4)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.AUTO_SMELT, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 32),
                        book,
                        6, 15, 0.2f);
            });
        });

        // Vegetation I — Apprentice Librarian (tier 2)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 2, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.VEGETATION, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 14),
                        book,
                        12, 5, 0.2f);
            });
        });

        // Vegetation II — Expert Librarian (tier 4)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.VEGETATION, 2));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 30),
                        book,
                        6, 15, 0.2f);
            });
        });

        // Vegetation III — Master Librarian (tier 5)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.VEGETATION, 3));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 48),
                        book,
                        3, 30, 0.2f);
            });
        });

        // Rebound I — Apprentice Librarian (tier 2)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 2, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.REBOUND, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 16),
                        book,
                        12, 5, 0.2f);
            });
        });

        // Rebound II — Expert Librarian (tier 4)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.REBOUND, 2));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 32),
                        book,
                        6, 15, 0.2f);
            });
        });

        // Rebound III — Master Librarian (tier 5)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.REBOUND, 3));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 50),
                        book,
                        3, 30, 0.2f);
            });
        });

        // Feedback I — Journeyman Librarian (tier 3)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 3, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.FEEDBACK, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 22),
                        book,
                        6, 15, 0.2f);
            });
        });

        // Second Wind I — Master Librarian (tier 5)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.SECOND_WIND, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 52),
                        book,
                        2, 30, 0.2f);
            });
        });

        // Guardian's Grace I — Apprentice Librarian (tier 2)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 2, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.GUARDIANS_GRACE, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 16),
                        book,
                        12, 5, 0.2f);
            });
        });

        // Guardian's Grace II — Expert Librarian (tier 4)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.GUARDIANS_GRACE, 2));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 32),
                        book,
                        6, 15, 0.2f);
            });
        });

        // Guardian's Grace III — Master Librarian (tier 5)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.GUARDIANS_GRACE, 3));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 50),
                        book,
                        3, 30, 0.2f);
            });
        });

        // Vulnerability I — Apprentice Librarian (tier 2)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 2, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.VULNERABILITY, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 18),
                        book,
                        12, 5, 0.2f);
            });
        });

        // Vulnerability II — Expert Librarian (tier 4)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.VULNERABILITY, 2));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 36),
                        book,
                        6, 15, 0.2f);
            });
        });

        // Vulnerability III — Master Librarian (tier 5)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.VULNERABILITY, 3));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 52),
                        book,
                        3, 30, 0.2f);
            });
        });

        // Tether Master I — Novice Librarian (tier 1)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 1, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.TETHER_MASTER, 1));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 10),
                        book,
                        12, 5, 0.2f);
            });
        });

        // Tether Master II — Journeyman Librarian (tier 3)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 3, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.TETHER_MASTER, 2));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 22),
                        book,
                        6, 15, 0.2f);
            });
        });

        // Tether Master III — Expert Librarian (tier 4)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
            factories.add((trader, random) -> {
                ItemStack book = EnchantedBookItem.createForEnchantment(
                        new EnchantmentInstance(ModEnchantments.TETHER_MASTER, 3));
                return new MerchantOffer(
                        new ItemStack(Items.EMERALD, 36),
                        book,
                        6, 15, 0.2f);
            });
        });
    }
}
