# Combat Punchy 1.1

- Added a dedicated Epic Fight attachment for Moves' facial layer, preventing the vanilla-head overlay from floating away from the combat head.
- Kept animated eyes, brows and facial effects on the same head transform as the player's skin during combat.
- Added `moves_facial_bridge`, enabled by default, with automatic restoration of the vanilla head/hat pose after drawing faces.
- Added support for **Punchy! Moves 1.0.4 and 1.0.5**, alongside 1.0.3.
- Removed the exact Moves 1.0.3 version restriction that prevented newer supported releases from loading.
- Connected Epic Fight combat to Moves' animation-intent API so flight transitions yield their base pose during combat.
- Retained automatic combat switching, the **7-second** default timeout, and the existing feature settings.

The facial-rendering correction is included in this rebuilt 1.1; visual confirmation in Minecraft is still pending.

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

**Known issue:** Punchy! Moves face features can render incorrectly during Epic Fight combat.
