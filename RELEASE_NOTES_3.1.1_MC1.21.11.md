# Custom Enchants 3.1.1 for Minecraft 1.21.11

## Compatibility

Minecraft 1.21.11, Java 21, Fabric Loader >=0.19.3, Fabric API >=0.141.6+1.21.11,
Cloth Config >=21.11.153; Mod Menu >=17.0.0 is optional.

## Gameplay fixes

Magnet now collects fresh drops on the next server tick. Double Jump is server-authoritative,
Shadow Blade uses an authoritative server teleport, Auto Smelt preserves the vanilla drop pipeline,
and Sky Rage anvil results are restricted to bows/crossbows. Feedback and XP Syphon regression
coverage was added.

## Verification

85 unit/contract tests and 7 GameTests pass locally. Remote CI and manual gameplay checks remain
required; this note is prepared for 3.1.1 and does not announce publication.

## Installation

Install Fabric Loader, Fabric API and Cloth Config versions above. Mod Menu is optional.

## Known limitations

Final artifacts, remote CI, manual gameplay checks and publication are pending.
