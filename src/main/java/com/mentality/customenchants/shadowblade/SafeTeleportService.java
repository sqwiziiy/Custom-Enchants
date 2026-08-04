package com.mentality.customenchants.shadowblade;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

/** Server-only, fail-closed teleportation used by both Shadow Blade hit paths. */
public final class SafeTeleportService {
    private SafeTeleportService() {
    }

    public static boolean tryTeleportBehind(ServerPlayer attacker, LivingEntity target) {
        if (attacker == null || target == null || !(attacker.level() instanceof ServerLevel level)
                || target.level() != level || !validEntity(attacker) || !validEntity(target)
                || attacker.isSleeping() || attacker.isPassenger() || attacker.isVehicle()
                || !finite(attacker.getX(), attacker.getY(), attacker.getZ(), target.getX(), target.getY(), target.getZ())) {
            return false;
        }

        for (ShadowBladeTeleportPlanner.Candidate candidate : ShadowBladeTeleportPlanner.candidates(
                target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getBbWidth(), attacker.getBbWidth())) {
            AABB box = movedBox(attacker, candidate);
            if (!safe(level, attacker, target, candidate, box)) {
                continue;
            }

            double dx = target.getX() - candidate.x();
            double dz = target.getZ() - candidate.z();
            float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
            attacker.teleportTo(level, candidate.x(), candidate.y(), candidate.z(), yaw, attacker.getXRot());
            return true;
        }
        return false;
    }

    private static boolean safe(ServerLevel level, ServerPlayer attacker, LivingEntity target,
                                ShadowBladeTeleportPlanner.Candidate candidate, AABB box) {
        if (!candidate.isFinite() || box.minY < level.getMinBuildHeight() || box.maxY > level.getMaxBuildHeight()
                || !level.getWorldBorder().isWithinBounds(box) || !loaded(level, box)
                || box.intersects(target.getBoundingBox()) || !level.noCollision(attacker, box)) {
            return false;
        }

        double[] xs = {box.minX + 0.05D, candidate.x(), box.maxX - 0.05D};
        double[] zs = {box.minZ + 0.05D, candidate.z(), box.maxZ - 0.05D};
        double[] ys = {box.minY + 0.05D, (box.minY + box.maxY) / 2.0D, box.maxY - 0.05D};
        for (double x : xs) {
            for (double z : zs) {
                for (double y : ys) {
                    if (dangerous(level, BlockPos.containing(x, y, z))) {
                        return false;
                    }
                }
                BlockPos floor = BlockPos.containing(x, box.minY - 0.05D, z);
                if (!loaded(level, floor) || dangerous(level, floor)
                        || level.getBlockState(floor).getCollisionShape(level, floor).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
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
