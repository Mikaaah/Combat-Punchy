# 1.3.5 — Weapon compatibility and combat mining

- Respect registered Epic Fight melee movesets on Cataclysm weapons that inherit from pickaxes, including Infernal Forge, Void Forge and Brontes. Honor their sword tag with the native pickaxe moveset too; ordinary mining pickaxes stay excluded from automatic combat entry.
- Supply a native Epic Fight pickaxe/axe moveset for the three Cataclysm weapons only when no capability exists. Keep explicit empty, addon and user datapack assignments intact.
- Keep backported `minecraft:*_spear` weapons on Epic Fight's native spear animations during combat instead of falling back to vanilla stab handling when aiming at blocks.
- Block block-breaking in combat mode except with axes and pickaxes; keep ordinary mining outside combat.
- Preserve combat attacks through blocks/grass instead of letting mining preference consume them.
- Add the enabled-by-default `combat_mining_axes_pickaxes_only` client setting.
- Retain Punchy! Moves 1.0.9 or newer with no maximum version.

# 1.3.4 — Fix Punchy! Moves FML requirements

- Correct Forge metadata that rejected installed Punchy! Moves 1.0.9.
- Require Moves 1.0.9 or newer with no upper version bound on both loaders.
- Retain the exact 1.3.3 Java runtime classes; this is a dependency-metadata hotfix.

# 1.3.3 — Combat responsiveness and sheath ownership

- Suppress sheathed offhand items in Epic Fight's patched held-item renderer as well as vanilla and Punchy hands.
- Restore hand visibility immediately for combat, offhand use and delayed hand-swap transitions.
- Give the first eligible attack immediate client-side render ownership while forwarding its original native attack once.
- Cache local patch/classification lookups and render poses; clear session references on player/world replacement.
- Replace reflective optional API calls with typed Punchy/Moves bridges and explicit compatible minimum versions.
- Update tested Punchy pin to 3.2b; retain Moves 1.0.9 and the existing loader-specific Epic Fight versions.
- Replace NeoForge's deprecated config-list overload with its supported editable-list API.

Epic Fight's native animation/stamina restrictions, server rules and shared attack/special-key long-press detection remain authoritative.

# 1.3.2 — Punchy! Moves 1.0.9 compatibility

- Allow and verify Punchy! Moves 1.0.9 on both supported loaders.
- Preserve offhand stowing, equip animations, back placement and automatic combat switching from 1.3.1.

# 1.3.1 — Offhand stowing and equip animations

- Hide the complete first-person offhand, including skin, sleeves, armor and overlays, while its weapon is stowed.
- Play the animation pack’s offhand hand-out and hand-in clips when stowing and drawing without moving inventory items.
- Wait for the lowering animation before moving the offhand item to the back; immediately release it for combat and active use.
- Keep existing combat entry, inactivity timeout and weapon placement settings.

# 1.3.0 — Action-based hands and back weapons

- Refine default back weapons to 85% scale with a 6-degree inward handle tilt.
- Add global tilt/pitch/yaw and per-item size/rotation overrides for custom weapon models.
- Center shields upright on the back with independent shield size and rotation controls.

- Restore Punchy between combat actions while preserving Epic Fight attacks, guards and skills.
- Override only Punchy’s native Epic Fight ownership gate; keep its other integrations and armor fixes.
- Add local cosmetic back displays for eligible offhand and recently selected main-hand weapons, following Moves and Epic Fight torso animation.
- Bring the offhand item back during combat and active use; expose back offset/scale settings.
- Target Punchy 3.2a and Punchy! Moves 1.0.8 on both loaders.
- Respect Epic Fight weapon categories, excluding axes, mining tools and gloves/fists.
- Preserve automatic combat entry, item-switch exit and the configurable inactivity timeout.

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

The facial-rendering correction is included from the rebuilt 1.1 onward; the original issue is now closed.

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

**Historical 1.0 issue:** Moves facial alignment was corrected in the rebuilt 1.1; the original report is closed.
