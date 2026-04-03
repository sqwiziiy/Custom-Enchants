package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class PoisonBladeEnchantment extends Enchantment {

    public PoisonBladeEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof AxeItem || super.canEnchant(stack);
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return super.checkCompatibility(other)
                && other != Enchantments.FIRE_ASPECT;
    }

    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        ModConfig config = ModConfig.get();
        if (!config.poisonBladeEnabled) {
            return;
        }
        if (attacker instanceof Player && target instanceof LivingEntity livingTarget) {
            int duration = switch (level) {
                case 1 -> config.poisonBladeDurationL1;
                case 2 -> config.poisonBladeDurationL2;
                case 3 -> config.poisonBladeDurationL3;
                default -> config.poisonBladeDurationL1;
            };
            livingTarget.addEffect(new MobEffectInstance(MobEffects.POISON, duration, 0));
        }
        super.doPostAttack(attacker, target, level);
    }
}
