# Custom Enchants 3.1.1 — Minecraft 1.20.1

**Compatibility:** Fabric, Minecraft 1.20.1, Java 17. Required: Fabric Loader >=0.18.4, Fabric API >=0.92.7+1.20.1, Cloth Config >=11.1.118. Mod Menu >=7.2.2 is optional.

Install `custom-enchants-3.1.1.jar` in `mods/`. Source branch: `3.1/1.20.1` (`afc74961731bca5e0f2298e6304b588d8eab4e9b`); verified by CI run `31193859578` (tests, check, build, release-JAR contract, 4/4 GameTests, dedicated-server smoke).

Highlights: final config UI; linked-drop Magnet fixes; resolved-loot Auto Smelt fixes; safe Shadow Blade teleport; Sky Rage item restrictions; authoritative Double Jump movement and sprint impulse; Feedback fixes; XP Syphon verification; runtime/mixin compatibility fixes.

Known limitation: on complex real-tree layouts, Magnet + Lumberjack can leave some secondary drops nearby. The release contract permits no loss or duplication.

Documentation: https://sqwiziiy.github.io/Custom-Enchants/ · Issues: https://github.com/sqwiziiy/Custom-Enchants/issues
