package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class XpSyphonEnchantment extends Enchantment {

    public XpSyphonEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinCost(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 15;
    }

    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        if (!ModConfig.get().xpSyphonEnabled) return;
        if (!(attacker instanceof Player)) return;
        if (!(target instanceof LivingEntity)) return;
        if (!(attacker.level() instanceof ServerLevel serverLevel)) return;

        float chance = switch (level) {
            case 1 -> 0.05f;
            case 2 -> 0.10f;
            default -> 0.15f;
        };

        if (serverLevel.getRandom().nextFloat() >= chance) return;

        // Drop XP orbs at the target's position (1/2/3 XP per level)
        serverLevel.addFreshEntity(new ExperienceOrb(
                serverLevel,
                target.getX(), target.getY() + 0.5, target.getZ(),
                level
        ));
    }
}
