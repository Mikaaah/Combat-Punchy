package nl.tnk.combatpunchy;

/** The session policy, independent of Minecraft so transition sequences can be tested. */
public final class ModeSession {
    public enum Decision { NONE, ENTER_COMBAT, ENTER_VANILLA }

    public record Features(boolean autoEnterCombat, boolean autoExitOnItemSwitch,
                           boolean startInVanillaMode, boolean autoEnterOnHit) {
        public static final Features DEFAULT = new Features(true, true, true, true);

        public Features(boolean autoEnterCombat, boolean autoExitOnItemSwitch, boolean startInVanillaMode) {
            this(autoEnterCombat, autoExitOnItemSwitch, startInVanillaMode, false);
        }
    }

    private boolean initialized;
    private boolean enteredOnHit;

    public void reset() {
        initialized = false;
        enteredOnHit = false;
    }

    public Decision update(boolean enabled, boolean switchingAllowed, boolean weapon,
                           boolean inCombat, boolean combatAttack) {
        return update(enabled, switchingAllowed, weapon, inCombat, combatAttack, Features.DEFAULT);
    }

    public Decision update(boolean enabled, boolean switchingAllowed, boolean weapon,
                           boolean inCombat, boolean combatAttack, Features features) {
        return update(enabled, switchingAllowed, weapon, inCombat, combatAttack, false, false, features);
    }

    public Decision update(boolean enabled, boolean switchingAllowed, boolean weapon,
                           boolean inCombat, boolean combatAttack, boolean incomingHit,
                           boolean heldItemChanged, Features features) {
        if (!enabled || !switchingAllowed) {
            reset();
            return Decision.NONE;
        }

        boolean firstUpdate = !initialized;
        initialized = true;
        if (!inCombat || heldItemChanged || !features.autoEnterOnHit()) enteredOnHit = false;
        // A real hit wins over startup/item-exit rules in this update. Retain the
        // mode with the current tool/empty hand until timeout or another selection.
        if (incomingHit && features.autoEnterOnHit()) {
            enteredOnHit = true;
            return inCombat ? Decision.NONE : Decision.ENTER_COMBAT;
        }
        if (!weapon) {
            return inCombat && !enteredOnHit && (features.autoExitOnItemSwitch()
                    || (firstUpdate && features.startInVanillaMode())) ? Decision.ENTER_VANILLA : Decision.NONE;
        }
        if (combatAttack && features.autoEnterCombat()) {
            return inCombat ? Decision.NONE : Decision.ENTER_COMBAT;
        }
        // Even servers whose initialMode is combat start this interaction policy in vanilla.
        if (firstUpdate && inCombat && features.startInVanillaMode()) {
            return Decision.ENTER_VANILLA;
        }
        return Decision.NONE;
    }
}
