# Changelog


All notable changes to the **Mentalitys | Custom Enchantments** mod are documented here.

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
