# Known issues

## Punchy! Moves facial rendering

**Status: open · Combat Punchy 1.0**

Punchy! Moves face features can render incorrectly during Epic Fight combat. A fix and a reliable workaround are not confirmed.

[Track this issue](https://github.com/Mikaaah/Combat-Punchy/issues/1). If affected, add your versions, first-/third-person perspective, other player-rendering mods/shaders, and a screenshot or short clip.

## Behavior to check before reporting

| Symptom | Check |
| --- | --- |
| Equipping a weapon does not enter combat | Expected: attack with an eligible weapon to enter |
| Block mining does not enter combat | Expected with `enter_while_targeting_block = false` |
| Mode does not switch | Master/feature switches, weapon eligibility and the server’s Epic Fight switching rule |
| Combat never times out | `auto_exit_on_timeout`, a nonzero timeout and whether activity keeps refreshing it |
| Punchy handoff is absent | The Punchy bridge and Epic Fight’s animated first-person model setting |
| Moves retains camera control | Both `moves_third_person_bridge` and `moves_camera_bridge` must be enabled |

These checks describe settings and expected behavior; they are not confirmed fixes for the facial-rendering issue.

[Report a bug](https://github.com/Mikaaah/Combat-Punchy/issues/new?template=bug_report.yml) · [Configuration](Configuration.md) · [Wiki home](Home.md)
