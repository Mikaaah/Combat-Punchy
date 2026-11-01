package nl.tnk.combatpunchy;

/** Uses player ticks, so pausing cannot silently consume the inactivity timeout. */
public final class CombatIdleTimeout {
    private int lastActivity = Integer.MIN_VALUE;

    public void reset() {
        lastActivity = Integer.MIN_VALUE;
    }

    public void activity(int tick) {
        lastActivity = tick;
    }

    public boolean expired(int tick, boolean inCombat, boolean active, int timeoutTicks) {
        if (!inCombat || timeoutTicks <= 0) {
            reset();
            return false;
        }
        if (lastActivity == Integer.MIN_VALUE || tick < lastActivity || active) {
            activity(tick);
            return false;
        }
        return (long) tick - lastActivity >= timeoutTicks;
    }
}
