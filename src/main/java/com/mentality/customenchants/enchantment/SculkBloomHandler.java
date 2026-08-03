package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.combat.KillingWeaponContext;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.SculkSpreader;

public class SculkBloomHandler {

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register(SculkBloomHandler::onEntityDeath);
    }

    private static void onEntityDeath(LivingEntity entity, DamageSource source) {
        if (!ModConfig.get().sculkBloomEnabled) return;
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        KillingWeaponContext.Snapshot killing = KillingWeaponContext.current(entity);
        if (!killing.isDirectPlayerHit()) return;
        Player player = killing.player();

        int level = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.SCULK_BLOOM,
                killing.weapon()
        );
        if (level <= 0) return;

        BlockPos deathPos = entity.blockPosition();

        // Level I: small bloom — 8 charges, 12 update steps
        // Level II: large bloom — 20 charges, 28 update steps
        int charges   = level == 1 ? 8  : 20;
        int iterations = level == 1 ? 12 : 28;

        SculkSpreader spreader = SculkSpreader.createLevelSpreader();
        spreader.addCursors(deathPos, charges);
        for (int i = 0; i < iterations; i++) {
            spreader.updateCursors(serverLevel, deathPos, serverLevel.getRandom(), true);
        }
    }
}
