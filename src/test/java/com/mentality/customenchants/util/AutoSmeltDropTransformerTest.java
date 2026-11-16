package com.mentality.customenchants.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.SharedConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutoSmeltDropTransformerTest {

    @BeforeAll
    static void bootstrapMinecraftRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void emptyInputProducesEmptyOutput() {
        assertTrue(AutoSmeltDropTransformer.transform(List.of(), ignored -> new ItemStack(Items.IRON_INGOT)).isEmpty());
    }

    @Test
    void unsmeltableStackIsCopiedAndPreserved() {
        ItemStack input = new ItemStack(Items.COBBLESTONE, 3);
        List<ItemStack> result = transform(List.of(input), ignored -> ItemStack.EMPTY);

        assertEquals(List.of(3), counts(result));
        assertEquals(Items.COBBLESTONE, result.get(0).getItem());
        assertNotSame(input, result.get(0));
    }

    @Test
    void smeltableStackScalesInputCountByRecipeOutputCount() {
        List<ItemStack> result = transform(List.of(new ItemStack(Items.RAW_IRON, 3)),
                ignored -> new ItemStack(Items.IRON_INGOT, 2));

        assertEquals(List.of(6), counts(result));
        assertEquals(Items.IRON_INGOT, result.get(0).getItem());
    }

    @Test
    void mixedStacksKeepDeterministicOrder() {
        List<ItemStack> input = List.of(
                new ItemStack(Items.RAW_IRON, 2),
                new ItemStack(Items.COBBLESTONE, 4),
                new ItemStack(Items.RAW_GOLD, 1));
        List<ItemStack> result = transform(input, stack -> {
            if (stack.is(Items.RAW_IRON)) return new ItemStack(Items.IRON_INGOT);
            if (stack.is(Items.RAW_GOLD)) return new ItemStack(Items.GOLD_INGOT);
            return ItemStack.EMPTY;
        });

        assertEquals(List.of(Items.IRON_INGOT, Items.COBBLESTONE, Items.GOLD_INGOT),
                result.stream().map(ItemStack::getItem).toList());
        assertEquals(List.of(2, 4, 1), counts(result));
    }

    @Test
    void outputSplitsAtOutputMaxStackSize() {
        List<ItemStack> result = transform(List.of(new ItemStack(Items.RAW_IRON, 65)),
                ignored -> new ItemStack(Items.IRON_INGOT, 2));

        assertEquals(List.of(64, 64, 2), counts(result));
        assertTrue(result.stream().allMatch(stack -> stack.getCount() <= stack.getMaxStackSize()));
    }

    @Test
    void emptyResolverOutputDoesNotDeleteInput() {
        List<ItemStack> result = transform(List.of(new ItemStack(Items.RAW_IRON, 2)), ignored -> ItemStack.EMPTY);

        assertEquals(Items.RAW_IRON, result.get(0).getItem());
        assertEquals(2, result.get(0).getCount());
    }

    @Test
    void missingResolverOutputDoesNotDeleteInput() {
        List<ItemStack> result = transform(List.of(new ItemStack(Items.RAW_IRON, 2)), ignored -> null);

        assertEquals(Items.RAW_IRON, result.get(0).getItem());
        assertEquals(2, result.get(0).getCount());
    }

    @Test
    void inputListAndStacksAreNotMutated() {
        ItemStack input = new ItemStack(Items.RAW_IRON, 3);
        List<ItemStack> original = new ArrayList<>(List.of(input));
        transform(original, ignored -> new ItemStack(Items.IRON_INGOT, 2));

        assertSame(input, original.get(0));
        assertEquals(3, input.getCount());
    }

    @Test
    void recipeOutputIsCopiedForEverySplit() {
        ItemStack output = new ItemStack(Items.IRON_INGOT, 2);
        List<ItemStack> result = transform(List.of(new ItemStack(Items.RAW_IRON, 65)), ignored -> output);

        assertEquals(3, result.size());
        assertTrue(result.stream().noneMatch(stack -> stack == output));
    }

    @Test
    void inputNbtIsNotCopiedToRecipeOutput() {
        ItemStack input = new ItemStack(Items.RAW_IRON);
        input.set(DataComponents.CUSTOM_NAME, Component.literal("raw input"));
        List<ItemStack> result = transform(List.of(input), ignored -> new ItemStack(Items.IRON_INGOT));

        assertFalse(result.get(0).has(DataComponents.CUSTOM_NAME));
    }

    @Test
    void overflowFailsClosedAndPreservesInput() {
        ItemStack input = new ItemStack(Items.RAW_IRON);
        input.setCount(Integer.MAX_VALUE);
        List<ItemStack> result = transform(List.of(input), ignored -> new ItemStack(Items.IRON_INGOT, 2));

        assertEquals(1, result.size());
        assertEquals(Items.RAW_IRON, result.get(0).getItem());
        assertEquals(Integer.MAX_VALUE, result.get(0).getCount());
    }

    @Test
    void emptyStacksAreRemovedWithoutAResolverCall() {
        int[] calls = {0};
        List<ItemStack> result = transform(List.of(ItemStack.EMPTY), ignored -> {
            calls[0]++;
            return new ItemStack(Items.IRON_INGOT);
        });

        assertTrue(result.isEmpty());
        assertEquals(0, calls[0]);
    }

    private List<ItemStack> transform(List<ItemStack> input, java.util.function.Function<ItemStack, ItemStack> resolver) {
        return AutoSmeltDropTransformer.transform(input, resolver);
    }

    private List<Integer> counts(List<ItemStack> stacks) {
        return stacks.stream().map(ItemStack::getCount).toList();
    }
}
