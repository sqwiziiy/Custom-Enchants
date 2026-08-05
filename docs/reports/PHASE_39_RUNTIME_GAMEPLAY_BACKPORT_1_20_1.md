# Phase 39 — runtime gameplay backport, Minecraft 1.20.1

Status: `MC_1_20_1_GAMEPLAY_READY_FOR_MANUAL_RETEST`

Baseline: `4032d94f2fca4a7f86eaef2882023c9223173b60` on `3.1/1.20.1` (Java 17, mod 3.1.1). Safety branch retained: `backup/3.1.1-pre-final-backport-1.20.1`. No remote operation, tag, publication, or push was performed.

## Completed UI status

`CONFIG_UI_IMPLEMENTED_COMPILE_GREEN` remains intact: availability-first layout, 14 categories, 19 toggles, parameter-only tabs, RU/EN parity, and the UI structure contract. The visual, save/reload, and small-window checks remain manual gates.

## Gameplay implementation and call chains

| Subsystem | Implemented 1.20.1 runtime path | Automated evidence | Manual gate still required |
| --- | --- | --- | --- |
| Magnet | `Block.playerDestroy` scope → `Block.getDrops`/`Block.popResource` → `ServerLevel.addFreshEntity` → linked `ItemEntity.playerTouch` | `MagnetPickupPolicy` tests, clean suite, GameTest server load | full/partial inventory, ownership and old-item isolation |
| Magnet + Lumberjack | every secondary `gameMode.destroyBlock` invokes its own `playerDestroy` scope | planner/reentrancy tests and runtime load | 5-log, large-tree and two-player cases |
| Auto Smelt + Magnet | vanilla final drops are transformed at `Block.getDrops`; the spawned transformed entity is captured by Magnet | transformer/context tests and runtime load | ore/Fortune/Silk/full-inventory cases |
| Shadow Blade | `ThrownTridentMixin` → `SafeTeleportService.tryTeleportBehind`; Slowness is now conditional on a successful server teleport | candidate planner and clean suite | four directions and unsafe fallback cases |
| Sky Rage | `EnchantmentCategory.BOW`, `canEnchant` permits bow/crossbow; book route remains available; anvil guard rejects other result items | anvil policy tests | anvil, `/enchant`, book and trident checks |
| Double Jump | key-edge C2S → server eligibility/enchantment/airtime validation → explicit Y/vanilla sprint impulse → sequenced S2C approval; client merges only the fresh impulse | validator and directional impulse tests | connected-client movement, water/lava, replay and sprint cases |
| Feedback/Rebound | scoped `LivingEntity.hurt` records vanilla shield block; Feedback has magic/shulker allowlist and Rebound accepts direct melee entities only | shield policy tests and runtime load | potion/arrow/shulker, front/back, recoil/level checks |
| XP Syphon | server-side player melee `doPostAttack` makes one chance roll and spawns the documented 1/2/3-XP orb | deterministic policy tests | forced success/failure and no-death-duplication checks |

## Mixin matrix

| Mixin | Target/method | Injection | Runtime evidence |
| --- | --- | --- | --- |
| `MagnetBlockDropContextMixin` | `Block.playerDestroy` | `WrapMethod`, scoped context | compile, dedicated smoke, GameTest, client startup |
| `MagnetFinalItemSpawnMixin` | `ServerLevel.addFreshEntity` | `RETURN` | compile, dedicated smoke, GameTest, client startup |
| `AutoSmeltBlockDropsMixin` | `Block.playerDestroy` / static `getDrops` | `WrapMethod` / `RETURN` | compile, dedicated smoke, GameTest, client startup |
| `ShieldBlockContextMixin` | `LivingEntity.hurt` | wrap + shield-decision wrap operation | compile, dedicated smoke, GameTest, client startup |
| `ShieldFeedbackMixin`, `ShieldReboundMixin` | `LivingEntity.hurt` | HEAD/RETURN | compile, dedicated smoke, GameTest, client startup |

The mixin configuration keeps `defaultRequire: 1`; no blanket optional injection was introduced.

## Regression and trade coverage

All 19 enchantments remain registered through `ModEnchantments`, retain their item/category/max-level/config definitions, and are covered by the existing contract or behavioral suites. `CustomEnchantsMod` retains the 44 librarian offers; `ShieldTradeDefinitionTest` and resource/registration contracts guard their definitions. The remaining offer-by-offer in-world inspection is included in manual retest.

## Local pipeline evidence

All commands ran with OpenJDK 17.0.20:

- `clean test` — PASS
- `check` — PASS
- `build` — PASS; release contract found `custom-enchants-3.1.1.jar`
- `verifyReleaseJar` — PASS
- `gameTest` — PASS, 2/2 required Fabric GameTests
- `dedicatedServerSmoke` — PASS; Minecraft 1.20.1, Fabric Loader 0.18.4 and Custom Enchants 3.1.1 initialized before the expected EULA gate
- `runClient` — startup PASS; Minecraft 1.20.1, Java 17 and Custom Enchants 3.1.1 logged. The process was stopped after the startup observation, so it is not visual/manual approval.

## Manual retest gates

Run the UI save/reload, RU/EN, search and small-window checks; then test Magnet/Lumberjack inventory accounting, Auto Smelt chains, Shadow Blade safety, Sky Rage application paths, Double Jump movement/replay/fluid rejection, Feedback/Rebound impact cases, and XP Syphon orb values. Manual approval is the only remaining gate before any push; no push is authorized by this phase.
