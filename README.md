# Mentalitys | Custom Enchantments

> [Русская версия](README_RU.md)

A **Minecraft 1.20.1** mod for **Fabric API** that adds unique enchantments with flexible configuration via **Cloth Config API**.

---

## Enchantments

### ⚔️ Glow Strike

A weapon enchantment for **swords and axes**. On hit, applies the **Glowing** effect to the target, making them visible through walls.

| Level | Duration | Ticks |
|-------|----------|-------|
| I     | 2 sec    | 40    |
| II    | 4 sec    | 80    |
| III   | 7 sec    | 140   |

- **Rarity:** Rare
- **Max level:** 3
- **Incompatible** with Knockback
- Effect only triggers when the attacker is a Player and the target is a LivingEntity
- Duration for each level is configurable

### 🦘 Double Jump

A boots enchantment. Allows the player to perform a **second jump in mid-air**.

| Level | Effect      |
|-------|-------------|
| I     | Double jump |

- **Rarity:** Very Rare
- **Max level:** 1
- Spawns **white particles** under the player on double jump
- **67% chance** to consume 1 durability from boots (**Unbreaking** enchantment reduces this chance)
- Only works when not in water or lava
- Can be enabled/disabled via config

### ⛏️ Drill

A tool enchantment for **pickaxes and shovels**. Breaks blocks in a **3×3 area** around the center block.

| Level | Effect    |
|-------|----------|
| I     | 3×3 mining |

- **Rarity:** Very Rare
- **Max level:** 1
- **Disabled while sneaking** (Shift) — allows precise single-block mining
- The 3×3 plane is determined by the face of the block being mined
- Consumes 1 durability per extra block broken (Unbreaking applies)
- Only breaks blocks appropriate for the tool; unbreakable blocks (bedrock, etc.) are skipped
- Can be enabled/disabled via config
### 🗡️ Poison Blade

A weapon enchantment for **swords and axes**. On hit, applies the **Poison** effect to the target.

| Level | Duration |
|-------|----------|
| I     | 2 sec    |
| II    | 3 sec    |
| III   | 4 sec    |

- **Rarity:** Rare
- **Max level:** 3
- **Incompatible** with Fire Aspect
- Effect only triggers when the attacker is a Player and the target is a LivingEntity
- Duration for each level is configurable
- Can be obtained from enchanting table and villager trades

### 🪵 Lumberjack

An axe enchantment. Chops down an entire **tree** by breaking one log block. Smart detection ensures only natural tree logs are felled — won't break player-built structures.

| Level | Tree Size       | Max Blocks |
|-------|-----------------|------------|
| I     | Small trees     | 16         |
| II    | Medium trees    | 48         |
| III   | Large trees     | 128        |

- **Rarity:** Rare
- **Max level:** 3
- Only breaks **log blocks** of the same type (oak, birch, spruce, crimson, warped, etc.)
- Works with **Nether trees** (crimson and warped stems)
- Verifies the block is part of a natural tree (checks for leaves or wart blocks at the top)
- Does **not** break below the original block (safe for log walls)
- Consumes 1 durability per log broken (Unbreaking applies)
- Can be enabled/disabled via config; max blocks per level are configurable

### 🗡️ Shadow Blade

A trident enchantment. On hit, has a chance to **teleport the player behind the target** and apply **Slowness II** to the target.

| Level | Chance | Slowness Duration |
|-------|--------|--------------------|
| I     | 15%    | 1 sec              |
| II    | 25%    | 2 sec              |
| III   | 35%    | 3 sec              |

- **Rarity:** Very Rare
- **Max level:** 3
- **Incompatible** with Channeling and Riptide
- Teleports the player 1.5 blocks behind the target entity, facing it
- Chance and slowness duration are configurable per level
- Can be obtained from enchanting table and villager trades

### 🧲 Magnet

A tool enchantment for **pickaxes, axes, shovels, and hoes**. When mining blocks, items within a configurable radius are **automatically picked up** into the player's inventory.

| Level | Effect                      |
|-------|-----------------------------|
| I     | Auto-pickup within 5 blocks |

- **Rarity:** Rare
- **Max level:** 1
- Works with Drill and Lumberjack — collects all drops from additional blocks
- Pickup radius is configurable (default: 5 blocks)
- Can be obtained from enchanting table and villager trades
- Can be enabled/disabled via config

---

## Obtaining

Enchanted books can be purchased from **Librarian villagers**:

