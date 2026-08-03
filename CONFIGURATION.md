# Configuration

The file is `config/custom-enchants.json`. It is created with defaults, merged
with the current schema, normalized, and saved atomically when possible.
`configVersion` is currently `1`. Boolean fields enable/disable the mechanic.
Durations and cooldowns are ticks unless noted.

| Group | Fields (default; accepted range) |
|---|---|
| Glow Strike | `glowStrikeEnabled` (true); `glowStrikeDurationL1/L2/L3` (40/80/140; 0–6000, non-decreasing) |
| Double Jump | `doubleJumpEnabled` (true) |
| Drill | `drillEnabled` (true) |
| Poison Blade | `poisonBladeEnabled` (true); `poisonBladeDurationL1/L2/L3` (40/60/80; 0–6000, non-decreasing) |
| Lumberjack | `lumberjackEnabled` (true); `lumberjackMaxBlocksL1/L2/L3` (16/48/128; 1–256, non-decreasing) |
| Shadow Blade | `shadowBladeEnabled` (true); `shadowBladeChanceL1/L2/L3` (15/25/35%; 0–100, non-decreasing); `shadowBladeSlowDurationL1/L2/L3` (20/40/60; 0–6000, non-decreasing) |
| Magnet | `magnetEnabled` (true); `magnetRadius` (5; 1–20) |
| Auto Smelt | `autoSmeltEnabled` (true) |
| Vegetation | `vegetationEnabled` (true); `vegetationChanceL1/L2/L3` (30/60/100%; 0–100, non-decreasing) |
| Rebound | `reboundEnabled` (true); `reboundKnockbackL1/L2/L3` (5/10/20; 0–50, non-decreasing) |
| Feedback | `feedbackEnabled` (true); `feedbackHealAmount` (2.0; 0–20); `feedbackRepairAmount` (2; 0–50) |
| Second Wind | `secondWindEnabled` (true); `secondWindSpeedDuration` (5; 1–60); `secondWindCooldown` (60; 0–600) |
| Guardian's Grace | `guardiansGraceEnabled` (true); `guardiansGraceChanceL1/L2/L3` (10/20/30%; 0–100, non-decreasing) |
| Vulnerability | `vulnerabilityEnabled` (true); `vulnerabilityIgnoreL1/L2/L3` (10/20/30%; 0–100, non-decreasing) |
| Tether Master | `tetherMasterEnabled` (true) |
| Sky Rage | `skyRageEnabled` (true); `skyRageCooldownTicks` (0; 0–6000) |
| XP Syphon | `xpSyphonEnabled` (true) |
| Kinetic Discharge | `kineticDischargeEnabled` (true); `kineticDischargeMinSpeed` (1.2; 0.1–8.0); `kineticDischargeKnockbackL1/L2/L3` (1.5/2.5/3.5; 0–10, non-decreasing); `kineticDischargeDamageL3` (2.0; 0–20) |
| Sculk Bloom | `sculkBloomEnabled` (true) |

Malformed JSON is backed up when possible and replaced by defaults. Unknown
fields are not a gameplay contract; keep backups before manual edits. The
client configuration screen writes the same server-owned file only in the
appropriate local/server context; do not copy one player's file blindly to a
dedicated server.
