package com.mentality.customenchants.mixin;

import com.mentality.customenchants.anvil.AnvilResultPolicy;
import com.mentality.customenchants.enchantment.EnchantmentAccess;
import com.mentality.customenchants.enchantment.ModEnchantments;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
            // Shadow Blade must never persist on a non-trident anvil result.
            EnchantmentHelper.updateEnchantments(result,
                    mutable -> mutable.removeIf(holder -> holder.is(ModEnchantments.SHADOW_BLADE)));
        }
        if (!result.isEmpty() && AnvilResultPolicy.rejectSkyRageResult(
                EnchantmentAccess.getLevel(result, ModEnchantments.SKY_RAGE) > 0, result)) {
            // Do not silently turn an invalid combine into a paid result. Standard anvil
            // paths must reject Sky Rage on non-bow items altogether.
            self.getSlot(2).set(ItemStack.EMPTY);
            cost.set(0);
            repairItemCountCost = 0;
        }
    }
}