| Enchantment      | Villager Tier    | Price       |
|------------------|------------------|-------------|
| Glow Strike I    | Novice (1)       | 10 Emeralds |
| Glow Strike II   | Journeyman (3)   | 28 Emeralds |
| Glow Strike III  | Master (5)       | 48 Emeralds |
| Double Jump I    | Expert (4)       | 38 Emeralds |
| Drill I          | Master (5)       | 50 Emeralds |
| Poison Blade I   | Novice (1)       | 12 Emeralds |
| Poison Blade II  | Journeyman (3)   | 30 Emeralds |
| Poison Blade III | Master (5)       | 50 Emeralds |
| Lumberjack I     | Apprentice (2)   | 14 Emeralds |
| Lumberjack II    | Expert (4)       | 32 Emeralds |
| Lumberjack III   | Master (5)       | 52 Emeralds |
| Shadow Blade I   | Apprentice (2)   | 16 Emeralds |
| Shadow Blade II  | Expert (4)       | 34 Emeralds |
| Shadow Blade III | Master (5)       | 54 Emeralds |
| Magnet I         | Journeyman (3)   | 24 Emeralds |

All enchantments can also be obtained from the **enchanting table**.

---

## Configuration (Cloth Config)

The mod supports configuration via **Cloth Config API**. Config file: `config/custom-enchants.json`.

### Parameters

| Parameter                | Default | Description                         |
|--------------------------|---------|-------------------------------------|
| `glowStrikeEnabled`      | `true`  | Enable/disable Glow Strike          |
| `glowStrikeDurationL1`   | `40`    | Level I duration (ticks)            |
| `glowStrikeDurationL2`   | `80`    | Level II duration (ticks)           |
| `glowStrikeDurationL3`   | `140`   | Level III duration (ticks)          |
| `doubleJumpEnabled`      | `true`  | Enable/disable Double Jump          |
| `drillEnabled`           | `true`  | Enable/disable Drill                |
| `poisonBladeEnabled`     | `true`  | Enable/disable Poison Blade         |
| `poisonBladeDurationL1`  | `40`    | Level I duration (ticks)            |
| `poisonBladeDurationL2`  | `60`    | Level II duration (ticks)           |
| `poisonBladeDurationL3`  | `80`    | Level III duration (ticks)          |
| `lumberjackEnabled`      | `true`  | Enable/disable Lumberjack           |
| `lumberjackMaxBlocksL1`  | `16`    | Max blocks Level I                  |
| `lumberjackMaxBlocksL2`  | `48`    | Max blocks Level II                 |
| `lumberjackMaxBlocksL3`  | `128`   | Max blocks Level III                |
| `shadowBladeEnabled`     | `true`  | Enable/disable Shadow Blade         |
| `shadowBladeChanceL1`    | `15`    | Chance Level I (%)                  |
| `shadowBladeChanceL2`    | `25`    | Chance Level II (%)                 |
| `shadowBladeChanceL3`    | `35`    | Chance Level III (%)                |
| `shadowBladeSlowDurationL1` | `20` | Slowness duration Level I (ticks)   |
| `shadowBladeSlowDurationL2` | `40` | Slowness duration Level II (ticks)  |
| `shadowBladeSlowDurationL3` | `60` | Slowness duration Level III (ticks) |
| `magnetEnabled`          | `true`  | Enable/disable Magnet               |
| `magnetRadius`           | `5`     | Pickup radius (blocks)              |

---

## Project Structure

```
src/main/java/com/mentality/customenchants/
├── CustomEnchantsMod.java         — Main mod class, trade registration
├── config/
│   ├── ModConfig.java             — JSON config load/save
│   └── ModConfigScreen.java       — GUI settings screen (Cloth Config)
└── enchantment/
    ├── ModEnchantments.java       — Enchantment registration
    ├── GlowStrikeEnchantment.java — Glow Strike logic
    ├── DoubleJumpEnchantment.java — Double Jump definition
    ├── DoubleJumpServerHandler.java — Server-side double jump + durability
    ├── DrillEnchantment.java      — Drill definition
    ├── DrillHandler.java          — Server-side 3×3 mining logic
    ├── PoisonBladeEnchantment.java — Poison Blade logic
    ├── LumberjackEnchantment.java — Lumberjack definition
    ├── LumberjackHandler.java     — Server-side tree felling logic
    ├── ShadowBladeEnchantment.java — Shadow Blade logic
    └── ModEnchantments.java       — Enchantment registration

src/client/java/com/mentality/customenchants/
├── CustomEnchantsClient.java      — Client initializer
├── config/
│   └── ModConfigScreen.java       — GUI settings screen (Cloth Config)
└── enchantment/
    └── DoubleJumpHandler.java     — Client-side double jump logic
```

---

## Requirements

- Minecraft 1.20.1
- Fabric Loader ≥ 0.18.4
- Fabric API
- Cloth Config API ≥ 11.0.0
- Java 21+

## Building

```bash
./gradlew build
```

The compiled jar will be in `build/libs/`.

## License

CC0-1.0
