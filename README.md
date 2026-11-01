![Combat Punchy — Epic Fight × Punchy × Punchy! Moves](docs/branding/banner.png)

# Combat Punchy

A client addon for **Minecraft 1.21.1 / NeoForge** that combines attack-triggered Epic Fight mode switching with Punchy first-person and Punchy! Moves third-person animation/camera handoffs.

**Current release: 1.0 · Default combat timeout: 7 seconds**

[Downloads](https://github.com/Mikaaah/Combat-Punchy/releases) · [Report a bug](https://github.com/Mikaaah/Combat-Punchy/issues) · [Changelog](CHANGELOG.md)

**Epic Tweaks and Epic Punchy are not required. Remove them for this setup.** The JAR contains its own integration code and uses Punchy's public API where available. It does not contain either compatibility mod's code or any third-party JARs or animations.

## Versions

| Component | Version used for verification |
| --- | --- |
| Minecraft / Java | 1.21.1 / Java 21 |
| NeoForge | 21.1.262 |
| Epic Fight — required | 21.17.3.1 |
| Punchy — optional | 3.1; legacy 2.8g also has a startup-tested fallback |
| Punchy! Moves — optional | 1.0.3 |
| Weapons of Miracles — optional | 2.0.178 |

The loader pins Epic Fight to the inspected version and Moves to 1.0.3. The allowed Punchy range is 2.8g through versions below 3.2; 2.8g and 3.1 were directly inspected. Use **Punchy 3.1** for the public API integration.

The addon works with Epic Fight alone, with Punchy, with Moves, or with both. WOM is recognised through Epic Fight's registered melee capabilities; no separate WOM dependency is added.

## Install / upgrade

1. Remove any earlier **TNK Combat Mode** or **Combat Punchy** JAR, **Epic Tweaks**, **Epic Punchy** (`efpunchycompat`) and any other Punchy/Epic Fight compatibility bridge that also controls rendering or mode switching.
2. Keep Epic Fight and whichever of Punchy, Punchy! Moves and WOM you want to use.
3. Download the JAR from Releases and put it in your Minecraft client's `mods` folder. If using the complete ZIP, the installable JAR is inside its `mods` folder.
4. Restart Minecraft. Keep your existing Punchy settings and animation packs. Moves still needs an enabled Moves animation resource pack to show body animations; this addon supplies compatibility, not animation assets.

Install this addon on the **client only**. Your server already needs Epic Fight and the normal gameplay mods. The addon uses Epic Fight's existing mode-change messages and adds no network protocol or server JAR requirement.

The generated client config is `config/combatpunchy-client.toml`. This is a new filename: the old `tnkcombatmode-client.toml` is no longer read. The new file starts with the defaults below, including the 7-second timeout. Reapply any custom settings you want to retain. A commented default config is included in the download; using it is optional because the mod generates one automatically.

## Combat mode

| Action | Behaviour |
| --- | --- |
| Join or equip an eligible melee weapon | Remain in vanilla mode until attacking. |
| Attack an entity or swing into air with that weapon | Enter combat before Epic Fight processes the same attack input. |
| Continue attacking or select another eligible weapon | Stay in combat until activity stops long enough for the timeout. |
| Combat inactivity timeout expires, even while holding the weapon | Return to vanilla; the next attack can enter combat again. |
| Select a utility item, block, ranged weapon, shield or empty hand | Return to vanilla mode. |
| Re-equip a weapon | Wait for the next attack before entering combat again. |
| Mine/chop a block while in vanilla | Stay vanilla by default. |

An eligible weapon must have a real registered Epic Fight `WeaponCapability`. Item names and damage stats are not used. Pickaxes, shovels, hoes and fishing rods are explicitly excluded. Projectile weapons, shields and Epic Fight ranged capabilities are excluded from melee activation. This includes the corresponding vanilla item subclasses and tool tags, so modded variants do not accidentally count just because a generic Epic Fight capability exists.

Axes with a melee capability count as weapons against entities or when swinging into air. Registered custom weapon categories and WOM melee weapons are accepted. This addon does not assign movesets to unsupported items.

The input integration respects Epic Fight's attack binding, including keyboard/mouse remapping. It allows the attack mapping to accept the first click while in vanilla, then lets Epic Fight run its normal attack handler. It never synthesizes a second attack or changes damage/cooldowns. Controller input still uses Epic Fight's normal attack handler, but a physical controller has not been tested.

`canSwitchPlayerMode` must be true in the world. Server restrictions and canceled Epic Fight mode transitions are respected.

## Combat inactivity timeout

The default is **7 seconds** without combat activity. Attacking refreshes the timer. Active combat animations, charging/holding a combat skill, guarding/blocking and receiving hits also refresh it, so an action is not interrupted by automatic expiry. The countdown starts from the end of that activity. Simply holding or reselecting a weapon does not keep the timer alive.

When it expires, the normal vanilla-mode transition runs and Punchy/Moves can resume through the existing handoff. A new attack with the same weapon enters combat again. The timer uses player ticks and does not advance while the player is paused. This is the addon's own client inactivity timer; it does not depend on Epic Fight's server-only last-action counter.

Set `auto_exit_on_timeout = false` or `combat_timeout_seconds = 0` to disable automatic inactivity exit. Allowed values are 0–300 seconds.

## Punchy first person

- Punchy handles normal hands, locomotion and interactions, including idle periods between attacks while in combat mode.
- Epic Fight takes over for attacks, guard/blocking, dodges and hurt/action poses that its entity state reports.
- A short release delay avoids flicker at the end of an action. Leaving combat or changing the selected weapon clears the handoff immediately.
- Native sheathed-katana and Ochs guard stances are preserved by default.
- Epic Fight item renderers that explicitly require vanilla first-person rendering remain respected.

Punchy 3.1 uses `PunchyApi.disable` with a temporary condition. The inspected Punchy 2.8g fallback temporarily filters its enabled getter while Epic Fight owns the hands. Neither route writes Punchy's saved configuration. No obsolete Epic Fight config-field shim is used.

## Punchy! Moves third person

The addon supplies Epic Fight ownership to Moves' own native-animation handoff. Moves can then reset its pose controller, discard stale action inputs and yield its body animation through its existing code. Ownership is checked per player, so other players' synchronized Epic Fight mode is considered too.

While Epic Fight renders the local player, Moves' shoulder camera, orbit control, targeting replacement and camera visibility/fade are suspended. With `moves_camera_bridge` enabled, Moves camera hotkeys cannot take the perspective away during combat. The user's saved camera preference is retained and the camera can resume when Epic Fight releases the model.

Outside combat, the vanilla player model is the default so Moves can animate it. Epic Fight's model-validation event is kept intact for other native animation integrations, including event-driven animation overrides.

The addon does not select a different camera perspective itself. Epic Fight's camera-auto-switch setting still controls whether entering combat changes between first/third person. Moves' camera resumes in an eligible third-person perspective.

## Config defaults

```toml
enabled = true
auto_enter_combat = true
auto_exit_on_item_switch = true
start_in_vanilla_mode = true
auto_exit_on_timeout = true
enter_while_targeting_block = false
punchy_first_person_bridge = true
moves_third_person_bridge = true
moves_camera_bridge = true
preserve_native_stances = true
first_person_release_ticks = 2
combat_timeout_seconds = 7
```

| Setting | Effect when false |
| --- | --- |
| `enabled` | Disable all automatic mode changes and all compatibility handoffs in this addon. |
| `auto_enter_combat` | Keep attacks in the selected mode; enter combat manually with Epic Fight's mode key. |
| `auto_exit_on_item_switch` | Keep combat active when selecting a tool, non-melee item or empty hand. |
| `start_in_vanilla_mode` | Keep the world's initial player mode when joining. Item exit and timeout settings still apply. |
| `auto_exit_on_timeout` | Keep combat active after inactivity. |
| `punchy_first_person_bridge` | Stop this addon's first-person ownership filtering and Punchy suppression. |
| `moves_third_person_bridge` | Stop all of this addon's Moves body, camera and targeting handoffs. |
| `moves_camera_bridge` | Keep Moves camera, orbit and targeting available while the Moves body bridge can still yield. Requires `moves_third_person_bridge`. |
| `preserve_native_stances` | Stop reserving idle sheath/Ochs stances for Epic Fight first-person rendering. Attacks and blocking still use Epic Fight poses. |

The switches are independent within these stated dependencies. For fully manual mode selection, disable `auto_enter_combat`, `auto_exit_on_item_switch`, `start_in_vanilla_mode` and `auto_exit_on_timeout`; the animation bridges can remain enabled. These settings only control this addon. Disabling a compatibility bridge leaves rendering to the installed mods and their own settings.

Edit the client TOML while Minecraft is closed, then restart. `enter_while_targeting_block` controls mode entry only; Epic Fight's ADAPTIVE targeting rules still decide whether a block should be mined. `first_person_release_ticks` accepts 0–5. The default is about 100 ms at 20 ticks per second.

**Known Moves limitation:** face-feature rendering during Epic Fight combat is not fixed in this release; keep Moves face features disabled for now.

## Verification and limits

See `VALIDATION.md` and `TEST_RESULTS.txt`. Verification includes a real isolated NeoForge client with Epic Fight, WOM, Punchy and Moves and **without Epic Tweaks or Epic Punchy**. Automated input exercises the native Epic Fight handler; the run is not a manual playthrough of the full TNK3 pack.

The complete TNK3 mod combination, every WOM special skill, remote multiplayer rendering, physical controllers, shader packs, and third-party hand/camera replacement mods still need pack-level testing. The test used a small original Moves animation pack; commercial/community animation packs are not redistributed or exhaustively verified.

## Source build

Use Java 21:

```sh
./gradlew build
```

On Windows: `gradlew.bat build`. The build downloads NeoForge, the exact Epic Fight dependency, and compile-only Moves/MixinExtras dependencies. The included source does not bundle these mods. `./gradlew runClient` is an Epic Fight development run; add the optional mods when testing their integrations.

The delivered binary was built against real resolved development artifacts using:

```sh
python scripts/build_from_classpath.py --java-home /path/to/java21 --classpath-file /path/to/classpath.txt --epic-fight-jar /path/to/epic-fight-21.17.3.1-mc1.21.1-neoforge.jar
```

The classpath file contains one resolved dependency JAR per line, including NeoForge/Minecraft, Moves 1.0.3 and MixinExtras 0.5.5. The direct build runs the 30 mode-session, 14 first-person handoff and 20 combat-timeout checks. The Gradle dependency-fetching build route has not been run in this environment.
