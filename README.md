![Combat Punchy — Epic Fight × Punchy × Punchy! Moves](docs/branding/banner.png)

# Combat Punchy

**Epic Fight combat. Punchy movement. Automatic switching.**

Combat Punchy brings **Epic Fight**, **Punchy** and **Punchy! Moves** together, switching between combat animations and normal movement as you play.

**Minecraft 1.20.1 · Forge · Client-side · Version 1.1**

[Wiki](https://github.com/Mikaaah/Combat-Punchy/wiki) · [Releases](https://github.com/Mikaaah/Combat-Punchy/releases) · [Report a bug](https://github.com/Mikaaah/Combat-Punchy/issues/new?template=bug_report.yml) · [Changelog](CHANGELOG.md)

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

- **Punchy** handles normal first-person movement, interactions and idle periods between attacks.
- **Epic Fight** takes over for attacks, guarding, dodging and other combat poses.
- **Punchy! Moves** yields its body animation and camera controls during Epic Fight combat, then resumes when combat ends.
- Supports **Punchy! Moves 1.0.5**, including its updated movement and flight transitions.
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

## Known issue

Version 1.1 includes a correction for displaced or duplicate Moves facial features during Epic Fight combat. A basic client test with the default skin showed aligned facial features during combat and hurt poses. Broader skin and animation-pack coverage is still pending. [Track the issue](https://github.com/Mikaaah/Combat-Punchy/issues/1).

## Guides & support

[Automatic combat](https://github.com/Mikaaah/Combat-Punchy/wiki/Automatic-Combat) · [Weapon support](https://github.com/Mikaaah/Combat-Punchy/wiki/Weapon-Support) · [Configuration](https://github.com/Mikaaah/Combat-Punchy/wiki/Configuration) · [Compatibility](https://github.com/Mikaaah/Combat-Punchy/wiki/Compatibility) · [Known issues](https://github.com/Mikaaah/Combat-Punchy/wiki/Known-Issues)

Use the [bug report](https://github.com/Mikaaah/Combat-Punchy/issues/new?template=bug_report.yml) or [suggestion](https://github.com/Mikaaah/Combat-Punchy/issues/new?template=feature_request.yml) form for this mod. See [support guidance](SUPPORT.md) for the details that help identify an issue.

## Forge 1.20.1 port

This branch targets Java 17 and Forge 47.4.13 or newer in the 47.x series. It uses the inspected stable Epic Fight 20.14.17 API; other Epic Fight versions are not declared compatible.

| Component | Supported version |
| --- | --- |
| Minecraft | 1.20.1 |
| Forge | 47.4.13–47.x |
| Java | 17 |
| Epic Fight (required) | 20.14.17 |
| Punchy (optional) | 3.1b |
| Punchy! Moves (optional) | 1.0.5 |

Install the Forge builds of the optional animation mods. Both remain optional: either bridge can be used independently, and automatic combat switching also works with Epic Fight alone. Do not combine Combat Punchy with TNK Combat Mode, Epic Tweaks or Epic Punchy.

Build with `./gradlew build` using JDK 17. The distributable includes MixinExtras and generated production mappings. Use `combat-punchy-1.1-forge-1.20.1.jar` from `build/libs/`.

Validation: the Java 17 Forge build and all 180 automated checks pass. On 4 November 2026, the exact release JAR was launched in a clean Forge 47.4.13 client with Epic Fight 20.14.17, Punchy 3.1b and Moves 1.0.5. It reached a single-player world, rendered first- and third-person combat, handled entity-attributed incoming damage and item switching, and saved/quit cleanly without Combat Punchy or mixin failures. Both optional API bridges registered successfully. This was an offline test with no audio device; Moves’ Fluid & Foolish animation resource pack was absent, so full resource-pack animation coverage remains unverified.
