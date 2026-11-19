package nl.tnk.combatpunchy.back;

/** The hand stays visible until its lowering clip finishes; drawing releases it immediately. */
public final class OffhandStowState {
    public enum Change { NONE, STOW, DRAW }
    private Object owner, item;
    private boolean target, hidden;
    private int lastTick, finishAt;

    public void reset() {
        owner = item = null;
        target = hidden = false;
        lastTick = finishAt = 0;
    }

    public Change update(Object owner, Object item, int tick, boolean stow) {
        if (this.owner != owner || this.item != item || tick < lastTick) {
            this.owner = owner;
            this.item = item;
            target = hidden = stow;
            finishAt = tick;
            lastTick = tick;
            return Change.NONE;
        }
        lastTick = tick;
        if (stow == target) {
            if (target && tick >= finishAt) hidden = true;
            return Change.NONE;
        }
        target = stow;
        hidden = false;
        finishAt = tick;
        return stow ? Change.STOW : Change.DRAW;
    }

    public void lowerUntil(int tick) { finishAt = tick; }
    public void finishLowering() { if (target) hidden = true; }
    public boolean hidden(Object owner, Object item, boolean stow) {
        // Actions and item changes must release the item even before the next client tick.
        return stow && (this.owner != owner || this.item != item || hidden);
    }
}
