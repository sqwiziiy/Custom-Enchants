# Custom Enchants 3.2.4 — Minecraft 1.21.1

Release date: 2026-08-08

## Fixed

- Custom enchanted-book trades from librarians now always require both the configured emerald price and one regular book.
- Fixed cases where a custom enchanted book could be purchased for emeralds only.
- Custom enchanted-book levels are no longer gated behind higher librarian tiers: any defined custom enchantment level can now roll from a novice librarian onward, matching vanilla enchanted-book availability more closely.
- Existing custom-book emerald prices, enchantment levels, max uses, villager XP rewards, and price multipliers are preserved.

## Validation

- Librarian trade behavior was manually verified in-game before release.
- CI passed unit/contract tests, `check`, `build`, release-JAR verification, Fabric GameTest, and dedicated-server smoke.
- Regression coverage verifies the required regular-book input and novice availability for the full custom librarian offer table.

Existing worlds and configurations remain compatible with 3.2.3.
