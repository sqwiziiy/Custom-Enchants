# Custom Enchants 3.2.2 — Minecraft 1.20.1

Release date: 2026-08-08

## Fixed

- Drill now resolves the mined face from the player's actual view ray instead of inferring it from eye position relative to the block center.
- Drill 3×3 orientation is stable at diagonal camera angles and near block edges/corners.
- Drill keeps processing the remaining safe blocks in its planned 3×3 plane instead of being derailed by unrelated invalid positions.
- Sneaking still keeps normal single-block mining.

## Validation

- Manual in-game Drill validation passed on Minecraft 1.20.1, including walls, floors, ceilings, diagonal angles, edge/corner aiming, holes in the 3×3 plane, and sneaking.
- CI passed unit/contract tests, `check`, `build`, `verifyReleaseJar`, Fabric GameTest, and dedicated-server smoke.

No enchantment balance or configuration changes. Existing worlds and configurations remain compatible with previous 3.1.x releases.

Known 1.20.1 limitation remains unchanged: Magnet + Lumberjack is generally usable, but complex real-tree layouts can leave some secondary log drops on nearby ground. No item duplication is expected.
