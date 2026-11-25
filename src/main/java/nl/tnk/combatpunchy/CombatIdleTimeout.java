package nl.tnk.combatpunchy;

/** Uses player ticks, so pausing cannot silently consume inactivity or item-switch grace. */
public final class CombatIdleTimeout {
    private int lastActivity = Integer.MIN_VALUE;
    private boolean observedHeldItem;
    private boolean previousWeapon;
    private boolean itemSwitchPending;

    public void reset() {
        lastActivity = Integer.MIN_VALUE;
        observedHeldItem = false;
        previousWeapon = false;
        itemSwitchPending = false;
    }

    /** Covers the item-update callback both before and after the next controller refresh. */
    public boolean hasWeaponOrSwitchGrace() {
        return (observedHeldItem && previousWeapon) || itemSwitchPending;
    }

    public void activity(int tick) {
        lastActivity = tick;
    }

    /** Observe every input/tick refresh; only a real weapon-to-nonweapon selection arms grace. */
    public void updateHeldItem(int tick, boolean weapon, boolean inCombat,
                               boolean heldItemChanged, boolean autoExitOnItemSwitch) {
        if (!inCombat || weapon || !autoExitOnItemSwitch) itemSwitchPending = false;
        if (inCombat && autoExitOnItemSwitch && observedHeldItem && previousWeapon
                && !weapon && heldItemChanged) {
            itemSwitchPending = true;
            activity(tick);
        }
        observedHeldItem = true;
        previousWeapon = weapon;
    }

    public boolean expired(int tick, boolean inCombat, boolean active, int timeoutTicks) {
        return expired(tick, inCombat, active, timeoutTicks, true, false);
    }

    /** Both exit features use the configured interval; zero disables both without an instant exit. */
    public boolean expired(int tick, boolean inCombat, boolean active, int timeoutTicks,
                           boolean autoExitOnTimeout, boolean autoExitOnItemSwitch) {
        if (!inCombat || timeoutTicks <= 0) {
            lastActivity = Integer.MIN_VALUE;
            itemSwitchPending = false;
            return false;
        }
        if (!autoExitOnItemSwitch) itemSwitchPending = false;
        if (!autoExitOnTimeout && !itemSwitchPending) {
            lastActivity = Integer.MIN_VALUE;
            return false;
        }
        if (lastActivity == Integer.MIN_VALUE || tick < lastActivity || active) {
            activity(tick);
            return false;
        }
        return (long) tick - lastActivity >= timeoutTicks;
    }
}
