package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class ShadowBladeEnchantment extends Enchantment {

    public ShadowBladeEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.TRIDENT, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof TridentItem;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return super.checkCompatibility(other)
                && other != Enchantments.CHANNELING
                && other != Enchantments.RIPTIDE
                && !(other instanceof GlowStrikeEnchantment);
    }

    public static void applyShadowBlade(Player player, LivingEntity livingTarget, int level) {
        ModConfig config = ModConfig.get();
        if (!config.shadowBladeEnabled) {
            return;
        }
        float chance = switch (level) {
            case 1 -> config.shadowBladeChanceL1 / 100f;
            case 2 -> config.shadowBladeChanceL2 / 100f;
            case 3 -> config.shadowBladeChanceL3 / 100f;
            default -> config.shadowBladeChanceL1 / 100f;
        };
        int slownessDuration = switch (level) {
            case 1 -> config.shadowBladeSlowDurationL1;
            case 2 -> config.shadowBladeSlowDurationL2;
            case 3 -> config.shadowBladeSlowDurationL3;
            default -> config.shadowBladeSlowDurationL1;
        };

        // Distance bonus: up to +10% at 30 blocks distance
        double distance = player.distanceTo(livingTarget);
        float distanceBonus = (float) (Math.min(distance / 30.0, 1.0) * 0.10);
        chance += distanceBonus;

        if (player.getRandom().nextFloat() < chance) {
            double yawRad = Math.toRadians(livingTarget.getYRot());
            double behindX = livingTarget.getX() + Math.sin(yawRad) * 1.5;
            double behindZ = livingTarget.getZ() - Math.cos(yawRad) * 1.5;
            double behindY = livingTarget.getY();

            // Face the target from behind
            double dx = livingTarget.getX() - behindX;
            double dz = livingTarget.getZ() - behindZ;
            float newYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.teleport(behindX, behindY, behindZ, newYaw, 0);
            } else {
                player.teleportTo(behindX, behindY, behindZ);
                player.setYRot(newYaw);
                player.setXRot(0);
            }

            livingTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, slownessDuration, 1));
        }
    }
}
