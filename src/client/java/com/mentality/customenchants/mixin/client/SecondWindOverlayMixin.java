package com.mentality.customenchants.mixin.client;

import com.mentality.customenchants.enchantment.SecondWindClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class SecondWindOverlayMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void renderSecondWindOverlay(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        int ticks = SecondWindClientHandler.getEffectTicksRemaining();
        if (ticks <= 0) return;

        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        // Pulsing alpha based on remaining ticks
        float alpha = Math.min(0.3f, ticks / 40.0f * 0.3f);
        float pulse = (float) (Math.sin(ticks * 0.3) * 0.5 + 0.5);
        alpha *= (0.5f + pulse * 0.5f);

        int a = (int) (alpha * 255) << 24;
        int red = a | 0x00FF0000;

        // Draw semi-transparent red edges
        int edgeSize = 30;
        // Top edge
        guiGraphics.fill(0, 0, width, edgeSize, red);
        // Bottom edge
        guiGraphics.fill(0, height - edgeSize, width, height, red);
        // Left edge
        guiGraphics.fill(0, edgeSize, edgeSize, height - edgeSize, red);
        // Right edge
        guiGraphics.fill(width - edgeSize, edgeSize, width, height - edgeSize, red);
    }
}
