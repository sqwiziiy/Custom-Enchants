# Mentalitys | Custom Enchantments

> [Русская версия](README.md)

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
- Only works when not in water or lava
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

---

## Configuration (Cloth Config)

The mod supports configuration via **Cloth Config API**. Config file: `config/custom-enchants.json`.

### Parameters

| Parameter              | Default | Description                         |
|------------------------|---------|-------------------------------------|
| `glowStrikeEnabled`   | `true`  | Enable/disable Glow Strike          |
| `glowStrikeDurationL1`| `40`    | Level I duration (ticks)            |
| `glowStrikeDurationL2`| `80`    | Level II duration (ticks)           |
| `glowStrikeDurationL3`| `140`   | Level III duration (ticks)          |
| `doubleJumpEnabled`   | `true`  | Enable/disable Double Jump          |

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
    └── DoubleJumpEnchantment.java — Double Jump definition

src/client/java/com/mentality/customenchants/
├── CustomEnchantsClient.java      — Client initializer
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
