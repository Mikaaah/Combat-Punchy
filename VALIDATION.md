# Validation — Combat Punchy 1.0 — 2026-11-01

## Result

The delivered build compiled against actual Minecraft/NeoForge and Epic Fight artifacts, passed **64 unit/session checks**, passed **74 checks in an isolated running client**, and passed **four optional-dependency startup configurations**. The full gameplay client and all four startup clients exited with code 0.

## Running-client configuration

- Minecraft 1.21.1, OpenJDK 21.0.12, NeoForge 21.1.262.
- Epic Fight 21.17.3.1, WOM 2.0.178, Punchy 3.1, Punchy! Moves 1.0.3.
- Earlier TNK Combat Mode, Epic Tweaks, Epic Punchy and other compatibility bridges absent.
- Software OpenGL under a virtual display, with an integrated singleplayer server and a disposable flat creative world.
- One original, minimal Moves idle/crouch animation clip loaded through an ordinary test resource pack.

The fixture queues attack keys and invokes Epic Fight's real input handler with a deterministic air target. It updates the selected item and server inventory for weapon-switch sequences. This verifies transformed classes, real capability data, mode transitions and native input processing; it does not simulate a human physically pressing the mouse.

## Feature and configuration checks

The live client verified the loader name **Combat Punchy**, version **1.0** and the new **7-second default**. It exercised disabling and re-enabling automatic attack entry, disabling and restoring item-switch exit, disabling timeout past its deadline, restarting a fresh countdown after re-enabling, and the master switch stopping both mode changes and animation bridges.

It also verified that the Punchy bridge can be disabled independently, that the Moves camera bridge can be disabled while keeping body ownership active, that Moves camera control becomes available with its bridge disabled, and that restoring the bridge yields the camera again. Disabling the Moves master bridge disables its body and camera handoffs.

Unit transition sequences additionally verify independent startup-mode control, all mode features disabled for manual operation, startup reset with item exit disabled, and priority of the master switch and server restrictions.

The normal gameplay regression covers excluded tools/projectile weapons/shields, six WOM melee capabilities, first-click entry, Punchy suppression and idle restoration, a real Epic Fight guard skill, Moves native body handoff, saved camera preferences, camera hotkey isolation, WOM combat, repeated weapon-tool-weapon transitions, attack-based timeout refresh, expiry with the weapon still selected, Punchy/Moves restoration after expiry, repeated re-entry and guard hold/release.

The live timeout deadline checks use a 2-second configured interval after verifying the 7-second default. Exact timer boundaries, pausing, zero/disabled intervals and player/tick resets have dedicated unit checks.

## Optional dependency matrix — reverified with 1.0

| Loaded alongside Combat Punchy and Epic Fight | Result |
| --- | --- |
| No Punchy or Moves | Startup and forced relevant-class loading passed. |
| Punchy 3.1 only | Startup/API registration and relevant-class loading passed. |
| Moves 1.0.3 only | Startup and body/camera/target class loading passed. |
| Punchy 2.8g + Moves 1.0.3 | Startup and legacy enabled-getter fallback transformation passed. |

These are startup checks, not four additional gameplay playthroughs.

## Static/build checks

- Built with Java 21 against NeoForge 21.1.262's real development artifact, Epic Fight 21.17.3.1, Moves 1.0.3 and MixinExtras 0.5.5. No API stubs.
- Built with the included `scripts/build_from_classpath.py`; the Gradle dependency-download route is included but was not run here.
- Verified renamed metadata, mod ID, source packages, Mixin JSON/class references, Java 21 class-file versions and archive integrity.
- The included commented default TOML comes from the game's generated config and contains the 7-second default.
- Earlier TNK Combat Mode is declared incompatible, so an upgrade with both JARs cannot silently apply both integrations.
- No upstream mod JARs, test worlds, generated game assets or binary test fixtures are included. The Gradle wrapper JAR is included for source builds.

## Remaining limits

This is not a full TNK3 launch, manual gameplay review, shader compatibility pass or remote multiplayer test. Every WOM special skill, other camera/hand replacement mods, physical controllers, remote-player rendering and larger animation resource packs need pack-level testing. Missing sound/menu assets and unavailable audio in the isolated environment did not prevent gameplay checks.

Moves face-feature rendering during Epic Fight combat remains a known limitation. Keep Moves face features disabled for now; this release changes branding, defaults and feature configuration, not that rendering path.

The original verification fixture source and minimal test animation pack are supplied under `verification/` for review. They are not needed for normal installation.
