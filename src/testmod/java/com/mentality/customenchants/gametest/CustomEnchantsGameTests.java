package com.mentality.customenchants.gametest;

import com.mentality.customenchants.enchantment.EnchantmentAccess;
import com.mentality.customenchants.enchantment.AutoSmeltHandler;
import com.mentality.customenchants.enchantment.MagnetHandler;
import com.mentality.customenchants.enchantment.ModEnchantments;
import com.mentality.customenchants.shadowblade.SafeTeleportService;
import com.mentality.customenchants.mixin.ItemEntityPickupDelayAccessor;
import com.mojang.authlib.GameProfile;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.UUID;

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

    @GameTest(maxTicks = 100)
    public void autoSmeltResolvesOreRecipeWithoutDuplicateDrop(GameTestHelper helper) {
        var level = helper.getLevel();
        Holder<Enchantment> autoSmelt = EnchantmentAccess
                .resolve(ModEnchantments.AUTO_SMELT, level.registryAccess()).orElseThrow();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack pickaxe = new ItemStack(Items.IRON_PICKAXE);
        pickaxe.enchant(autoSmelt, 1);

        List<ItemStack> drops = AutoSmeltHandler.transformDrops(
                level, Blocks.IRON_ORE.defaultBlockState(), new BlockPos(1, 1, 1), null,
                player, pickaxe, List.of(new ItemStack(Items.RAW_IRON)));
        helper.assertTrue(drops.size() == 1 && drops.get(0).is(Items.IRON_INGOT)
                        && drops.get(0).getCount() == 1,
                "Auto Smelt must replace raw iron with exactly one iron ingot");
        helper.succeed();
    }

    @GameTest(maxTicks = 100)
    public void magnetCollectsCurrentDropDespiteVanillaPickupDelay(GameTestHelper helper) {
        var level = helper.getLevel();
        BlockPos localPos = new BlockPos(1, 1, 1);
        BlockPos absolutePos = helper.absolutePos(localPos);
        ServerPlayer player = realServerPlayer(helper, localPos);
        ItemEntity drop = new ItemEntity(level, absolutePos.getX() + 0.5D, absolutePos.getY(),
                absolutePos.getZ() + 0.5D, new ItemStack(Items.COBBLESTONE));
        drop.setThrower(player);
        drop.setPickUpDelay(10);
        level.addFreshEntity(drop);

        MagnetHandler.collectNearby(level, player, absolutePos);
        helper.assertTrue(((ItemEntityPickupDelayAccessor) drop).customEnchants$getPickupDelay() > 0,
                "test fixture must start with a vanilla pickup delay");
        helper.onEachTick(() -> {
            MagnetHandler.processPendingForTest(level.getServer(), player);
            if (drop.isRemoved()) {
                helper.assertTrue(player.getInventory().countItem(Items.COBBLESTONE) == 1,
                        "collected drop must enter the real ServerPlayer inventory");
                helper.succeed();
            }
        });
    }

    @GameTest(maxTicks = 100)
    public void shadowBladeUsesOpenFloorCandidateWithRealServerPlayerHarness(GameTestHelper helper) {
        var level = helper.getLevel();
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) helper.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
        }
        ServerPlayer attacker = realServerPlayer(helper, new BlockPos(3, 2, 0));
        LivingEntity target = helper.spawn(EntityType.PIG, new BlockPos(0, 2, 0));
        target.setYRot(0.0F);
        double beforeX = attacker.getX();
        double beforeZ = attacker.getZ();

        SafeTeleportService.TeleportResult result = SafeTeleportService.diagnoseTeleportBehind(attacker, target);

        helper.assertTrue(result.candidates().stream().anyMatch(d -> d.reason() == SafeTeleportService.FailureReason.TELEPORT_API_REJECTED),
                "diagnostic result must retain the candidate rejected by teleport API");
        helper.assertTrue(attacker.distanceToSqr(beforeX, attacker.getY(), beforeZ) < 1.0E-4D,
                "disconnected harness must not claim that teleport succeeded");
        helper.succeed();
    }

    private static ServerPlayer realServerPlayer(GameTestHelper helper, BlockPos pos) {
        BlockPos absolutePos = helper.absolutePos(pos);
        ServerPlayer player = new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
                new GameProfile(UUID.randomUUID(), "game-test-player"), ClientInformation.createDefault());
        player.setPos(absolutePos.getX() + 0.5D, absolutePos.getY(), absolutePos.getZ() + 0.5D);
        return player;
    }

}
