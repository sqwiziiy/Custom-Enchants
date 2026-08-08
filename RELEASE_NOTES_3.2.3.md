# Custom Enchants 3.2.3 — Minecraft 1.20.1

Release date: 2026-08-08

## Fixed

- Feedback now rejects newly applied harmful status effects while an enchanted shield is actively raised.
- Harming / Instant Damage and the Feedback magic allowlist are blocked by an independent magic guard instead of depending on vanilla shield-facing or shield-bypass damage resolution.
- Raising Feedback no longer removes, shortens, replaces, or otherwise rewrites harmful effects that were already active before the shield was raised.
- Beneficial and neutral effects continue to apply normally while Feedback is active.

## Validation

- Final in-game Feedback behavior was accepted for release.
- CI passed unit/contract tests, `check`, `build`, `verifyReleaseJar`, Fabric GameTest, and dedicated-server smoke.
- Regression coverage verifies new harmful-effect rejection, preservation of existing harmful effects, beneficial/neutral effect passthrough, and direct magic/Harming protection.

No enchantment balance or configuration changes. Existing worlds and configurations remain compatible with 3.2.2.

Known 1.20.1 limitation remains unchanged: Magnet + Lumberjack is generally usable, but complex real-tree layouts can leave some secondary log drops on nearby ground. No item duplication is expected.
