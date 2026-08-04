# Phase 24B — remaining 1.21.11 gameplay runtime fixes

Date: 2026-08-04  
Branch: `3.1/1.21.11`  
Baseline: `7c7bf9635227b1559e02a5d9f64105c9ccf5df2e`  
Version: `3.1.1`, Minecraft `1.21.11`

## Scope

Local work only. No remote, release, tag, Modrinth, Wiki, main/default branch, other
Minecraft version, version bump, or new enchantment work was performed.

## Implemented

- Magnet now opens a batch capture around Lumberjack, records only successfully destroyed
  positions returned by `AdditionalBlockBreakService`, carries the pre-break item UUID set,
  and keeps linked pickup requests isolated and idempotent.
- Sky Rage anvil results on non-bow/crossbow items are now rejected with an empty result and
  zero cost. The data-driven standard path rejects tridents and swords; forced component
  injection remains an explicit admin bypass.
- Auto Smelt's `Block.getDrops` return injection is explicitly cancellable, and an actual
  `Block.playerDestroy` GameTest verifies smelted ore entity output without raw-ore output.
- Feedback audit retained the existing source-of-truth semantics: front-facing active
  Feedback shields independently block allowlisted direct/indirect magic and Shulker Bullet
  damage; harmful-effect cleanup is tied to that confirmed block path. Splash/lingering
  potion application, witch effects, tipped-arrow effects, and generic effect interception
  are not universal shield immunity promises. Instant Damage is damage-path coverage, not a
  claim that an already-applied potion effect can be removed afterward.

## Validation

- `./gradlew compileJava compileTestJava compileTestmodJava` — PASS
- `./gradlew test check build verifyReleaseJar dedicatedServerSmoke` — PASS
- `./gradlew runGametest` — PASS, 11/11 required tests
- Existing ordinary Magnet, Shadow Blade, Double Jump, and XP Syphon coverage remains green.
- `runClient`/real-player manual retest is still required for Magnet + Lumberjack, Sky Rage
  anvil/command paths, Feedback potion timing, and Auto Smelt with a real player.

## Status

`REMAINING_1_21_11_GAMEPLAY_FIXED_READY_FOR_RETEST`

Open manual gates: `MAGNET_LUMBERJACK`, `SKY_RAGE_PATH`, `FEEDBACK_SPEC/RUNTIME`,
`AUTO_SMELT_PIPELINE`, `REAL_PLAYER_TEST`. If the manual retest fails, use
`MANUAL_RETEST_FAILED` and do not proceed to cross-version work.
