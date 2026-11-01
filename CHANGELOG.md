# Combat Punchy 1.0

- Renamed TNK Combat Mode to **Combat Punchy**, including loader name, mod ID, JAR, source packages, Mixin config and client config filename.
- Changed the default combat inactivity timeout from 10 to **7 seconds**.
- Added independent feature switches for attack-triggered combat entry, utility/item-switch exit, initial vanilla mode and inactivity exit.
- Added a separate Moves camera/orbit/targeting handoff switch alongside the Moves body master switch.
- Kept the master switch, Punchy bridge switch, Moves bridge switch, native-stance switch, block-target entry option and release-delay/timeout settings.
- Existing `tnkcombatmode-client.toml` files are not imported. The new config is `combatpunchy-client.toml`; remove the earlier TNK Combat Mode JAR when upgrading.
- Added transition-policy regressions and running-client checks for disabling/re-enabling the main features.

# 0.2.1

- Added automatic return to vanilla after combat inactivity, including when the weapon is still selected.
- Default timeout: 10 seconds; configurable with `combat_timeout_seconds`, or 0 to disable.
- Attacks, active combat actions/skills, guarding/blocking and receiving hits refresh the countdown.
- Player-tick timing preserves the timeout while paused. Leaving combat, changing player/world, disabling the addon or losing permission to switch clears timer history.
- Expiry is checked after input processing, so a fresh attack at the deadline refreshes combat first.
- Added 20 timer checks and extended the real-client regression scenario to cover expiry, attack refresh, guard hold/release, resumed Punchy/Moves and repeated re-entry.

# 0.2.0

- Integrated standalone Punchy and Punchy! Moves handoffs. Epic Tweaks and Epic Punchy should be removed.
- Used Punchy's public transient-suppression API on 3.1, with an optional fallback for the inspected legacy 2.8g build.
- Filtered Epic Fight first-person rendering between action poses; preserved attacks, guard/blocking, hurt/dodge/action poses and native sheath/Ochs stances.
- Added a bounded release period and immediate clearing after player/weapon/slot changes or leaving combat.
- Connected Epic Fight to Moves' existing native-animation handoff, including pose-controller reset and stale-input discard.
- Suspended Moves shoulder/orbit camera, targeting replacement, avatar fade and body-yaw blending while Epic Fight owns the model. Preserved saved camera settings and blocked camera hotkeys from stealing combat perspective.
- Preserved the attack-to-enter / select-other-item-to-exit mode policy and automatic WOM melee-capability recognition.
- Explicitly excluded projectile weapons and shields as well as mining tools and fishing rods.
- Confirmed 30 unit/session checks, 41 live client assertions, and four optional-dependency startup cases. See VALIDATION.md for scope and limits.
