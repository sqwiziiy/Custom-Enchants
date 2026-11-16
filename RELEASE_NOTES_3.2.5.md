# Custom Enchants 3.2.5 — Minecraft 1.21.11

Release date: 2026-08-10

## Fixed

- Double Jump now applies its local movement prediction immediately on the activation input tick instead of waiting for the server approval round trip.
- The activation packet carries the sprint state and finite activation yaw snapshot so the authoritative server impulse uses the direction from the actual activation moment.
- The server approval is acknowledgement-only for owner movement and no longer adds the same horizontal/vertical impulse a second time after network latency.
- Minecraft 1.21.11 uses its valid movement synchronization path (`hurtMarked`) rather than relying on the older `hasImpulse` field used by earlier mappings.
- Server-side validation, one-jump-per-airtime enforcement, fall-distance reset, durability handling, particles, and invalid-state restrictions remain authoritative and unchanged in behavior.

## Root cause

On Minecraft 1.21.11, the previous implementation delayed the owner's complete Double Jump impulse until the S2C approval arrived. The client therefore continued along the pre-jump trajectory for part of the network round trip and then received a late movement kick. Direction could also diverge from the activation moment if yaw changed while the request was in flight.

## Validation

- Final movement behavior was manually accepted in-game.
- Unit/contract tests, Gradle check/build, release-JAR verification, Fabric GameTests, and dedicated-server smoke passed on the accepted hotfix build.
- Minecraft 1.20.1 is unaffected and remains on version 3.2.4.

No enchantment balance or configuration changes. Existing worlds and configurations remain compatible with 3.2.4.
