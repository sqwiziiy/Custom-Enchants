package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class GlowStrikeEnchantment extends Enchantment {

    public GlowStrikeEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return super.checkCompatibility(other) && other != Enchantments.KNOCKBACK;
    }

    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        ModConfig config = ModConfig.get();
        if (!config.glowStrikeEnabled) {
            return;
        }
        if (attacker instanceof Player && target instanceof LivingEntity livingTarget) {
            int duration = switch (level) {
                case 1 -> config.glowStrikeDurationL1;
                case 2 -> config.glowStrikeDurationL2;
                case 3 -> config.glowStrikeDurationL3;
                default -> config.glowStrikeDurationL1;
            };
            livingTarget.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration, 0));
        }
        super.doPostAttack(attacker, target, level);
    }
}
