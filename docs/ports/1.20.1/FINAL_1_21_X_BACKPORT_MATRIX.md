# Final 1.21.x Backport Matrix — 1.20.1

| Area | Local status | Evidence | Manual status |
| --- | --- | --- | --- |
| Double Jump | Frozen | Carried-forward PASS | Reconfirm before push |
| Sky Rage trident compatibility | Frozen | Carried-forward PASS | Reconfirm before push |
| Magnet + Lumberjack | Fixed, ready for retest | Survival GameTest: five logs, exact picked/remainder accounting | Pending |
| Auto Smelt | Fixed, ready for retest | Survival GameTest: final spawned iron ingot, no raw iron | Pending |
| Auto Smelt + Magnet | Pending manual integration retest | Both hooks share the final `ItemEntity` path | Pending |

Baseline SHA: `797752ea6da13728c1841771e03ca1d2c0eee731`. No remote changes were made.
