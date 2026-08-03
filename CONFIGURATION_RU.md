# Конфигурация

Файл находится в `config/custom-enchants.json`. Он создаётся с defaults,
объединяется с текущей схемой, нормализуется и по возможности сохраняется
атомарно. Сейчас `configVersion` равен `1`. Boolean-поля включают и выключают
механику. Длительности и кулдауны указаны в тиках, если не сказано иначе.

| Группа | Поля (default; допустимый диапазон) |
|---|---|
| Glow Strike | `glowStrikeEnabled` (true); `glowStrikeDurationL1/L2/L3` (40/80/140; 0–6000, неубывающие) |
| Double Jump | `doubleJumpEnabled` (true) |
| Drill | `drillEnabled` (true) |
| Poison Blade | `poisonBladeEnabled` (true); `poisonBladeDurationL1/L2/L3` (40/60/80; 0–6000, неубывающие) |
| Lumberjack | `lumberjackEnabled` (true); `lumberjackMaxBlocksL1/L2/L3` (16/48/128; 1–256, неубывающие) |
| Shadow Blade | `shadowBladeEnabled` (true); `shadowBladeChanceL1/L2/L3` (15/25/35%; 0–100, неубывающие); `shadowBladeSlowDurationL1/L2/L3` (20/40/60; 0–6000, неубывающие) |
| Magnet | `magnetEnabled` (true); `magnetRadius` (5; 1–20) |
| Auto Smelt | `autoSmeltEnabled` (true) |
| Vegetation | `vegetationEnabled` (true); `vegetationChanceL1/L2/L3` (30/60/100%; 0–100, неубывающие) |
| Rebound | `reboundEnabled` (true); `reboundKnockbackL1/L2/L3` (5/10/20; 0–50, неубывающие) |
| Feedback | `feedbackEnabled` (true); `feedbackHealAmount` (2.0; 0–20); `feedbackRepairAmount` (2; 0–50) |
| Second Wind | `secondWindEnabled` (true); `secondWindSpeedDuration` (5; 1–60); `secondWindCooldown` (60; 0–600) |
| Guardian's Grace | `guardiansGraceEnabled` (true); `guardiansGraceChanceL1/L2/L3` (10/20/30%; 0–100, неубывающие) |
| Vulnerability | `vulnerabilityEnabled` (true); `vulnerabilityIgnoreL1/L2/L3` (10/20/30%; 0–100, неубывающие) |
| Tether Master | `tetherMasterEnabled` (true) |
| Sky Rage | `skyRageEnabled` (true); `skyRageCooldownTicks` (0; 0–6000) |
| XP Syphon | `xpSyphonEnabled` (true) |
| Kinetic Discharge | `kineticDischargeEnabled` (true); `kineticDischargeMinSpeed` (1.2; 0.1–8.0); `kineticDischargeKnockbackL1/L2/L3` (1.5/2.5/3.5; 0–10, неубывающие); `kineticDischargeDamageL3` (2.0; 0–20) |
| Sculk Bloom | `sculkBloomEnabled` (true) |

Повреждённый JSON по возможности резервируется и заменяется defaults. Не
копируйте конфигурацию клиента вслепую на dedicated server.
