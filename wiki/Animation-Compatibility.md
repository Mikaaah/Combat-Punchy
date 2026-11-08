[← Guide home](https://github.com/Mikaaah/Combat-Punchy/wiki) · [Project](https://github.com/Mikaaah/Combat-Punchy) · [Get help](https://github.com/Mikaaah/Combat-Punchy/issues)

# Animation compatibility

| Situation | Animation owner |
| --- | --- |
| Normal first-person movement, interactions and idle intervals between combat actions | Punchy, when present and its bridge is enabled |
| Attacks, guarding and other native combat poses | Epic Fight |
| Body animation while Epic Fight owns the player model | Epic Fight; Punchy! Moves yields its locomotion/body rotation |
| Camera, orbit and targeting while Epic Fight owns the model | Epic Fight when both Moves bridge options are enabled |

## First-person handoff

The default release delay is **2 ticks**. It briefly retains Epic Fight's first-person renderer after an action to reduce flicker, then lets Punchy resume. The range is 0–5 ticks.

Changing the selected item/slot or player session clears stale handoff state. Items that explicitly require vanilla first-person rendering retain that behavior.

Epic Fight's animated first-person model must be enabled for its first-person combat rendering to take ownership.

## Native stances

`preserve_native_stances` keeps Epic Fight's sheathed-katana and Ochs guard poses in first person. Disable it if you prefer the normal handoff behavior outside active actions.

## Saved preferences

The bridges temporarily hand off rendering ownership. Combat Punchy does not rewrite saved Punchy or Moves preferences.

## Known limitation

Punchy! Moves facial features can render incorrectly during Epic Fight combat. See [known issues](https://github.com/Mikaaah/Combat-Punchy/wiki/Known-Issues).

[Configuration](https://github.com/Mikaaah/Combat-Punchy/wiki/Configuration) · [Wiki home](https://github.com/Mikaaah/Combat-Punchy/wiki/Home)
