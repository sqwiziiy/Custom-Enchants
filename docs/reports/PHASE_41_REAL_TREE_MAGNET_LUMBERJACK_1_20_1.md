# Phase 41 — Real-tree Magnet + Lumberjack (Minecraft 1.20.1)

## Status

`MC_1_20_1_LUMBERJACK_MAGNET_READY_FOR_RETEST`

Baseline SHA: `6131dedb4a5c271d41f547a4778f1b74f49deddd` on `3.1/1.20.1`. No remote changes were made.

## Root cause

The Phase 40 vertical five-log structure had only one traversal path, so it could not expose the two early-termination paths used with real branches:

- **A — planner traversal:** encountering one unloaded queued branch used `break`, abandoning other already-discovered branches.
- **B — secondary destruction:** a missing/invalid planned position or a `destroyBlock == false` result used `break`, abandoning all later positions in the batch.

Neither condition represents a reason to abandon independent planned logs. Entity merging was not used for accounting; all checks sum `ItemStack#getCount`.

## Production fix

`LumberjackBlockPlanner` now skips only an unloaded branch and continues its queue. `AdditionalBlockBreakService` now skips only a failed snapshot target and continues the batch; a genuinely unusable tool still stops the batch. Each successful secondary block remains on the vanilla `ServerPlayerGameMode.destroyBlock -> Block.playerDestroy -> Block.popResource -> ServerLevel.addFreshEntity` path, with its own existing Magnet deque context.

## Runtime evidence

The GameTest now creates a deterministic 10-log branched oak (trunk plus horizontal/diagonal branches), breaks its initial log via `ServerPlayerGameMode.destroyBlock`, and asserts:

```text
final drops = successful log breaks = 10
picked + world remainder = 10
free inventory: picked = 10, remainder = 0
```

The regression unit test also establishes that an unloaded sibling branch does not prevent traversal of a loaded branch and its tip. Java 17 targeted planner tests and GameTest passed locally.

## Regressions and manual retest

Auto Smelt, Auto Smelt + Magnet, ordinary Magnet, Double Jump, and Sky Rage code were not changed. Manual retest remains required for small/large oak, 2x2 spruce, dark oak, jungle, and full inventory; confirm `logs before = inventory delta + world remainder + cap-excluded logs`. Reconfirm Auto Smelt + Magnet, Double Jump, and Sky Rage trident rejection.

## Pipeline and remote state

Phase 40's complete Java 17 pipeline was green. Phase 41 targeted Java 17 planner tests and GameTest are green; the full pipeline must be rerun after final Phase 41 commit(s). No push, force-push, tag, release, or other remote action occurred.
