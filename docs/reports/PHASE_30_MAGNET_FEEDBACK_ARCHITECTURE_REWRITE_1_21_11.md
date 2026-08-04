# Phase 30 — Magnet/Feedback architecture rewrite, 1.21.11

Date: 2026-08-04  
Branch: `3.1/1.21.11`  
Baseline: `97394ad`  
Version: `3.1.1`, Minecraft `1.21.11`

## Why the previous fixes were insufficient

The former Magnet implementation waited for ItemEntities and then searched AABBs. A timeout
increase could not recover an entity that was never associated, merged before the scan, or was
spawned after the request's snapshot. Feedback's generic effect hook likewise could not cover
null-source and instant potion paths.

## Implemented design

Magnet now opens a thread-scoped context around each real `Block.playerDestroy` call. A
`ServerLevel.addFreshEntity` return hook captures each final `ItemEntity` while that context is
active. This observes the final Auto Smelt stack, not raw ore, and immediately uses the vanilla
`playerTouch` inventory/remainder behavior. The existing position-based request remains bounded
fallback/retry coverage for unusual late drops; its hard window is 100 ticks.

Feedback now has a source-scoped harmful-effect policy for `addEffect(effect, source)`: it
requires active blocking, a real Feedback shield, same-level finite source coordinates, and a
front-facing shield arc. Null-source and later area-cloud exposure are not converted into global
immunity. Confirmed vanilla shield blocks also perform the existing harmful cleanup once.

## Call-chain evidence

The 1.21.11 mappings identify the relevant paths as `Block.playerDestroy`,
`ServerLevel.addFreshEntity`, `AbstractThrownPotion.onHitAsPotion`,
`ThrownSplashPotion.onHitAsPotion`, `ThrownLingeringPotion.onHitAsPotion`, and
`AreaEffectCloud.serverTick`. The spawn hook is deliberately placed after `addFreshEntity`
returns, so it sees the final entity produced by the already-transformed `Block.getDrops` path.

## Runtime validation

- 12/12 GameTests pass, including the new actual Auto Smelt + Magnet test: final ingot is in
  inventory and raw iron is absent.
- Existing ordinary Magnet, Auto Smelt-only, Shadow Blade, Sky Rage, Double Jump, and XP Syphon
  tests remain green.
- Compile, unit tests, build, release JAR contract, and dedicated-server smoke pass.

The disconnected GameTest harness cannot provide the full connected-player potion packet path;
manual splash/tipped/lingering/instant-impact tests remain required.

## Status

`MAGNET_AND_FEEDBACK_REWRITTEN_READY_FOR_RETEST`

No push, tag, release, Modrinth, Wiki, or other-version work was performed.
