# Automatic combat

## Enter combat by attacking

With the default settings, equipping an eligible weapon keeps you in vanilla mode. Press attack against an entity or while swinging into air to enter Epic Fight combat on that attack input.

Selecting an item alone does not start combat. Normal block mining/chopping stays in vanilla mode unless `enter_while_targeting_block` is enabled.

## Enter combat when hit

With `auto_enter_on_hit = true` (the default), a hit from another entity or projectile enters Epic Fight combat even with a tool, non-weapon or empty hand selected. This setting is independent of attack-triggered entry. Self-attributed hits and damage without a source entity, such as falling or fire, do not trigger it.

The current held item is allowed to remain selected during hit-triggered combat. The usual inactivity timer still applies; selecting another non-weapon exits combat when item-switch exit is enabled.

This client-side feature responds to the server's damage notification. The triggering hit has already been calculated, so its damage and armor mitigation cannot be changed retroactively by switching modes.

## Return to normal movement

Selecting a tool, a non-melee item or an empty hand returns you to vanilla mode by default. Switching between eligible melee weapons keeps combat mode active.

Each new world/player session starts in vanilla mode by default. These entry, item-switch and startup behaviors have separate configuration switches.

## Inactivity timer

The default timeout is **7 seconds**. Attacks, active combat animations/skills, guarding and receiving hits refresh the timer. After combat activity stops, the full interval must pass before automatic return to vanilla mode—even if you are still holding a weapon.

The timer follows player ticks, so pausing does not consume it. A new attack or qualifying incoming hit at the timeout boundary refreshes the timer. Attack again or receive another qualifying hit after returning to vanilla mode to re-enter combat.

Set `auto_exit_on_timeout = false` or `combat_timeout_seconds = 0` to disable inactivity exit.

## Server rules

Automatic mode changes respect Epic Fight's server rule for switching player modes. Combat Punchy does not override a server that disallows switching.

[Configuration](Configuration.md) · [Weapon support](Weapon-Support.md) · [Wiki home](Home.md)
