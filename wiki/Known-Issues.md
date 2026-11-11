# Troubleshooting and resolved reports

The earlier [Punchy! Moves facial-alignment report](https://github.com/Mikaaah/Combat-Punchy/issues/1) is closed. The dedicated facial attachment is retained. No additional confirmed open Combat Punchy report was found during the 11 November 2026 review.

| Symptom | Check |
| --- | --- |
| Equipping a weapon does not enter combat | Attack with an eligible weapon, or receive an entity-attributed hit. |
| Mining does not enter combat | Expected with `enter_while_targeting_block = false`. |
| Mode does not switch | Check the master switch, feature toggles, weapon eligibility and the server's Epic Fight switching rule. |
| Combat never times out | Check `auto_exit_on_timeout`, a nonzero timeout and activity that keeps refreshing it. |
| Moves retains camera control | Enable both `moves_third_person_bridge` and `moves_camera_bridge`. |
| Punchy 3.2 or Moves 1.0.8 is rejected | Use the matching 1.2.1 build; the older 1.2 source has narrower version bounds. |

For a new rendering problem, include the exact JAR versions, perspective, skin/hat layers, resource packs and a short recording.

[Guide home](https://github.com/Mikaaah/Combat-Punchy/wiki) · [Report an issue](https://github.com/Mikaaah/Combat-Punchy/issues) · [Discord](https://discord.gg/CRYk3uDPVt)
