# Enchantments

This is the current 1.20.1 reference. Roman numerals are the supported levels;
all enchantments are registered under the `custom-enchants` namespace.

| Enchantment | Supported item(s) | Max | Current behavior |
|---|---|---:|---|
| Glow Strike | sword, axe, trident | III | Player hits apply Glowing; duration is configurable. Conflicts with Knockback; on tridents also Shadow Blade. |
| Double Jump | boots | I | Enables one mid-air jump with particles and durability handling. |
| Drill | pickaxe, shovel | I | Breaks a safe 3×3 plane; sneaking keeps single-block mining. |
| Poison Blade | sword, axe | III | Player hits apply Poison; duration is configurable. Conflicts with Fire Aspect. |
| Lumberjack | axe | III | Safely fells a detected natural tree within the configured block cap. |
| Shadow Blade | trident | III | Chance-based teleport behind the hit target and Slowness; chance/duration are configurable. |
| Magnet | compatible tool/weapon | I | Pulls nearby item entities toward the player within the configured radius. |
| Auto Smelt | pickaxe, shovel, axe | I | Uses the block loot pipeline and smelts eligible drops without bypassing normal block-break callbacks. Conflicts with Fortune. |
| Vegetation | hoe | III | Chance-based extra vegetation drops; chance is configurable. |
| Rebound | shield | III | While blocking, rebounds eligible incoming attacks with level-based knockback. |
| Feedback | shield | I | While blocking, protects against configured harmful/magic damage paths, clears harmful effects, and can heal/repair. |
| Second Wind | leggings | I | Grants a short speed burst with a cooldown after the configured trigger. |
| Guardian's Grace | chestplate | III | Chance-based protection effect; chance is configurable. |
| Vulnerability | bow, crossbow | III | Chance-based damage mitigation/ignore effect; percentage is configurable. |
| Tether Master | fishing rod | III | Increases pull strength when reeling in entities. |
| Sky Rage | bow, crossbow | III | During a thunderstorm and with sky access, arrows can summon lightning; cooldown is configurable. |
| XP Syphon | sword | III | Hits can create XP orbs before death; the gameplay handler controls the amount/chance. |
| Kinetic Discharge | elytra | III | A fast elytra landing creates a scoped knockback wave; level III also deals configured damage. |
| Sculk Bloom | sword | II | A kill can trigger catalyst-like sculk spreading around the death position. |

## Books, anvil, and limits

The enchantments use vanilla-compatible enchantment books and anvil flows. Some
effects intentionally extend an enchantment's item applicability for books or
anvils (for example Vulnerability supports crossbows). Creative inventory and
commands remain valid ways to obtain test items. Enchantment-table availability,
item applicability, and incompatibilities are enforced by the registered
enchantment definitions; this document does not promise unsupported combinations.

Gameplay edge cases still requiring automated/in-world coverage include full
multiplayer lifecycle coverage, protection-mod interaction, and exhaustive
loot/trade/anvil combinations. See `docs/TEST_MATRIX.md` locally.

## Librarian trades

Every built-in offer is an enchanted book for emeralds. The table lists the
implemented librarian tier and emerald price; vanilla demand and restock rules
still apply.

| Book | Tier | Emeralds | Book | Tier | Emeralds |
|---|---:|---:|---|---:|---:|
| Glow Strike I | 1 | 10 | Glow Strike II | 3 | 28 |
| Glow Strike III | 5 | 48 | Double Jump I | 4 | 38 |
| Drill I | 5 | 50 | Poison Blade I | 1 | 12 |
| Poison Blade II | 3 | 30 | Poison Blade III | 5 | 50 |
| Lumberjack I | 2 | 14 | Lumberjack II | 4 | 32 |
| Lumberjack III | 5 | 52 | Shadow Blade I | 2 | 16 |
| Shadow Blade II | 4 | 34 | Shadow Blade III | 5 | 54 |
| Magnet I | 3 | 24 | Auto Smelt I | 4 | 32 |
| Vegetation I | 2 | 14 | Vegetation II | 4 | 30 |
| Vegetation III | 5 | 48 | Second Wind I | 5 | 52 |
| Vulnerability I | 2 | 18 | Vulnerability II | 4 | 36 |
| Vulnerability III | 5 | 52 | Tether Master I | 1 | 10 |
| Tether Master II | 3 | 22 | Tether Master III | 4 | 36 |
| Sky Rage I | 3 | 22 | Sky Rage II | 4 | 38 |
| Sky Rage III | 5 | 55 | XP Syphon I | 2 | 14 |
| XP Syphon II | 3 | 26 | XP Syphon III | 4 | 40 |
| Kinetic Discharge I | 4 | 28 | Kinetic Discharge II | 5 | 44 |
| Kinetic Discharge III | 5 | 62 | Sculk Bloom I | 4 | 30 |
| Sculk Bloom II | 5 | 50 |  |  |  |
