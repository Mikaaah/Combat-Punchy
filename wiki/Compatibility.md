# Compatibility

Combat Punchy 1.0 is a **client-side** addon for Minecraft 1.21.1 / NeoForge. The version constraints below are taken from its mod metadata.

| Component | Status | Declared version |
| --- | --- | --- |
| Minecraft | Required | 1.21.1 |
| NeoForge | Required | 21.1.262 or newer within 21.1.x |
| Epic Fight | Required | 21.17.3.1 exactly |
| Punchy | Optional first-person integration | 2.8g up to, excluding, 3.2 |
| Punchy! Moves (`punchy_moves`) | Optional body/camera integration | 1.0.3 exactly |
| Weapons of Miracles | Optional weapon support | Eligible registered Epic Fight melee capabilities |

Automatic mode switching is provided by Combat Punchy with Epic Fight. Punchy and Moves provide their respective integrations when present.

## Overlapping addons

Combat Punchy replaces the earlier **TNK Combat Mode** implementation. Its metadata marks **TNK Combat Mode** (`tnkcombatmode`), **Epic Tweaks** (`epictweaks`) and **Epic Punchy** (`efpunchycompat`) as incompatible because they overlap its behavior.

“Standalone” means Combat Punchy provides its own compatibility bridges; Epic Fight remains required.

## Scope

Epic Fight server rules still control whether the player may switch modes. The addon does not replace native movesets, rebalance weapons or promise compatibility with every player-model, camera or shader mod.

[Animation compatibility](Animation-Compatibility.md) · [Known issues](Known-Issues.md) · [Wiki home](Home.md)
