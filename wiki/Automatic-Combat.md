[← Guide home](https://github.com/Mikaaah/Combat-Punchy/wiki) · [Project](https://github.com/Mikaaah/Combat-Punchy) · [Get help](https://github.com/Mikaaah/Combat-Punchy/issues)

# Automatic combat

## Enter combat by attacking

With the default settings, equipping an eligible weapon keeps you in vanilla mode. Press attack against an entity or while swinging into air to enter Epic Fight combat on that attack input.

Selecting an item alone does not start combat. Normal block mining/chopping stays in vanilla mode unless `enter_while_targeting_block` is enabled.

## Enter combat when hit

Entity- or projectile-attributed damage can also trigger combat, including with a tool or empty hand. `auto_enter_on_hit` controls this separately from attack entry. Switching happens after the server reports damage.

## Enter combat with a skill

Press Epic Fight's Dodge or Guard binding with a supported skill equipped. Starting an actual shield block also works. A separately bound Mobility or Weapon Innate Skill action can enter combat directly.

Standard movement and interaction bindings retain priority. If Guard shares the Use button, aim away from a block or entity and use a compatible weapon. A dedicated Guard binding also works while aiming at an interaction target.

## Return to normal movement

Swapping from a melee weapon to a tool, non-melee item or empty hand starts a fresh seven-second grace period by default. Returning to a melee weapon clears the item-switch exit. Combat activity still refreshes the inactivity timer.

Each new world/player session starts in vanilla mode by default. These entry, item-switch and startup behaviors have separate configuration switches.

## Inactivity timer

The default timeout is **7 seconds**. Attacks, active combat animations/skills, guarding and receiving hits refresh the timer. After combat activity stops, the full interval must pass before automatic return to vanilla mode—even if you are still holding a weapon.

The timer follows player ticks, so pausing does not consume it. A new attack at the timeout boundary takes priority over expiry. Attack again after returning to vanilla mode to re-enter combat.

Set `auto_exit_on_timeout = false` to disable inactivity exit and `auto_exit_on_item_switch = false` to disable the separate item-switch exit. Setting `combat_timeout_seconds = 0` disables both timers.

## Server rules

Automatic mode changes respect Epic Fight's server rule for switching player modes. Combat Punchy does not override a server that disallows switching.

[Configuration](https://github.com/Mikaaah/Combat-Punchy/wiki/Configuration) · [Weapon support](https://github.com/Mikaaah/Combat-Punchy/wiki/Weapon-Support) · [Wiki home](https://github.com/Mikaaah/Combat-Punchy/wiki/Home)
