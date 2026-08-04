package com.mentality.customenchants.gametest;

import com.mentality.customenchants.enchantment.EnchantmentAccess;
import com.mentality.customenchants.enchantment.ModEnchantments;
import com.mentality.customenchants.projectile.ProjectileEnchantmentContextHolder;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

public final class CustomEnchantsGameTests {

    @GameTest(maxTicks = 100)
    public void registryAndRealWorldAreAvailable(GameTestHelper helper) {
        helper.assertTrue(
                EnchantmentAccess.resolve(ModEnchantments.AUTO_SMELT, helper.getLevel().registryAccess()).isPresent(),
                "Auto Smelt must be present in the data-driven enchantment registry");
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, Blocks.IRON_ORE);
        helper.assertBlockPresent(Blocks.IRON_ORE, pos);
        helper.succeed();
    }

    @GameTest(maxTicks = 100)
    public void realEnchantedBookUsesRegisteredEnchantments(GameTestHelper helper) {
        Holder<Enchantment> holder = EnchantmentAccess
                .resolve(ModEnchantments.AUTO_SMELT, helper.getLevel().registryAccess())
                .orElseThrow();
        ItemStack book = EnchantmentHelper.createBook(new EnchantmentInstance(holder, 1));
        ItemEnchantments stored = book.get(DataComponents.STORED_ENCHANTMENTS);
        helper.assertTrue(stored != null
                        && stored.getLevel(holder) == 1
                        && stored.keySet().stream().anyMatch(h -> h.is(ModEnchantments.AUTO_SMELT)),
                "Real enchanted book must retain the registered Auto Smelt level");
        helper.succeed();
    }

    /** Runtime proof that all 19 custom enchantment keys resolve in the real server registry. */
    @GameTest(maxTicks = 100)
    public void allNineteenEnchantmentsResolveInRuntimeRegistry(GameTestHelper helper) {
        var access = helper.getLevel().registryAccess();
        helper.assertTrue(ModEnchantments.ALL.size() == 19, "expected exactly 19 declared keys");
        for (var key : ModEnchantments.ALL) {
            helper.assertTrue(EnchantmentAccess.resolve(key, access).isPresent(),
                    "missing runtime registry entry for " + key.identifier());
        }
        helper.succeed();
    }

    /**
     * Runtime evidence for the melee combat hook: directly invokes the real, mixin-transformed
     * {@code EnchantmentHelper.doPostAttackEffectsWithItemSource} — the exact call vanilla makes
     * from {@code Player.attack} — and asserts Poison Blade's deterministic on-hit effect lands.
     */
    @GameTest(maxTicks = 100)
    public void meleeHookAppliesPoisonBladeOnConfirmedHit(GameTestHelper helper) {
        var level = helper.getLevel();
        Holder<Enchantment> poisonBlade = EnchantmentAccess
                .resolve(ModEnchantments.POISON_BLADE, level.registryAccess())
                .orElseThrow();

        Player attacker = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack sword = new ItemStack(Items.IRON_SWORD);
        sword.enchant(poisonBlade, 1);
        attacker.setItemInHand(InteractionHand.MAIN_HAND, sword);

        LivingEntity target = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 1));
        helper.assertFalse(target.hasEffect(MobEffects.POISON), "target must start without Poison");

        EnchantmentHelper.doPostAttackEffectsWithItemSource(
                level, target, level.damageSources().playerAttack(attacker), sword);

        helper.assertTrue(target.hasEffect(MobEffects.POISON),
                "melee hook must apply Poison Blade's effect on a confirmed hit");
        helper.succeed();
    }

    /**
     * Runtime evidence that projectile enchantment context is a shot-time snapshot: the arrow
     * entity must keep the weapon context it was created with even after the shooter's held item
     * changes before impact (no re-reading the current held item on hit).
     */
    @GameTest(maxTicks = 100)
    public void projectileContextSurvivesWeaponSwitchBeforeImpact(GameTestHelper helper) {
        var level = helper.getLevel();
        Holder<Enchantment> vulnerability = EnchantmentAccess
                .resolve(ModEnchantments.VULNERABILITY, level.registryAccess())
                .orElseThrow();

        Player shooter = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack bow = new ItemStack(Items.BOW);
        bow.enchant(vulnerability, 2);

        ArrowItem arrowItem = (ArrowItem) Items.ARROW;
        var arrow = arrowItem.createArrow(level, new ItemStack(Items.ARROW), shooter, bow);
        helper.assertTrue(arrow instanceof ProjectileEnchantmentContextHolder,
                "arrow entity must implement ProjectileEnchantmentContextHolder");
        int shotTimeLevel = ((ProjectileEnchantmentContextHolder) arrow).customEnchants$getProjectileContext().vulnerability();
        helper.assertTrue(shotTimeLevel == 2, "shot-time context must capture the enchanted bow's level, got " + shotTimeLevel);

        // Switch the held item after the shot — the arrow's captured context must not change.
        shooter.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK));
        int afterSwitchLevel = ((ProjectileEnchantmentContextHolder) arrow).customEnchants$getProjectileContext().vulnerability();
        helper.assertTrue(afterSwitchLevel == 2,
                "projectile context must survive a weapon switch before impact, got " + afterSwitchLevel);

        helper.succeed();
    }
}
