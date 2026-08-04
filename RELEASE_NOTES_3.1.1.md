# Custom Enchants 3.1.1 for Minecraft 1.20.1

## Compatibility

Minecraft 1.20.1, Java 17, Fabric Loader >=0.18.4, Fabric API >=0.92.7+1.20.1,
Cloth Config >=11.1.118; Mod Menu >=7.2.2 is optional.

## Gameplay fixes

Magnet collects fresh block drops on the following server tick; Shadow Blade uses a server
teleport; Double Jump is server-authoritative and rejects water/lava/fall-flying requests;
Feedback, Auto Smelt, Sky Rage and XP Syphon received regression fixes and deterministic tests.

## Verification

Java 17 unit/contract tests and GameTests pass locally. Full cross-version acceptance and remote
CI are still required. This release note is prepared for 3.1.1 and is not a publication claim.

## Installation

Install the matching Fabric Loader, Fabric API and Cloth Config versions above. Mod Menu is optional.

## Known limitations

Manual gameplay checks, remote CI and final artifacts remain pending.
