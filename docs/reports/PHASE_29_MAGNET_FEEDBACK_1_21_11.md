# Prompt 29 — Magnet integrations and Feedback finalization

Date: 2026-08-04  
Branch: `3.1/1.21.11`  
Baseline: `5bdb9e3f56997cef91caf9ff76b82291db825fd4`

## Changes

- Extended Magnet's linked-drop hard window from 20 to 100 ticks so final Auto Smelt
  entities and late Lumberjack drops can be observed without depending on the raw stack type.
  Existing UUID association and per-position scanning remain in place; no world-wide scan or
  special Auto Smelt+Magnet branch was added.
- Added a source-scoped Feedback effect-application hook. Harmful effects passed through
  `LivingEntity.addEffect(effect, source)` are rejected only for an actively used Feedback
  shield with a finite, same-level, front-facing source. Null-source effects, back-facing
  sources, wrong shields, and area-cloud later exposure remain vanilla.
- Confirmed Feedback cleanup now runs on confirmed vanilla shield blocks as well as the
  independent allowlisted magic path, while heal/repair remain once per damage call.

## Validation

- `./gradlew --no-daemon --console=plain test check build verifyReleaseJar runGametest dedicatedServerSmoke`
  — PASS; 11/11 required GameTests.
- Release contract and dedicated-server smoke — PASS.
- `runClient` on Minecraft 1.21.11 / custom-enchants 3.1.1 previously launched successfully.

## Manual gates

Actual connected-player retest remains required for large oak, 2×2 spruce/jungle, dark oak,
Auto Smelt+Magnet ore variants, splash/tipped/lingering potion geometry, and full-inventory
remainder behavior. The disconnected GameTest harness cannot validate effect packets because its
`ServerPlayer.connection` is null. No remote operation was performed.

Status: `REMAINING_1_21_11_GAMEPLAY_FIXED_READY_FOR_RETEST`
