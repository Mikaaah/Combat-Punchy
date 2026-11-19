<p align="center"><img src="docs/branding/banner.png" alt="Combat Punchy" width="100%"></p>

<h1 align="center">Combat Punchy</h1>

<p align="center"><strong>Epic Fight combat. Punchy movement. Automatic switching that follows the action.</strong></p>

<p align="center"><a href="https://www.curseforge.com/minecraft/mc-mods/combat-punchy-epic-fight-x-punchy-x-punchy-moves">Downloads</a> · <a href="https://github.com/Mikaaah/Combat-Punchy/wiki">Player guide</a> · <a href="https://github.com/Mikaaah/Combat-Punchy/issues">Report an issue</a> · <a href="https://github.com/Mikaaah/Combat-Punchy/blob/port/forge-1.20.1/CHANGELOG.md">Changelog</a> · <a href="https://discord.gg/CRYk3uDPVt">Discord</a></p>

<p align="center"><img src="https://img.shields.io/badge/Minecraft-1.20.1-5b8731?style=flat-square" alt="Minecraft 1.20.1"> <img src="https://img.shields.io/badge/Forge-d77828?style=flat-square" alt="Forge"> <a href="https://mikaaah.github.io/TownsAndKingdoms/projects/"><img src="https://img.shields.io/badge/Towns_%26_Kingdoms-Modding_Hub-7f384a?style=flat-square" alt="Towns & Kingdoms Modding Hub"></a></p>


**Current build: 1.3.5.** Client-side Epic Fight, Punchy and Punchy! Moves integration, with optional common weapon defaults.

## Supported targets

| Loader | Minecraft | Java | Epic Fight | Punchy (optional) | Moves (optional) |
| --- | --- | --- | --- | --- | --- |
| NeoForge 21.1.262+ within 21.1 | 1.21.1 | 21 | 21.17.3.1 | 3.2a–3.2b; tested 3.2b | 1.0.9 or newer |
| Forge 47.4.13+ within 47 | 1.20.1 | 17 | 20.14.17 | 3.2a–3.2b; tested 3.2b | 1.0.9 or newer |

Use the JAR matching your loader. Punchy and Moves remain optional; older versions outside these ranges are rejected so their missing API hooks cannot silently fail. This source project has NeoForge and Forge implementations; it has no Fabric port.

## Automatic combat

Attack with an eligible registered Epic Fight melee weapon to enter battle mode on that input. Entity-attributed damage can also trigger combat. Selecting a non-weapon exits battle mode. The configurable inactivity timeout defaults to seven seconds (140 player ticks); attacks, combat actions, guarding and hits refresh it. Zero disables the timeout.

The first attack predicts **visual ownership**, not damage. Epic Fight changes local mode and sends its native mode packet before processing the original attack. Combat Punchy never consumes/replays that click, queues another attack, or bypasses stamina, animation restrictions or server rules. If basic and innate attacks share a binding, Epic Fight's native tap/hold discrimination still applies. Holding attack does not gain automatic repeated attacks.

## Weapon compatibility and mining

Combat eligibility follows the registered Epic Fight melee capability, including pack-assigned movesets on tools. Cataclysm's Infernal Forge, Void Forge and Brontes also remain eligible with the native pickaxe capability because they declare the standard sword tag. Ordinary pickaxes remain mining tools. Existing server/datapack weapon types are retained. If no capability is assigned to these three weapons, a reload-time fallback supplies Epic Fight's native animated pickaxe moveset. Explicit empty assignments are also retained.

For multiplayer, the server must supply the same capabilities. If it has no Cataclysm weapon datapack/addon, install Combat Punchy on the Epic Fight server as well to enable these three defaults; its rendering, input and configuration still run only on clients. Clients connecting to an unchanged server cannot create authoritative server weapon capabilities.

In combat, `combat_mining_axes_pickaxes_only` defaults to true: only axe/pickaxe classes or standard axe/pickaxe tags may break blocks. Other items keep the native combat attack path while looking at blocks, including Vanilla Backport's `minecraft:*_spear` weapons. Mining outside combat and the separate `enter_while_targeting_block` auto-entry setting retain their behavior. This is a client input restriction, not a server permission rule.

## Back weapons and offhand visibility

Your last eligible hotbar weapon can appear on your back while using tools/blocks. Eligible offhand weapons and shields stow outside battle mode. This is local cosmetic state: no items are moved or deleted. Axes, mining tools and fists/gloves keep their existing behavior.

Vanilla, Epic Fight and Punchy hand layers share the offhand suppression decision. The hand becomes visible immediately when entering combat or using/swinging the offhand. Moves' delayed item-swap visuals retain ownership until their handoff completes. State resets when the local player, world or patch changes.

Placement remains configurable: `back_weapon_offset`, `back_weapon_scale`, `back_weapon_rotation`, pitch/yaw, shield scale/rotation, and `back_item_overrides` (`item_id|scale|rotation|pitch|yaw`). Existing configuration is retained.

## API and rendering design

Stable Punchy v3 and Moves APIs are called through guarded, typed bridges. Optional mod absence is tested with a rejecting class loader. Bundled Expressions uses one-time class discovery at layer registration; there are no reflective API calls in render/tick loops.

The local client state is confined to Minecraft's client thread. Local patch lookups are shared per player tick. Hand classification and placement defaults are cached with owner/stack/config invalidation. Face poses and matrices are reused for ordinary rendering, with independent snapshots for nested renders and restoration in `finally`.

## Build and checks

Run `./gradlew build` with the Java version above. The build runs transition, timeout, handoff/prediction, offhand, back placement, face restoration, optional absence, native input, Cataclysm fallback precedence, combat-mining/tool-classification and upstream bytecode-contract checks. Source and packaged metadata versions come from `release.json`.

API contracts verify target descriptors and injection call counts; they do not replace graphical, multiplayer or full-modpack testing. See the diagnostic report supplied with the build for the actual validation performed.

[Release notes](CHANGELOG.md) · [Support](SUPPORT.md) · [Issues](https://github.com/Mikaaah/Combat-Punchy/issues)

## Moves dependency hotfix

The FML range is `[1.0.9,)` on both loaders: 1.0.9 is included and there is no maximum. The previous Forge range `[1.0.5,1.0.9)` incorrectly excluded 1.0.9. Newer Moves versions may load without a metadata ceiling; their APIs still need to remain compatible.
