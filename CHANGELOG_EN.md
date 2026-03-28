# Changelog


All notable changes to the **Mentalitys | Custom Enchantments** mod are documented here.

## [2.6.0] - 2026-03-28

### Added

- **Tether Master** enchantment for fishing rods (uncommon, max level 3). Increases the pull strength when reeling in hooked mobs.
  - Level I: +15% pull strength.
  - Level II: +25% pull strength.
  - Level III: +40% pull strength.
  - Available from enchanting table and villager trades.
- Librarian trades: Tether Master I (tier 1, 10 emeralds), II (tier 3, 22 emeralds), III (tier 4, 36 emeralds).
- Config option: enable/disable enchantment.

### Changed

- **Shadow Blade** is now compatible with **Loyalty** again. A trident with both enchantments will teleport the player on throw and return as normal.

---

## [2.5.1] - 2026-03-28

### Changed

- **Shadow Blade** is now incompatible with **Loyalty** and **Glow Strike**.
  - Loyalty: trident returns after throwing; Shadow Blade teleports the player on throw — mechanics are mutually exclusive.
  - Glow Strike: two offensive special effects on one trident create an overpowered combo.
- **Glow Strike** on tridents is now incompatible with **Shadow Blade**.

---

## [2.5.0] - 2026-03-28

### Added

- **Vulnerability** enchantment for bows and crossbows (rare, max level 3). Arrows from this bow partially bypass the target's armor.
  - Level I: ignores 10% of armor.
  - Level II: ignores 20% of armor.
  - Level III: ignores 30% of armor.
  - Available from enchanting table and villager trades.
- Librarian villager trades: Vulnerability I (tier 2, 18 emeralds), II (tier 4, 36 emeralds), III (tier 5, 52 emeralds).
- Config options: enable/disable enchantment, configurable armor ignore percentage per level.

### Changed

- **Rebound**, **Feedback**, and **Guardian's Grace** are now mutually incompatible — only one of the three can be applied to a shield at a time.

---

## [2.4.0] - 2026-03-28

### Added

- **Guardian's Grace** enchantment for shields (rare, max level 3). When successfully blocking an attack, has a chance to restore food. At level III, also has a rare chance to heal the player.
  - Level I: 10% chance to restore 1 food point.
  - Level II: 20% chance to restore 1 food point.
  - Level III: 30% food chance + additional 10% chance to heal 2 HP (1 heart).
  - Available from enchanting table and villager trades.
- Librarian villager trades: Guardian's Grace I (tier 2, 16 emeralds), II (tier 4, 32 emeralds), III (tier 5, 50 emeralds).
- Config options: enable/disable enchantment, configurable chance per level.

### Changed

- **Second Wind** can now be applied to **any armor piece** (helmet, chestplate, leggings, boots). Effect durations now scale by the number of pieces enchanted:
  - 1 piece: Speed II — 2 sec, Resistance I — 1 sec
  - 2 pieces: Speed II — 3 sec, Resistance I — 1 sec
  - 3 pieces: Speed II — 3 sec, Resistance I — 2 sec
  - 4 pieces: Speed II — 4 sec, Resistance I — 2 sec
- **Second Wind** — removed the red screen overlay effect; smoke particles remain.
- **Lumberjack** — no longer triggers on `_wood` or `hyphae` blocks (full-bark crafted blocks); only works on true log/stem blocks and their stripped variants.

---

## [2.3.0] - 2026-03-27

### Added

- **Second Wind** enchantment for chestplates (very rare, max level 1). When the player's health drops below 1 heart (2 HP), instantly grants a burst of survival effects.
  - Speed II for 5 seconds (configurable).
  - Damage Resistance I for 5 seconds.
  - 100% knockback resistance for 5 seconds.
  - Visual effects: red pulsing screen overlay + smoke and cloud particles.
  - Cooldown: 60 seconds (configurable).
  - Available from enchanting table and villager trades.
- Librarian villager trade: Second Wind I (tier 5, 52 emeralds).
- Config options: enable/disable enchantment, configurable speed duration and cooldown.

### Changed

