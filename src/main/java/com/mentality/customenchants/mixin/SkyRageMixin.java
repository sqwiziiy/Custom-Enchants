package com.mentality.customenchants.mixin;

import com.mentality.customenchants.config.ModConfig;
import com.mentality.customenchants.enchantment.ModEnchantments;
import com.mentality.customenchants.enchantment.SkyRageEnchantment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(AbstractArrow.class)
public abstract class SkyRageMixin {

    @Inject(method = "onHitEntity", at = @At("RETURN"))
    private void onSkyRageHitEntity(EntityHitResult result, CallbackInfo ci) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        trySpawnLightning(self, result.getLocation());
    }

    @Inject(method = "onHitBlock", at = @At("RETURN"))
    private void onSkyRageHitBlock(BlockHitResult result, CallbackInfo ci) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        // Use exact hit location on the block face — ensures isRainingAt check
        // works correctly (block bottom-center would be inside solid block and fail).
        trySpawnLightning(self, result.getLocation());
    }

    private static void trySpawnLightning(AbstractArrow arrow, Vec3 pos) {
        if (!ModConfig.get().skyRageEnabled) return;
        if (!(arrow.level() instanceof ServerLevel serverLevel)) return;

        // Requires a thunderstorm, not just rain
        if (!serverLevel.isThundering()) return;

        // For vertical block faces strikePos can end up inside/beside the block,
        // causing isRainingAt to return false even in the open.  Use the top of
        // the block column at this X,Z for the sky-exposure check instead.
        BlockPos strikePos  = BlockPos.containing(pos);
        BlockPos columnTop  = serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, strikePos);
        if (!serverLevel.isRainingAt(columnTop)) return;

        Entity owner = arrow.getOwner();
        if (!(owner instanceof Player player)) return;

        // Find the enchantment level on bow/crossbow in either hand
        int level = 0;
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof BowItem || mainHand.getItem() instanceof CrossbowItem) {
            level = Math.max(level, EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SKY_RAGE, mainHand));
        }
        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof BowItem || offHand.getItem() instanceof CrossbowItem) {
            level = Math.max(level, EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SKY_RAGE, offHand));
        }
        if (level <= 0) return;

        // Check per-player cooldown
        UUID playerUUID = player.getUUID();
        long currentTime = serverLevel.getGameTime();
        Long lastTime = SkyRageEnchantment.lastLightningTime.get(playerUUID);
        if (lastTime != null && (currentTime - lastTime) < ModConfig.get().skyRageCooldownTicks) return;

        // Roll the chance
        if (serverLevel.getRandom().nextFloat() >= SkyRageEnchantment.getChance(level)) return;

        // Update cooldown and spawn lightning
        SkyRageEnchantment.lastLightningTime.put(playerUUID, currentTime);

        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
        if (bolt == null) return;
        bolt.moveTo(pos.x, pos.y, pos.z);
        serverLevel.addFreshEntity(bolt);
    }
}
