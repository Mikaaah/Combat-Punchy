# Configuration

Settings are stored in `config/combatpunchy-client.toml`. They are client preferences. Restart the game after editing the file.

## Feature switches

| Setting | Default | Effect |
| --- | --- | --- |
| `enabled` | `true` | Master switch for automatic mode changes and compatibility handoffs |
| `auto_enter_combat` | `true` | Attack with an eligible melee weapon to enter combat |
| `auto_enter_on_hit` | `true` | Enter when another entity hits you, including projectiles and while holding non-weapons |
| `auto_exit_on_item_switch` | `true` | Return to vanilla when selecting a non-melee item or empty hand |
| `start_in_vanilla_mode` | `true` | Start each world/player session in vanilla mode |
| `auto_exit_on_timeout` | `true` | Return to vanilla after the configured inactivity interval |
| `enter_while_targeting_block` | `false` | Also allow block-targeted attack inputs to start combat |
| `punchy_first_person_bridge` | `true` | Hand off between Punchy normal hands and Epic Fight combat poses |
| `moves_third_person_bridge` | `true` | Let Moves yield body animation to Epic Fight |
| `moves_camera_bridge` | `true` | Let Moves yield camera/orbit/targeting; requires the third-person bridge |
| `moves_facial_bridge` | `true` | Attach animated faces to Epic Fight's head; requires the third-person bridge |
| `preserve_native_stances` | `true` | Retain sheathed-katana and Ochs guard poses in first person |

## Timing

| Setting | Default | Range | Meaning |
| --- | --- | --- | --- |
| `combat_timeout_seconds` | `7` | 0–300 seconds | Inactivity interval; 0 disables timeout exit |
| `first_person_release_ticks` | `2` | 0–5 ticks | Short first-person renderer hold after a combat pose |

Attacks, active combat animations/skills, guarding and receiving hits refresh the inactivity timer. It follows player ticks.

## Common preferences

- **Keep combat active while holding a weapon:** set `auto_exit_on_timeout = false`.
- **Keep manual mode when changing items:** set `auto_exit_on_item_switch = false`.
- **Allow combat while aiming at blocks:** set `enter_while_targeting_block = true`.
- **Keep Moves camera handling:** set `moves_camera_bridge = false`.
- **Disable the addon’s behavior:** set `enabled = false`.

The timing switches and integration switches are independent; the master switch overrides all of them. Changes do not bypass Epic Fight's server mode-switching rule.

[Default config](https://github.com/Mikaaah/Combat-Punchy/blob/main/config/combatpunchy-client.toml) · [Wiki home](Home.md)
