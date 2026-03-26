# Changelog


All notable changes to the **Mentalitys | Custom Enchantments** mod are documented here.

## [1.6.0] - 2026-03-26

### Added

- **Shadow Blade** enchantment for tridents (very rare, max level 3). On hit, has a chance to teleport the player behind the target and apply Slowness II.
  - Level I: 15% chance, slowness 1 sec. Level II: 25% chance, 2 sec. Level III: 35% chance, 3 sec.
  - Incompatible with Channeling and Riptide.
  - Available from enchanting table and villager trades.
- Librarian villager trades: Shadow Blade I (tier 2, 16 emeralds), II (tier 4, 34 emeralds), III (tier 5, 54 emeralds).
- Config options: enable/disable enchantment, configurable chance and slowness duration per level.

---

## [1.5.1] - 2026-03-26

### Fixed

- **Lumberjack** now works with **Nether trees** (crimson and warped stems) — added `wart_blocks` check alongside regular leaves.
- Fixed incomplete felling of **large oak trees** and branching trees — neighbor search now covers all 26 directions (full 3×3×3 cube), including horizontal diagonals.

---

## [1.5.0] - 2026-03-26

### Added

- **Lumberjack** enchantment for axes (rare, max level 3). Chops down an entire tree when one log is broken.
  - Level I: small trees (up to 16 blocks), Level II: medium (up to 48), Level III: large (up to 128).
  - Smart tree detection: verifies leaves exist, only breaks logs of the same type.
  - Does not break blocks below the broken log — safe for player-built log structures.
  - Consumes axe durability per log broken.
  - Available from enchanting table and villager trades.
- Librarian villager trades: Lumberjack I (tier 2, 14 emeralds), II (tier 4, 32 emeralds), III (tier 5, 52 emeralds).
- Config options: enable/disable enchantment, configurable max blocks per level.

---

## [1.4.0] - 2026-03-26

### Added

- **Poison Blade** enchantment for swords and axes (rare, max level 3). On hit, applies the Poison effect to the target.
  - Level I: 2 sec, Level II: 3 sec, Level III: 4 sec.
  - Can be applied to swords and axes.
  - Available from enchanting table and villager trades.
- Librarian villager trades: Poison Blade I (tier 1, 12 emeralds), II (tier 3, 30 emeralds), III (tier 5, 50 emeralds).
- Config options: enable/disable enchantment, configurable poison duration for each level.

---

## [1.3.0] - 2026-03-26

### Added

- **Drill** enchantment for pickaxes (very rare, max level 1). Breaks blocks in a 3×3 area around the center block.
  - Disabled while sneaking (Shift) — allows single-block mining.
  - The 3×3 plane is determined by the face of the block being mined.
  - Consumes pickaxe durability for each extra block broken.
  - Only breaks blocks appropriate for the pickaxe; unbreakable blocks are ignored.
- Librarian villager trade: Drill I — Master (tier 5), 50 emeralds.
- Drill enable/disable toggle in config.

---

## [1.2.0] - 2026-03-25

### Added

- **White particles** on double jump (12 `cloud` particles under the player).
- **Durability cost** on double jump: 67% chance to consume 1 durability from boots.
  - **Unbreaking** enchantment reduces this chance as usual.
- Server-side double jump validation via network packet (client → server).

---

## [1.1.0] - 2026-03-25

### Added

- **Double Jump** enchantment for boots (very rare, max level 1). Allows a second jump in mid-air.
- Librarian villager trade: Double Jump I — Expert (tier 4), 38 emeralds.
- **Cloth Config API** integration — mod configuration via JSON file (`config/custom-enchants.json`) and GUI settings screen.
- Configurable Glow Strike effect duration for each level.
- Ability to enable/disable each enchantment individually.
- Settings screen localization (EN/RU).
- English versions of README and CHANGELOG.

### Changed

- Mod renamed from "Glow Strike" to **"Mentalitys | Custom Enchantments"**.
- Mod ID changed from `glowstrike` to `custom-enchants`.
- Code refactored: split into `config` and `enchantment` packages.
- Glow Strike duration is now read from config.

---

## [1.0.0] - 2026-03-25

### Added

- **Glow Strike** enchantment for swords and axes (max level 3).
- **Glowing** effect on hit: 2 sec (I), 4 sec (II), 7 sec (III).
- Incompatibility with **Knockback** enchantment.
- Effect triggers only when attacker is a Player and target is a LivingEntity.
- Librarian villager trades (tiers 1, 3, 5).
- Localization: English (`en_us.json`) and Russian (`ru_ru.json`).
- Built for Minecraft 1.20.1, Fabric API, Java 21.
