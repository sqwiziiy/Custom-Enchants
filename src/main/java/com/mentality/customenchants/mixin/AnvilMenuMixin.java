package com.mentality.customenchants.mixin;

import com.mentality.customenchants.anvil.AnvilResultPolicy;
import com.mentality.customenchants.enchantment.EnchantmentAccess;
import com.mentality.customenchants.enchantment.ModEnchantments;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @org.spongepowered.asm.mixin.Shadow @org.spongepowered.asm.mixin.Final
    private DataSlot cost;
    @org.spongepowered.asm.mixin.Shadow
    private int repairItemCountCost;

    @Inject(method = "createResult", at = @At("RETURN"))
    private void blockShadowBladeOnNonTrident(CallbackInfo ci) {
        AnvilMenu self = (AnvilMenu) (Object) this;
        ItemStack result = self.getSlot(2).getItem();
        if (!result.isEmpty() && AnvilResultPolicy.rejectShadowBladeResult(
                EnchantmentAccess.getLevel(result, ModEnchantments.SHADOW_BLADE) > 0,
                result.getItem() instanceof TridentItem)) {
            self.getSlot(2).set(ItemStack.EMPTY);
            cost.set(0);
            repairItemCountCost = 0;
            return;
        }
        if (!result.isEmpty() && !(result.getItem() instanceof TridentItem)) {
            Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(result);
            if (enchants.remove(ModEnchantments.SHADOW_BLADE) != null) {
                EnchantmentHelper.setEnchantments(enchants, result);
            }
        }
    }
}
