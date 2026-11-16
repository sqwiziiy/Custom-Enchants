# Custom Enchants 3.1.0 for Minecraft 1.21.1

## Compatibility

- Minecraft 1.21.1
- Fabric Loader `>=0.19.3`
- Fabric API `>=0.116.15+1.21.1`
- Java `>=21`

## Release coordinates

- Release line: 3.1
- Minecraft branch: `3.1/1.21.1`
- Minecraft version: 1.21.1
- Mod version: 3.1.0
- Release tag: `v3.1.0-mc1.21.1`

## Port highlights

Custom Enchants 3.1.0 is now available for Minecraft 1.21.1 on Java 21. This is a full
compatibility port of the stable 1.20.1 release (`v3.1.0`), moving through Minecraft's
data-driven enchantment rewrite: all 19 enchantments now ship as
`data/custom-enchants/enchantment/*.json` definitions with `ResourceKey`/holder-based lookup,
villager trades run on the 1.21.1 `MerchantOffer`/`ItemCost` API, Double Jump and Second Wind
networking use typed `CustomPayload`, and all 19 mixins were re-verified against Minecraft
1.21.1's Mojang-mapped classes.

## Gameplay parity

A full parity audit compared every enchantment, all 44 librarian trade offers, and the
complete configuration schema against the 1.20.1 baseline. Result: **zero gameplay
regressions**. Every enchanted item's supported items, max level, cost curve, weight, anvil
cost, and conflict set match 1.20.1 exactly; every trade offer's price, tier, uses and XP
match exactly; the config schema is byte-identical (existing 1.20.1 config files load
unchanged, no manual migration needed). No gameplay balance was changed as part of this port.
See `docs/ports/1.21.1/FINAL_PARITY_MATRIX.md` in the source repository for the full
mechanic-by-mechanic evidence.

## Installation

Download `custom-enchants-3.1.0-mc1.21.1.jar` from the GitHub release and place it in the
Fabric `mods/` directory.

## Required dependencies

Fabric Loader `>=0.19.3`, Fabric API `>=0.116.15+1.21.1`, Cloth Config `>=15.0.140`
and Java `>=21`.

## Optional dependencies

Mod Menu `>=11.0.4` provides the optional client configuration UI.

## Verification

The release is checked by 70 JUnit/contract tests, 5 official Fabric GameTests
(including runtime proof that all 19 enchantments resolve in the live registry, that the
melee combat hook applies a representative effect, and that projectile enchantment context
is a true shot-time snapshot surviving a weapon switch before impact), release-JAR contract
verification, isolated dedicated-server smoke, and a full local + remote CI pass.

## Known limitations

Interactive client verification (title screen, Mod Menu screen contents, config screen UI,
translations rendering, representative enchantment recognized visually) could not be
performed in this headless build environment. A bounded headless client boot did reach a
live joined world with zero error-level log output, `cloth-config`/`modmenu`/`custom-enchants`
all loading cleanly — strong evidence the client entrypoint, mixins and resources are sound —
but no human/visual confirmation was captured. As with the 1.20.1 release, the complete
survival, protection, multiplayer, reconnect, trade/anvil and client visual matrix remains
partially unverified beyond the automated and headless-runtime evidence described above.

## Upgrade notes

The 3.1.0 release for Minecraft 1.21.1 preserves the established enchantment IDs, translation
keys and configuration compatibility baseline from the 1.20.1 release. Existing server
configuration files can be copied over without modification. Worlds/saves are not
cross-compatible between Minecraft 1.20.1 and 1.21.1 — this is a Minecraft engine constraint,
not specific to this mod.
