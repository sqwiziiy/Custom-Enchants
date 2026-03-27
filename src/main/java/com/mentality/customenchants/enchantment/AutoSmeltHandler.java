package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.Set;

public class AutoSmeltHandler {

    private static final Set<Block> SMELTABLE_ORES = Set.of(
            Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
            Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE
    );

    /**
     * Tries to smelt a block drop. Does NOT remove the block or damage the tool — caller must do that.
     * Returns true if the smelted item was dropped (block should be removed without normal drops).
     */
    public static boolean trySmeltBlock(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state, ItemStack tool) {
        if (!ModConfig.get().autoSmeltEnabled) return false;
        if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.AUTO_SMELT, tool) <= 0) return false;
        if (!SMELTABLE_ORES.contains(state.getBlock())) return false;

        ItemStack blockDrop = new ItemStack(state.getBlock().asItem());
        Optional<SmeltingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(blockDrop), level);

        if (recipe.isPresent()) {
            ItemStack smeltedResult = recipe.get().getResultItem(level.registryAccess()).copy();
            Block.popResource(level, pos, smeltedResult);
            float xp = recipe.get().getExperience();
            if (xp > 0) {
                int xpAmount = (int) xp;
                if (xpAmount < 1 && player.getRandom().nextFloat() < xp) {
                    xpAmount = 1;
                }
                if (xpAmount > 0) {
                    player.giveExperiencePoints(xpAmount);
                }
            }
            return true;
        }
        return false;
    }

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return true;
            if (!(world instanceof ServerLevel serverLevel)) return true;

            ItemStack tool = player.getMainHandItem();
            if (tool.isEmpty()) return true;

            boolean smelted = trySmeltBlock(serverLevel, serverPlayer, pos, state, tool);
            if (!smelted) return true;

            // Remove the block without vanilla drops
            serverLevel.removeBlock(pos, false);
            tool.hurtAndBreak(1, serverPlayer, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));

            // Trigger Drill for surrounding blocks (since AFTER won't fire)
            if (!tool.isEmpty() && !player.isShiftKeyDown() && ModConfig.get().drillEnabled
                    && EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DRILL, tool) > 0) {
                DrillHandler.drillAround(serverPlayer, pos);
            }

            // Trigger Magnet pickup (since AFTER won't fire)
            if (ModConfig.get().magnetEnabled
                    && EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MAGNET, tool) > 0) {
                MagnetHandler.collectNearby(serverLevel, serverPlayer, pos);
            }

            return false;
        });
    }
}
