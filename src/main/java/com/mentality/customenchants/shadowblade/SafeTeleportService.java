package com.mentality.customenchants.shadowblade;

import com.mentality.customenchants.CustomEnchantsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/** Server-only, fail-closed teleportation used by both Shadow Blade hit paths. */
public final class SafeTeleportService {
    private static final boolean DEBUG = Boolean.getBoolean("customenchants.debug.shadow");

    public enum FailureReason {
        SUCCESS,
        INVALID_ENTITY,
        DIFFERENT_LEVEL,
        UNLOADED_CHUNK,
        WORLD_BORDER,
        TARGET_INTERSECTION,
        NO_COLLISION_FAILED,
        DANGEROUS_BLOCK,
        NO_SOLID_FLOOR,
        TELEPORT_API_REJECTED,
        POSITION_NOT_UPDATED,
        NO_CANDIDATE
    }

    public record CandidateDiagnostic(ShadowBladeTeleportPlanner.Candidate candidate, FailureReason reason) {
    }

    public record TeleportResult(FailureReason reason,
                                 ShadowBladeTeleportPlanner.Candidate selected,
                                 List<CandidateDiagnostic> candidates) {
        public boolean success() {
            return reason == FailureReason.SUCCESS;
        }
    }

    private SafeTeleportService() {
    }

    public static boolean tryTeleportBehind(ServerPlayer attacker, LivingEntity target) {
        return diagnoseTeleportBehind(attacker, target).success();
    }

    public static TeleportResult diagnoseTeleportBehind(ServerPlayer attacker, LivingEntity target) {
        if (attacker == null || target == null || !(attacker.level() instanceof ServerLevel level)
                || target.level() != level) {
            return result(FailureReason.DIFFERENT_LEVEL, null, List.of());
        }
        if (!validEntity(attacker) || !validEntity(target)
                || attacker.isSleeping() || attacker.isPassenger() || attacker.isVehicle()
                || !finite(attacker.getX(), attacker.getY(), attacker.getZ(), target.getX(), target.getY(), target.getZ())) {
            return result(FailureReason.INVALID_ENTITY, null, List.of());
        }

        List<CandidateDiagnostic> diagnostics = new ArrayList<>();
        for (ShadowBladeTeleportPlanner.Candidate candidate : ShadowBladeTeleportPlanner.candidates(
                target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getBbWidth(), attacker.getBbWidth())) {
            AABB box = movedBox(attacker, candidate);
            FailureReason rejection = safetyReason(level, attacker, target, candidate, box);
            if (rejection != null) {
                diagnostics.add(new CandidateDiagnostic(candidate, rejection));
                continue;
            }

            double dx = target.getX() - candidate.x();
            double dz = target.getZ() - candidate.z();
            float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
            try {
                attacker.teleportTo(level, candidate.x(), candidate.y(), candidate.z(), Set.of(), yaw,
                        attacker.getXRot());
            } catch (RuntimeException exception) {
                debug("teleport API rejected candidate={} reason={} exception={}", candidate, exception.getClass().getSimpleName());
                diagnostics.add(new CandidateDiagnostic(candidate, FailureReason.TELEPORT_API_REJECTED));
                continue;
            }
            if (attacker.distanceToSqr(candidate.x(), candidate.y(), candidate.z()) > 1.0E-6D) {
                diagnostics.add(new CandidateDiagnostic(candidate, FailureReason.POSITION_NOT_UPDATED));
                continue;
            }
            debug("teleport success candidate={} yaw={} attempts={}", candidate, yaw, diagnostics.size() + 1);
            return result(FailureReason.SUCCESS, candidate, diagnostics);
        }
        FailureReason finalReason = diagnostics.isEmpty() ? FailureReason.NO_CANDIDATE
                : diagnostics.get(diagnostics.size() - 1).reason();
        debug("teleport failed reason={} diagnostics={}", finalReason, diagnostics);
        return result(finalReason, null, diagnostics);
    }

