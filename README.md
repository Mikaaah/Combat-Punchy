![Combat Punchy — Epic Fight × Punchy × Punchy! Moves](docs/branding/banner.png)

# Combat Punchy

**Epic Fight combat. Punchy movement. Automatic switching.**

Combat Punchy brings **Epic Fight**, **Punchy** and **Punchy! Moves** together, switching between combat animations and normal movement as you play.

**Minecraft 1.21.1 · NeoForge · Client-side · Version 1.0**

[Downloads](https://github.com/Mikaaah/Combat-Punchy/releases) · [Report a bug](https://github.com/Mikaaah/Combat-Punchy/issues) · [Changelog](CHANGELOG.md)

## Automatic combat mode

- Attack with an Epic Fight melee weapon to enter combat on the same attack input.
- Switch to a tool, non-weapon item or empty hand to return to vanilla mode.
- Return to vanilla after **7 seconds** without combat activity, even while holding a weapon.
- Attack again to immediately re-enter combat.
- Attacks, active combat skills, guarding and receiving hits refresh the timer.
- Equipping a weapon alone keeps you in vanilla mode until you attack.

## Animation compatibility

- **Punchy** handles normal first-person movement, interactions and idle periods between attacks.
- **Epic Fight** takes over for attacks, guarding, dodging and other combat poses.
- **Punchy! Moves** yields its body animation and camera controls during Epic Fight combat, then resumes when combat ends.
- Preserves native sheathed-katana and Ochs stances, with a short transition delay to reduce hand-animation flicker.
- Retains saved Punchy and Moves preferences.
- Standalone compatibility with **no Epic Tweaks requirement**.

## Weapon support

Recognises Epic Fight melee weapons and **Weapons of Miracles (WOM)** weapons through their registered weapon capabilities.

Pickaxes, shovels, hoes, fishing rods, ranged weapons and shields do not trigger automatic melee combat. Axes with melee support can trigger combat against entities or when swinging into air; normal block mining stays in vanilla by default.

## Configurable features

Separate switches control attack-triggered combat entry, item-switch exit, inactivity exit, starting in vanilla mode, Punchy first-person compatibility, Moves third-person compatibility, Moves camera handling and native combat stances.

Also includes a master switch, an adjustable combat timeout, a first-person transition delay and optional combat entry while targeting blocks.

## Known issue

Punchy! Moves face features can render incorrectly during Epic Fight combat.
