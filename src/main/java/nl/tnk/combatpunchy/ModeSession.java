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

    public void reset() {
        initialized = false;
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
        return update(enabled, switchingAllowed, weapon, inCombat, combatAttack, false, incomingHit,
                features.autoEnterCombat(), false, features.startInVanillaMode(), features.autoEnterOnHit());
    }

    /** Combat actions may enter with an empty hand; basic attack entry still requires a weapon. */
    public Decision update(boolean enabled, boolean switchingAllowed, boolean weapon,
                           boolean inCombat, boolean combatAttack, boolean combatAction,
                           boolean incomingHit, boolean autoEnterCombat, boolean autoEnterOnCombatAction,
                           boolean startInVanillaMode, boolean autoEnterOnHit) {
        if (!enabled || !switchingAllowed) {
            reset();
            return Decision.NONE;
        }

        boolean firstUpdate = !initialized;
        initialized = true;
        // Inputs and real hits win over the initial-mode policy in the same update.
        if ((incomingHit && autoEnterOnHit) || (combatAction && autoEnterOnCombatAction)
                || (weapon && combatAttack && autoEnterCombat)) {
            return inCombat ? Decision.NONE : Decision.ENTER_COMBAT;
        }
        // Item selection never changes mode synchronously. CombatIdleTimeout owns the
        // configurable switch grace, including when ordinary inactivity exit is disabled.
        if (firstUpdate && inCombat && startInVanillaMode) {
            return Decision.ENTER_VANILLA;
        }
        return Decision.NONE;
    }
}
