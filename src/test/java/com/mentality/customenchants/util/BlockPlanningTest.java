package com.mentality.customenchants.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockPlanningTest {

    @Test
    void drillPlansEightUniqueNeighborsForEveryFace() {
        BlockPos origin = new BlockPos(10, 64, -3);
        for (Direction face : Direction.values()) {
            List<BlockPos> plan = DrillBlockPlanner.plan(origin, face);
            assertEquals(8, plan.size());
            assertEquals(8, new HashSet<>(plan).size());
            assertFalse(plan.contains(origin));
        }
    }

    @Test
    void drillOrderIsDeterministicAndExtremeCoordinatesDoNotWrap() {
        BlockPos origin = new BlockPos(Integer.MAX_VALUE - 1, 100, Integer.MIN_VALUE + 1);
        List<BlockPos> first = DrillBlockPlanner.plan(origin, Direction.UP);
        assertEquals(first, DrillBlockPlanner.plan(origin, Direction.UP));
        assertNotEquals(origin.getX(), first.get(0).getX());
        assertFalse(DrillBlockPlanner.plan(null, Direction.UP).iterator().hasNext());
        assertFalse(DrillBlockPlanner.plan(origin, null).iterator().hasNext());
    }

    @Test
    void drillFaceResolverUsesActualViewRayForAxisAlignedFaces() {
        BlockPos block = new BlockPos(0, 64, 0);
        assertEquals(Direction.NORTH, DrillFaceResolver.resolve(
                new Vec3(0.5, 64.5, -3.0), new Vec3(0.0, 0.0, 1.0), block));
        assertEquals(Direction.SOUTH, DrillFaceResolver.resolve(
                new Vec3(0.5, 64.5, 4.0), new Vec3(0.0, 0.0, -1.0), block));
        assertEquals(Direction.UP, DrillFaceResolver.resolve(
                new Vec3(0.5, 68.0, 0.5), new Vec3(0.0, -1.0, 0.0), block));
        assertEquals(Direction.DOWN, DrillFaceResolver.resolve(
                new Vec3(0.5, 61.0, 0.5), new Vec3(0.0, 1.0, 0.0), block));
    }

    @Test
    void drillFaceResolverFollowsCameraAtDiagonalBoundaryInsteadOfEyeToCenterTie() {
        BlockPos block = new BlockPos(0, 64, 0);
        Vec3 eye = new Vec3(4.0, 64.5, -3.0);

        Direction resolved = DrillFaceResolver.resolve(eye, new Vec3(-3.1, 0.0, 3.0), block);
        assertEquals(Direction.NORTH, resolved);

        List<BlockPos> plan = DrillBlockPlanner.plan(block, resolved);
        assertTrue(plan.stream().allMatch(pos -> pos.getZ() == block.getZ()));
    }

    @Test
    void drillFaceResolverFallsBackToDominantCameraAxisIfRayNoLongerHitsBrokenBlock() {
        BlockPos block = new BlockPos(0, 64, 0);
        Direction resolved = DrillFaceResolver.resolve(
                new Vec3(0.5, 65.5, -3.0), new Vec3(0.8, -0.1, 0.2), block);
        assertEquals(Direction.WEST, resolved);
    }

    @Test
    void drillFaceResolverRejectsInvalidInput() {
        BlockPos block = new BlockPos(0, 64, 0);
        assertNull(DrillFaceResolver.resolve(null, new Vec3(0.0, 0.0, 1.0), block));
        assertNull(DrillFaceResolver.resolve(new Vec3(0.5, 64.5, -3.0), Vec3.ZERO, block));
        assertNull(DrillFaceResolver.resolve(new Vec3(Double.NaN, 0.0, 0.0), new Vec3(0.0, 0.0, 1.0), block));
    }

    @Test
    void lumberjackPlansSimpleVerticalTreeWithinLimit() {
        BlockPos origin = new BlockPos(0, 64, 0);
        Set<BlockPos> logs = Set.of(origin.above(), origin.above(2), origin.above(3));
        List<BlockPos> plan = LumberjackBlockPlanner.plan(origin, 10, logs::contains, ignored -> true);
        assertEquals(List.of(origin.above(), origin.above(2), origin.above(3)), plan);
    }

    @Test
    void lumberjackPlansBranchDeterministicallyAndExcludesDisconnectedLogs() {
        BlockPos origin = new BlockPos(0, 64, 0);
        Set<BlockPos> logs = new HashSet<>(Set.of(
                origin.above(), origin.above().east(), origin.above().west(),
                new BlockPos(10, 64, 10)));
        List<BlockPos> first = LumberjackBlockPlanner.plan(origin, 10, logs::contains, ignored -> true);
        assertEquals(first, LumberjackBlockPlanner.plan(origin, 10, logs::contains, ignored -> true));
        assertEquals(3, first.size());
        assertFalse(first.contains(new BlockPos(10, 64, 10)));
    }

    @Test
    void lumberjackHonorsLimitAndDoesNotTraverseBelowOrigin() {
        BlockPos origin = new BlockPos(0, 64, 0);
        Set<BlockPos> logs = Set.of(origin.below(), origin.above(), origin.above(2), origin.above(3));
        List<BlockPos> plan = LumberjackBlockPlanner.plan(origin, 2, logs::contains, ignored -> true);
        assertEquals(2, plan.size());
        assertTrue(plan.stream().allMatch(pos -> pos.getY() >= origin.getY()));
    }

    @Test
    void lumberjackStopsAtUnloadedNeighborAndInvalidInputFailsClosed() {
        BlockPos origin = new BlockPos(0, 64, 0);
        Set<BlockPos> logs = Set.of(origin.above(), origin.above(2));
        List<BlockPos> plan = LumberjackBlockPlanner.plan(origin, 10, logs::contains,
                pos -> !pos.equals(origin.above(2)));
        assertEquals(1, plan.size());
        assertTrue(LumberjackBlockPlanner.plan(origin, 0, logs::contains, ignored -> true).isEmpty());
        assertTrue(LumberjackBlockPlanner.plan(origin, 10, null, ignored -> true).isEmpty());
        assertTrue(LumberjackBlockPlanner.plan(origin, 10, logs::contains, null).isEmpty());
    }

    @Test
    void lumberjackSkipsOnlyTheUnloadedBranchOfABranchedTree() {
        BlockPos origin = new BlockPos(0, 64, 0);
        BlockPos unloadedBranch = origin.above().west();
        BlockPos loadedBranch = origin.above().east();
        BlockPos loadedTip = loadedBranch.east();
        Set<BlockPos> logs = Set.of(unloadedBranch, loadedBranch, loadedTip);

        List<BlockPos> plan = LumberjackBlockPlanner.plan(origin, 10, logs::contains,
                pos -> !pos.equals(unloadedBranch));

        assertEquals(List.of(loadedBranch, loadedTip), plan);
        assertFalse(plan.contains(unloadedBranch));
    }

    @Test
    void lumberjackCycleIsBoundedByVisitedSet() {
        BlockPos origin = new BlockPos(0, 64, 0);
        Set<BlockPos> cycle = Set.of(origin.above(), origin.above().east(), origin.above().east().below());
        List<BlockPos> plan = LumberjackBlockPlanner.plan(origin, 100, cycle::contains, ignored -> true);
        assertEquals(3, plan.size());
    }
}
