package com.mentality.customenchants.enchantment;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.util.AutoSmeltBreakContext;
import com.mentality.customenchants.util.AutoSmeltDropTransformer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public final class AutoSmeltHandler {

    private AutoSmeltHandler() {
    }

    /**
     * Transforms the already computed vanilla drops. The block, block entity and tool are
     * the exact context supplied by Block.getDrops during the standard playerDestroy path.
     * No block, item entity, XP or durability side effect is performed here.
     */
    public static List<ItemStack> transformDrops(
            ServerLevel level,
            BlockState state,
            BlockPos pos,
            BlockEntity blockEntity,
            Entity breaker,
            ItemStack tool,
            List<ItemStack> drops
    ) {
        if (!(breaker instanceof ServerPlayer)) return drops;
        if (!isEligible(tool)) return drops;

        return AutoSmeltDropTransformer.transform(drops, input -> resolveSmelting(level, input));
    }

    public static boolean isEligible(ItemStack tool) {
        if (!ModConfig.get().autoSmeltEnabled || tool.isEmpty()) return false;
        if (EnchantmentAccess.getLevel(tool, ModEnchantments.AUTO_SMELT) <= 0) return false;

        // Commands can create this combination even though the enchantment API rejects it.
        // Silk Touch remains authoritative over the already generated block-item drop.
        return !hasSilkTouch(tool);
    }

    private static ItemStack resolveSmelting(ServerLevel level, ItemStack input) {
        SingleRecipeInput recipeInput = new SingleRecipeInput(input.copy());
        Optional<RecipeHolder<SmeltingRecipe>> recipe = level.recipeAccess()
                .getRecipeFor(RecipeType.SMELTING, recipeInput, level);
        return recipe.map(holder -> holder.value().assemble(recipeInput, level.registryAccess()))
                .orElse(ItemStack.EMPTY);
    }

    static boolean hasSilkTouch(ItemStack tool) {
        return EnchantmentAccess.getLevel(tool, Enchantments.SILK_TOUCH) > 0;
    }
}
