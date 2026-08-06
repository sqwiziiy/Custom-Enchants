# Phase 40: Magnet + Lumberjack and Auto Smelt (Minecraft 1.20.1)

## Status

`MC_1_20_1_DROPS_FIXED_READY_FOR_RETEST`

Baseline SHA: `797752ea6da13728c1841771e03ca1d2c0eee731` on `3.1/1.20.1`; final SHA is pending the requested local commits. No remote operation was performed.

Carried-forward manual results: Double Jump PASS; Sky Rage rejects tridents PASS; Magnet + Lumberjack FAIL; Auto Smelt FAIL. The two failed drop paths have local automated coverage and require the requested manual retest before push.

## Magnet nested trace and accounting

`LumberjackHandler` plans secondary logs and `AdditionalBlockBreakService` calls `ServerPlayerGameMode.destroyBlock` once per position. Each real `Block.playerDestroy` opens a stack-scoped `MagnetBreakDropContext`; `Block.popResource` synchronously reaches `ServerLevel.addFreshEntity`; the RETURN hook identifies the spawned `ItemEntity`, clears its pickup delay, calls vanilla `playerTouch`, and leaves any inventory remainder in the world. Stack scopes restore an outer context after nested calls.

The new survival GameTest constructs exactly five oak logs, breaks the initial log through `ServerPlayerGameMode.destroyBlock`, and asserts `inventory oak logs + linked world oak-log counts == 5`. With a free inventory it asserts `5 + 0`. It passed locally.

## Auto Smelt trace and root cause

The exact 1.20.1 path is `Block.playerDestroy -> Block.getDrops(state, ServerLevel, BlockPos, BlockEntity, Entity, ItemStack) -> Block.popResource -> ServerLevel.addFreshEntity`.

Root cause: the `getDrops` RETURN injector called `CallbackInfoReturnable.setReturnValue` without `cancellable = true`; it could not replace vanilla drops at runtime. The obsolete context gate also relied on a tool-stack equality comparison vulnerable to a mutable break stack. The final hook transforms the actual 1.20.1 `getDrops` result directly, preserving the original stack when no recipe/output exists. Silk Touch remains excluded by `AutoSmeltHandler` and no furnace XP is generated.

The survival GameTest breaks iron ore through `ServerPlayerGameMode.destroyBlock`, then verifies that the actual `ItemEntity` is an iron ingot and that raw iron is absent. It passed locally.

## Production fixes and coverage

- Made the `Block.getDrops` RETURN injector cancellable and applied its transformed return value to the actual final drop list.
- Kept Magnet’s per-break deque context; added a nested-scope restoration unit test.
- Added real server GameTests for Auto Smelt output and the five-log Magnet + Lumberjack accounting invariant.
- Added a source contract test preventing the non-cancellable Auto Smelt return-hook regression.

## Verification and manual retest

Java 17 was used. Targeted unit tests and `./gradlew --no-daemon --console=plain gameTest` passed; the latter reported all four GameTests green. The full local pipeline also passed: `clean test`, `check`, `build`, `verifyReleaseJar`, and `dedicatedServerSmoke`; the release artifact was `custom-enchants-3.1.1.jar`.

Manual retest required: five-log/ordinary/large trees (three repetitions and full inventory); iron/gold/copper with Fortune and Silk Touch; Auto Smelt + Magnet confirming only the ingot enters inventory; confirm Double Jump and Sky Rage trident rejection remain green.

## Commits and remote state

The requested four local commits have not yet been created. No push, force-push, tag, release, Modrinth action, or other remote change occurred.
