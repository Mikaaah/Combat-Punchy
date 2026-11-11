<p align="center"><img src="docs/branding/banner.png" alt="Combat Punchy" width="100%"></p>

<h1 align="center">Combat Punchy</h1>

<p align="center"><strong>Epic Fight combat. Punchy movement. Automatic switching that follows the action.</strong></p>

<p align="center"><a href="https://www.curseforge.com/minecraft/mc-mods/combat-punchy-epic-fight-x-punchy-x-punchy-moves">Downloads</a> · <a href="https://github.com/Mikaaah/Combat-Punchy/wiki">Player guide</a> · <a href="https://github.com/Mikaaah/Combat-Punchy/issues">Report an issue</a> · <a href="https://github.com/Mikaaah/Combat-Punchy/blob/main/CHANGELOG.md">Changelog</a> · <a href="https://discord.gg/CRYk3uDPVt">Discord</a></p>

<p align="center"><img src="https://img.shields.io/badge/Minecraft-1.21.1-5b8731?style=flat-square" alt="Minecraft 1.21.1"> <img src="https://img.shields.io/badge/NeoForge-d77828?style=flat-square" alt="NeoForge"> <a href="https://mikaaah.github.io/TownsAndKingdoms/projects/"><img src="https://img.shields.io/badge/Towns_%26_Kingdoms-Modding_Hub-7f384a?style=flat-square" alt="Towns & Kingdoms Modding Hub"></a></p>

**Current delivered build: 1.2.1.** NeoForge 1.21.1 and Forge 1.20.1 have separate JARs. The checked-in source snapshots still identify as 1.2; use the 1.2.1 build for Punchy 3.2 and Moves 1.0.8.

> **[Forge 1.20.1 edition guide](https://github.com/Mikaaah/Combat-Punchy/tree/port/forge-1.20.1)** — Combat Punchy is client-side. The NeoForge 1.21.1 edition uses Java 21; the Forge 1.20.1 port uses Java 17 and its own matching Epic Fight and animation-mod versions.

## Automatic combat mode

- Attack with an Epic Fight melee weapon to enter combat on the same attack input.
- Receive a hit from another entity or projectile to enter combat automatically, even with a tool or empty hand selected.
- Hit-triggered combat stays active with the current held item until timeout; selecting another non-weapon still exits combat.
- Switch to a tool, non-weapon item or empty hand to return to vanilla mode.
- Return to vanilla after **7 seconds** without combat activity, even while holding a weapon.
- Attack again to immediately re-enter combat.
- Attacks, active combat skills, guarding and receiving hits refresh the timer.
- Equipping a weapon alone keeps you in vanilla mode until you attack or receive a hit.

## Animation compatibility

- **Punchy** handles normal first-person movement and interactions outside battle mode.
- **Epic Fight** controls first-person hands throughout battle mode in 1.2.1, including between attacks.
- **Punchy! Moves** yields its body animation and camera controls during Epic Fight combat, then resumes when combat ends.
- The 1.2.1 build supports **Punchy! Moves 1.0.3–1.0.8**, including its updated movement and flight transitions.
- Attaches Moves' animated facial features to Epic Fight's head so eyes and brows follow the combat pose.
- Preserves native sheathed-katana and Ochs stances, with a short transition delay to reduce hand-animation flicker.
- Retains saved Punchy and Moves preferences.
- Standalone compatibility with **no Epic Tweaks requirement**.

## Weapon support

Recognises Epic Fight melee weapons and **Weapons of Miracles (WOM)** weapons through their registered weapon capabilities.

Pickaxes, shovels, hoes, fishing rods, ranged weapons and shields do not trigger automatic melee combat. Axes with melee support can trigger combat against entities or when swinging into air; normal block mining stays in vanilla by default.

## Configurable features

Separate switches control attack-triggered combat entry, hit-triggered combat entry, item-switch exit, inactivity exit, starting in vanilla mode, Punchy first-person compatibility, Moves third-person compatibility, Moves camera handling, facial alignment and native combat stances.

Also includes a master switch, an adjustable combat timeout, a first-person transition delay and optional combat entry while targeting blocks.

Hit-triggered switching uses the server's damage notification. It cannot change damage already calculated for the triggering hit, and it respects the server's Epic Fight mode-switching rule.

## Facial alignment

The earlier Moves facial-alignment report is [closed](https://github.com/Mikaaah/Combat-Punchy/issues/1). The dedicated head attachment remains enabled. Report any new rendering regression with your versions, skin, perspective and animation packs.

## Guides & support

[Automatic combat](https://github.com/Mikaaah/Combat-Punchy/wiki/Automatic-Combat) · [Weapon support](https://github.com/Mikaaah/Combat-Punchy/wiki/Weapon-Support) · [Configuration](https://github.com/Mikaaah/Combat-Punchy/wiki/Configuration) · [Compatibility](https://github.com/Mikaaah/Combat-Punchy/wiki/Compatibility) · [Known issues](https://github.com/Mikaaah/Combat-Punchy/wiki/Known-Issues)

Use the [bug report](https://github.com/Mikaaah/Combat-Punchy/issues/new?template=bug_report.yml) or [suggestion](https://github.com/Mikaaah/Combat-Punchy/issues/new?template=feature_request.yml) form for this mod. See [support guidance](SUPPORT.md) for the details that help identify an issue.

## Current compatibility

The supplied **1.2.1** update supports **Punchy 3.2** and **Punchy! Moves 1.0.8**. Epic Fight controls first-person hands throughout battle mode; automatic attack/hit entry, the seven-second timeout, item-switch exit and saved animation preferences remain.

The table below is taken from the supplied **1.2.1 NeoForge JAR**. The earlier 1.2 Java source on GitHub declares narrower optional-mod ranges.

## Supported versions


| Component | Supported version |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.262 or newer in the 21.1 series |
| Java | 21 |
| Epic Fight (required) | 21.17.3.1 |
| Punchy (optional) | 2.8g up to, excluding, 3.3 |
| Punchy! Moves (optional) | 1.0.3 up to, excluding, 1.0.9 |


[Read the release notes](CHANGELOG.md) for compatibility changes. Report animation problems with the installed skin, resource packs and mod versions.

## Pull up a chair

Questions, builds, feedback, server news and the occasional wildly overambitious factory: join the **Towns & Kingdoms Modding Hub**.

[![Join the Towns & Kingdoms Discord](https://i.imgur.com/Ojgcccm.png)](https://discord.gg/CRYk3uDPVt)

**[Join Discord](https://discord.gg/CRYk3uDPVt)** · [Explore all projects](https://mikaaah.github.io/TownsAndKingdoms/projects/) · [CurseForge projects](https://www.curseforge.com/members/mikaaah/projects)
