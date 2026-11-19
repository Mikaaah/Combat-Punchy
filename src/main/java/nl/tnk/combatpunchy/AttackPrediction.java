package nl.tnk.combatpunchy;

/** A short visual ownership bridge, never a queued/replayed attack or a damage prediction. */
public final class AttackPrediction {
    private static final int MAX_TICKS = 4;
    private Object item;
    private int slot = -1;
    private int startedAt = Integer.MIN_VALUE;

    public void begin(int tick, Object item, int slot) {
        this.item = item;
        this.slot = slot;
        startedAt = tick;
    }

    public boolean active(int tick, boolean inCombat, Object item, int slot) {
        if (!inCombat || this.item != item || this.slot != slot || startedAt == Integer.MIN_VALUE
                || tick < startedAt || (long) tick - startedAt >= MAX_TICKS) {
            reset();
            return false;
        }
        return true;
    }

    public void reset() {
        item = null;
        slot = -1;
        startedAt = Integer.MIN_VALUE;
    }
}