    private static FailureReason safetyReason(ServerLevel level, ServerPlayer attacker, LivingEntity target,
                                              ShadowBladeTeleportPlanner.Candidate candidate, AABB box) {
        if (!candidate.isFinite() || box.minY < level.getMinBuildHeight() || box.maxY > level.getMaxBuildHeight()) {
            return FailureReason.INVALID_ENTITY;
        }
        if (!level.getWorldBorder().isWithinBounds(box)) return FailureReason.WORLD_BORDER;
        if (!loaded(level, box)) return FailureReason.UNLOADED_CHUNK;
        if (box.intersects(target.getBoundingBox())) return FailureReason.TARGET_INTERSECTION;
        // A standing player's feet are exactly on the top face of the floor block. A
        // microscopic inward shrink avoids treating that shared face as an overlap while
        // preserving all meaningful wall/ceiling collision checks.
        boolean blockCollisionFree = level.noCollision(box.deflate(1.0E-4D));
        boolean entityCollisionFree = level.getEntities(attacker, box).stream()
                .noneMatch(entity -> entity != target && entity.isAlive());
        if (!blockCollisionFree || !entityCollisionFree) {
            return FailureReason.NO_COLLISION_FAILED;
        }

        double[] xs = {box.minX + 0.05D, candidate.x(), box.maxX - 0.05D};
        double[] zs = {box.minZ + 0.05D, candidate.z(), box.maxZ - 0.05D};
        double[] ys = {box.minY + 0.05D, (box.minY + box.maxY) / 2.0D, box.maxY - 0.05D};
        for (double x : xs) {
            for (double z : zs) {
                for (double y : ys) {
                    if (dangerous(level, BlockPos.containing(x, y, z))) return FailureReason.DANGEROUS_BLOCK;
                }
                BlockPos floor = BlockPos.containing(x, box.minY - 0.05D, z);
                if (!loaded(level, floor)) return FailureReason.UNLOADED_CHUNK;
                if (dangerousFloor(level, floor)) return FailureReason.DANGEROUS_BLOCK;
                if (level.getBlockState(floor).getCollisionShape(level, floor).isEmpty()) {
                    return FailureReason.NO_SOLID_FLOOR;
                }
            }
        }
        return null;
    }

    private static TeleportResult result(FailureReason reason,
                                         ShadowBladeTeleportPlanner.Candidate selected,
                                         List<CandidateDiagnostic> diagnostics) {
        return new TeleportResult(reason, selected, List.copyOf(diagnostics));
    }

    private static void debug(String message, Object... args) {
        if (DEBUG || Boolean.getBoolean("customenchants.debug.shadow")) {
            CustomEnchantsMod.LOGGER.info("[Shadow debug] " + message, args);
        }
    }

    private static AABB movedBox(ServerPlayer attacker, ShadowBladeTeleportPlanner.Candidate candidate) {
        AABB current = attacker.getBoundingBox();
        return current.move(candidate.x() - attacker.getX(), candidate.y() - attacker.getY(), candidate.z() - attacker.getZ());
    }

    private static boolean loaded(ServerLevel level, AABB box) {
        return loaded(level, BlockPos.containing(box.minX, box.minY, box.minZ))
                && loaded(level, BlockPos.containing(box.maxX, box.maxY, box.maxZ));
    }

    private static boolean loaded(ServerLevel level, BlockPos pos) {
        return level.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
    }

    private static boolean dangerous(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        FluidState fluid = level.getFluidState(pos);
        return !fluid.isEmpty() || state.isSuffocating(level, pos)
                || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)
                || state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE)
                || state.is(Blocks.MAGMA_BLOCK) || state.is(Blocks.CACTUS)
                || state.is(Blocks.SWEET_BERRY_BUSH) || state.is(Blocks.POWDER_SNOW);
    }

    private static boolean dangerousFloor(ServerLevel level, BlockPos pos) {
        FluidState fluid = level.getFluidState(pos);
        BlockState state = level.getBlockState(pos);
        return !fluid.isEmpty() || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)
                || state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE)
                || state.is(Blocks.MAGMA_BLOCK) || state.is(Blocks.CACTUS)
                || state.is(Blocks.SWEET_BERRY_BUSH) || state.is(Blocks.POWDER_SNOW);
    }

    private static boolean validEntity(LivingEntity entity) {
        return entity.isAlive() && !entity.isRemoved() && !entity.isSpectator()
                && !entity.isSleeping() && !entity.isPassenger() && !entity.isVehicle()
                && finite(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
    }

    private static boolean finite(double... values) {
        for (double value : values) {
            if (!Double.isFinite(value)) return false;
        }
        return true;
    }
}
