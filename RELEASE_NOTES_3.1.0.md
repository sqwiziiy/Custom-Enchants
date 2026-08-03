# Custom Enchants 3.1.0

## Compatibility

- Minecraft 1.20.1
- Fabric Loader `>=0.18.4`
- Fabric API `>=0.92.7+1.20.1`
- Java `>=17`

## Release coordinates

- Release line: 3.1
- Minecraft branch: `3.1/1.20.1`
- Minecraft version: 1.20.1
- Mod version: 3.1.0
- Release tag: `v3.1.0`

## Highlights

Custom Enchants 3.1.0 consolidates the Java 17/Fabric 1.20.1 baseline with safer
configuration, block-breaking, loot, projectile, shield, teleportation and
multiplayer lifecycle behavior.

## Major behavior fixes

- Auto Smelt transforms resolved vanilla loot while preserving Silk Touch/Fortune behavior.
- Feedback requires a real front-facing active shield block; Rebound requires a confirmed block.
- Shadow Blade fail-closes when no safe destination exists.
- Magnet respects thrower ownership and pickup delay.
- Kinetic Discharge does not repair pre-existing Elytra damage.
- Sculk Bloom uses the direct lethal weapon snapshot.
- Double Jump is server-authoritative and limited to one activation per airtime.

## Installation

Download `custom-enchants-3.1.0.jar` from the GitHub release and place it in the
Fabric `mods/` directory.

## Required dependencies

Fabric Loader `>=0.18.4`, Fabric API `>=0.92.7+1.20.1`, Cloth Config `>=11.1.118`
and Java `>=17`.

## Optional dependencies

Mod Menu `>=7.2.2` provides the optional client configuration UI.

## Verification

The release is checked by 62 JUnit/contract tests, 2 official Fabric GameTests,
release-JAR contract verification and isolated dedicated-server smoke.

## Known limitations

Client runtime and the complete survival, protection, multiplayer, reconnect,
trade/anvil and client visual matrix remain partially unverified.

## Upgrade notes

The 3.1.0 release preserves the established enchantment IDs and configuration
compatibility baseline. Keep a backup of the server configuration before upgrade.
