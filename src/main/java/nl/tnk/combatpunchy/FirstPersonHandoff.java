package nl.tnk.combatpunchy;

/** A bounded renderer handoff. Losing combat ownership clears it immediately. */
public final class FirstPersonHandoff {
    private int holdUntil = Integer.MIN_VALUE;

    public boolean update(int tick, boolean allowed, boolean combatPose, int releaseTicks) {
        if (!allowed) {
            reset();
            return false;
        }
        if (combatPose) {
            holdUntil = tick + releaseTicks;
            return true;
        }
        return tick < holdUntil;
    }

    public void reset() {
        holdUntil = Integer.MIN_VALUE;
    }
}
