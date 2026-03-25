# Changelog

> [Русская версия](CHANGELOG.md)

All notable changes to the **Mentalitys | Custom Enchantments** mod are documented here.

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
