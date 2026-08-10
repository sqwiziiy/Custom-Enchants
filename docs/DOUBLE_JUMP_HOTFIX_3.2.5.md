# Double Jump hotfix report — 3.2.5

Date: 2026-08-10

Affected versions: Minecraft 1.21.1 and 1.21.11.
Reference/unaffected version: Minecraft 1.20.1 (remains on 3.2.4).

## Summary

Double Jump on Minecraft 1.21.1 and 1.21.11 felt visibly jerky compared with the 1.20.1 implementation. The issue was not the configured jump strength (`0.42` vertical velocity and `0.2` sprint impulse); it was when and how the owner client received the movement impulse.

## Root cause

The previous 1.21.x path delayed the owner's complete Double Jump movement until the client received the server approval packet. During the client -> server -> client round trip, the owner continued along the old predicted trajectory. When the approval finally arrived, the client added the jump/sprint impulse late, producing the visible kick/stutter.

The old request also did not preserve the activation direction strongly enough for reconciliation: if player yaw changed while the request was in flight, a later server-side direction could differ from the direction at the input tick.

Minecraft 1.21.11 additionally differs from 1.21.1 in movement-dirty internals: the accepted implementation uses the valid `hurtMarked` synchronization path and does not depend on the older `hasImpulse` field.

## Fix design

- The owner client captures sprint state and yaw on the exact Double Jump input tick.
- The client immediately predicts the accepted movement locally using the same `0.42` vertical velocity and vanilla-style `0.2` sprint impulse.
- C2S carries the sprint/yaw activation snapshot.
- The server remains authoritative: it validates the enchantment, player state, replay/airtime restrictions and applies the equivalent authoritative impulse.
- The activation yaw snapshot is used for the server impulse instead of a later direction.
- S2C approval is acknowledgement-only for the owner and cannot add the same movement impulse a second time after RTT.
- Full stale server X/Z vectors are not used to overwrite newer owner prediction.

## Behavior preserved

One Double Jump per airtime, landing reset, water/swimming/lava/fall-flying/passenger/vehicle restrictions, creative/spectator restrictions, fall-distance reset, particles and boot durability behavior remain in place.

## Accepted hotfix commits

- Minecraft 1.21.1: `eaf5b3bde7e2f2f354d36f5478349988a8896140`
- Minecraft 1.21.11: `8be107e7a456a104394d14d2ef65fb21f9765913`

The accepted hotfix commits were based directly on the published 3.2.4 tags:

- `v3.2.4-mc1.21.1` (`03df681c85c02d657134810ca9da15e7cb3c109c`)
- `v3.2.4-mc1.21.11` (`411292c1c29366ff4d4f86613f9cd5abe729b421`)

Minecraft 1.20.1 was not changed.

## Validation and acceptance

Both target builds passed unit/contract tests, Gradle check/build, release-JAR verification, Fabric GameTests and dedicated-server smoke in the hotfix validation. The final Double Jump behavior was then manually accepted in-game on both Minecraft 1.21.1 and 1.21.11, including the previously noticeable movement smoothness regression.

## Release scope

3.2.5 is a targeted Double Jump movement hotfix for Minecraft 1.21.1 and 1.21.11 only. Minecraft 1.20.1 remains on 3.2.4 because its Double Jump behavior was already correct.
