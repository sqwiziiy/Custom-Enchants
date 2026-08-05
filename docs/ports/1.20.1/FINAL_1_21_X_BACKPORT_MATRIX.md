# Final 1.21.x semantic backport matrix — Minecraft 1.20.1

Baseline local `4032d94f2fca4a7f86eaef2882023c9223173b60`; remote `e18ca698cb58d7d4ea55646f712acb50483327e6`; safety branch `backup/3.1.1-pre-final-backport-1.20.1`.

| Area | Current 1.20.1 | Final approved behavior | API difference | Backport plan | Tests |
| --- | --- | --- | --- | --- | --- |
| Config UI | final UI implementation | 14 tabs, availability first, 19 toggles | Cloth Config 11 / Java 17 | retained; no rewrite | structure green; visual/save/reload pending |
| Magnet | scoped `Block.playerDestroy` context + `ServerLevel.addFreshEntity` capture | current final item is passed to vanilla `playerTouch` immediately | classic block/item APIs | `MagnetBlockDropContextMixin` + `MagnetFinalItemSpawnMixin` | unit/compile/GameTest load green; manual inventory cases pending |
| Magnet + Lumberjack | batch secondary break | child context per block | classic player destroy | adapt break service | tree runtime |
| Auto Smelt | legacy drops | final transformed entity chain | LootContext/recipes | adapt getDrops | ore runtime |
| Shadow Blade | safe candidate/floor checks | only a successful server teleport counts as a proc; then Slowness applies | old teleport overload | preserve classic signature | teleport planner + compile/GameTest load green; positional cases pending |
| Sky Rage | classic enchantments | bow/crossbow/book only | EnchantmentCategory | anvil + category filter | compatibility |
| Double Jump | server-authoritative airborne Y plus vanilla sprint delta | `0.42` Y, one `0.2F` yaw impulse when sprinting, sequence-protected approval packet | Fabric 1.20 raw packet API | request/S2C path without full-vector sync | validator + directional impulse unit tests; runtime manual pending |
| Feedback | generic shield hook | source-aware harmful impact block | old potion/projectile paths | event-scoped hooks | effect runtime |
| XP Syphon | existing policy | 5/10/15%, 1/2/3 XP | classic entity API | verify only if parity | forced success |
| Villager trades | classic books | 44 offer contract | NBT enchant data | audit definitions | offer contract |
| Runtime mixins | fail-fast default require=1 | changed drop and shield hooks remain explicit, cancellable only where needed | 1.20 descriptors | compiled and loaded on dedicated/GameTest/client | dedicated/GameTest/client startup green |
| CI/JAR/README | 1.20 contract | `3.1.1`, Java 17 JAR | version-specific | retain 1.20 metadata | build contract |