- **Auto Smelt** is now **incompatible with Fortune**.
- **Rebound** — reduced player self-knockback from 30% to 15%.

---

## [2.2.0] - 2026-03-27

### Added

- **Feedback** enchantment for shields (rare, max level 1). When blocking a magical attack (witch potion, evoker fangs, shulker bullet), the shield restores 2 durability and the player heals 1 heart of health.
  - Blocks all harmful potion effects (poison, slowness, weakness, etc.) while blocking.
  - Available from enchanting table and villager trades.
- Librarian villager trade: Feedback I (tier 3, 22 emeralds).
- Config options: enable/disable enchantment, configurable heal amount and durability restoration.

### Fixed

- **Vegetation** — fixed unreliable auto-replanting: replant now guaranteed to execute on the next server tick.
- **Vegetation** — added seedling protection: a freshly replanted crop cannot be broken for 10 ticks (0.5 sec) after planting.

---

## [2.1.0] - 2026-03-27

### Added

- **Rebound** enchantment for shields (rare, max level 3). When blocking a melee hit, knocks the attacker back and gives the player a small backwards impulse.
  - Level I: knockback 0.5, Level II: knockback 1.0, Level III: knockback 2.0.
  - Does not deal damage. Consumes extra shield durability.
  - Available from enchanting table and villager trades.
- Librarian villager trades: Rebound I (tier 2, 16 emeralds), II (tier 4, 32 emeralds), III (tier 5, 50 emeralds).
- Config options: enable/disable enchantment, configurable knockback strength per level.

### Changed

- **Vegetation** Level III now has **100% replant chance** (was 75%).

---

## [2.0.0] - 2026-03-27

### Added

- **Vegetation** enchantment for hoes (rare, max level 3). When breaking a mature crop, has a chance to auto-replant.
  - Level I: 30%, Level II: 60%, Level III: 75%.
  - Works with wheat, carrots, potatoes, beetroot, nether wart.
  - Available from enchanting table and villager trades.
- Librarian villager trades: Vegetation I (tier 2, 14 emeralds), II (tier 4, 30 emeralds), III (tier 5, 48 emeralds).
- Config options: enable/disable enchantment, configurable chance per level.

### Changed

- **Glow Strike** can now be enchanted on **tridents** — Glowing effect triggers when thrown trident hits.
- **Shadow Blade** — added distance-based teleport chance bonus: up to +10% at 30 blocks (linear scaling).

---

## [1.9.0] - 2026-03-26

### Added

- **Auto Smelt** enchantment for pickaxes (rare, max level 1). When mining ores that require smelting (iron, gold, copper), automatically drops the smelted result instead of raw ore.
  - Only applies to ores that have a smelting recipe (iron, gold, copper ores and their deepslate variants).
  - Grants smelting XP.
  - Available from enchanting table and villager trades.
- Librarian villager trade: Auto Smelt I (tier 4, 32 emeralds).
- Config options: enable/disable enchantment.
- Added GitHub source code link to mod description.

---

## [1.8.0] - 2026-03-26

### Changed

- **Shadow Blade** can now only be enchanted on **tridents** — added `canEnchant` check and anvil mixin protection.
- **Poison Blade** is now **incompatible with Fire Aspect**.
- **Drill** now works on **shovels** too — digs dirt, sand, gravel, etc. in a 3×3 area.

### Fixed

- **Drill** — fixed a bug where the slightest camera movement caused wrong blocks to be mined. Face detection now uses geometric player-to-block position calculation instead of raycast.

---

## [1.7.0] - 2026-03-26

### Added

- **Magnet** enchantment for tools (pickaxe, axe, shovel, hoe) (rare, max level 1). When mining blocks, items within a 5-block radius are automatically picked up into the player's inventory.
  - Works with Drill and Lumberjack — collects all drops from additional blocks.
  - Pickup radius is configurable (default 5 blocks).
  - Available from enchanting table and villager trades.
- Librarian villager trade: Magnet I (tier 3, 24 emeralds).
- Config options: enable/disable enchantment, configurable pickup radius.

### Fixed

- **Cloth Config settings screen** now opens via Mod Menu — added Mod Menu integration.

---

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
