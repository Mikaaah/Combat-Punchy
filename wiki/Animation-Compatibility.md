# Animation compatibility

## Current 1.2.1 build

| Situation | Animation owner |
| --- | --- |
| Normal first-person movement and interactions outside battle mode | Punchy, when installed and enabled |
| First-person hands throughout Epic Fight battle mode | Epic Fight, including between attacks |
| Body animation while Epic Fight owns the player model | Epic Fight; Moves yields its body animation |
| Camera while Epic Fight owns the player model | Epic Fight, with both Moves bridge settings enabled |
| Moves facial expressions during combat | Moves artwork attached to Epic Fight's animated head |

Automatic mode switching still enters on a supported attack or entity-attributed hit and exits on item selection or inactivity. Animation bridges retain saved Punchy and Moves preferences.

## Earlier 1.2 source

The 1.2 Java snapshot uses action-based first-person handoffs and a two-tick default release delay, configurable from zero to five ticks. Its native-stance option retains sheathed-katana and Ochs poses. That older timing should not be used to describe the supplied 1.2.1 battle-mode ownership.

## Facial attachment

The earlier [facial-alignment report](https://github.com/Mikaaah/Combat-Punchy/issues/1) is closed. Report new regressions with exact versions, perspective, skin and resource packs.


[Guide home](https://github.com/Mikaaah/Combat-Punchy/wiki) · [Report an issue](https://github.com/Mikaaah/Combat-Punchy/issues)
