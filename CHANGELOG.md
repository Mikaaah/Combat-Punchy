# Combat Punchy 1.2

- Added support for Punchy! Moves 1.0.6, 1.0.6-hotfix and 1.0.7.
- Prevented Moves' new Spatial GUI camera handoff from restoring its shoulder-camera offset during Epic Fight combat.
- Preserved support for earlier Moves builds and Punchy 3.1b.
- Retained automatic combat switching, incoming-hit entry, timeout settings and facial attachment handling.

# Combat Punchy changelog

## 1.1 — Forge 1.20.1

- Ported Combat Punchy to Minecraft 1.20.1 and Forge 47.4.13+ using Java 17.
- Retained attack-triggered and incoming-hit combat entry, item-switch exit and configurable inactivity timeout.
- Adapted first-person handoffs to Epic Fight 20.14.17's Forge render events.
- Preserved Punchy 3.1b and Punchy! Moves 1.0.5 body, camera and facial compatibility.
- Adapted the animated-head facial attachment to Forge's patched-renderer registration event.
- Bundled MixinExtras and generated Forge production remapping metadata.

## 1.1 — NeoForge 1.21.1

- Added automatic combat entry when another entity or projectile hits you, including while holding a tool or using empty hands.
- Added `auto_enter_on_hit`, enabled by default and independent of attack-triggered entry.
- Kept hit-triggered combat active with the current held item until the inactivity timeout; selecting another non-weapon still exits combat.
- Added a dedicated Epic Fight attachment for Moves' facial layer, preventing the vanilla-head overlay from floating away from the combat head.
- Kept animated eyes, brows and facial effects on the same head transform as the player's skin during combat.
- Added `moves_facial_bridge`, enabled by default, with automatic restoration of the vanilla head/hat pose after drawing faces.
- Added support for **Punchy! Moves 1.0.4 and 1.0.5**, alongside 1.0.3.
- Removed the exact Moves 1.0.3 version restriction that prevented newer supported releases from loading.
- Connected Epic Fight combat to Moves' animation-intent API so flight transitions yield their base pose during combat.
- Retained automatic combat switching, the **7-second** default timeout, and the existing feature settings.

The facial-rendering correction is included in this rebuilt 1.1; visual confirmation in Minecraft is still pending.

## 1.0 — NeoForge 1.21.1

Initial public release.

- Added standalone compatibility between **Epic Fight**, **Punchy** and **Punchy! Moves**.
- Added automatic combat entry when attacking with a supported melee weapon.
- Added automatic return to vanilla when selecting a non-weapon item, tool or empty hand.
- Added a configurable combat inactivity timer, with a **7-second default**.
- Attacks, active combat skills, guarding and receiving hits refresh the combat timer.
- Added Punchy first-person animations between combat actions, with Epic Fight taking over for combat poses.
- Added automatic Moves body and camera handoffs during Epic Fight combat.
- Added support for registered **Weapons of Miracles (WOM)** melee weapons.
- Added weapon filtering to exclude mining tools, fishing rods, ranged weapons and shields from automatic melee activation.
- Preserved native sheathed-katana and Ochs stances, with smoother first-person transitions.
- Added individual feature toggles, a master switch, configurable timeout and transition timing, and optional combat entry while targeting blocks.

**Known issue:** Punchy! Moves face features can render incorrectly during Epic Fight combat.
