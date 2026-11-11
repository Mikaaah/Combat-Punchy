# Combat Punchy 1.2.1 — delivered hotfix

- Added support for Punchy 3.2 and Punchy! Moves 1.0.8 on NeoForge 1.21.1 and Forge 1.20.1.
- Kept Epic Fight in control of first-person hands throughout battle mode.
- Retained automatic combat switching, hit-triggered entry, the seven-second timeout, facial attachment and saved animation preferences.

The GitHub Java source snapshots remain at 1.2. These notes describe the separately supplied 1.2.1 builds.

# Combat Punchy 1.2

- Added support for Punchy! Moves 1.0.6, 1.0.6-hotfix and 1.0.7.
- Prevented Moves' new Spatial GUI camera handoff from restoring its shoulder-camera offset during Epic Fight combat.
- Preserved support for earlier Moves builds and Punchy 3.1b.
- Retained automatic combat switching, incoming-hit entry, timeout settings and facial attachment handling.

# Combat Punchy 1.1

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

The facial-rendering correction is included from the rebuilt 1.1 onward. The original report is [closed](https://github.com/Mikaaah/Combat-Punchy/issues/1).

# Combat Punchy 1.0

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

**Historical 1.0 issue:** Moves facial alignment was corrected in the rebuilt 1.1; the report is now closed.
