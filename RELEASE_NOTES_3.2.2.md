# Custom Enchants 3.2.2 — Minecraft 1.21.1

Release date: 2026-08-08

## Fixed

- Drill now resolves the mined face from the player's actual view ray instead of inferring it from eye position relative to the block center.
- Drill 3×3 orientation is stable at diagonal camera angles and near block edges/corners.
- A single unsafe, air, unloaded, protected, or otherwise unbreakable planned block no longer aborts the rest of the Drill 3×3 plane.
- Sneaking still keeps normal single-block mining.

## Validation

- Manual in-game Drill validation passed on Minecraft 1.21.1, including walls, floors, ceilings, diagonal angles, edge/corner aiming, holes in the 3×3 plane, and sneaking.
- CI passed unit/contract tests, `check`, `build`, `verifyReleaseJar`, Fabric GameTest, and dedicated-server smoke.

No enchantment balance or configuration changes. Existing worlds and configurations remain compatible with previous 3.1.x releases.
