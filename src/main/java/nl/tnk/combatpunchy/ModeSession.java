package nl.tnk.combatpunchy;

/** The session policy, independent of Minecraft so transition sequences can be tested. */
public final class ModeSession {
    public enum Decision { NONE, ENTER_COMBAT, ENTER_VANILLA }

    public record Features(boolean autoEnterCombat, boolean autoExitOnItemSwitch,
                           boolean startInVanillaMode) {
        public static final Features DEFAULT = new Features(true, true, true);
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
        if (!enabled || !switchingAllowed) {
            reset();
            return Decision.NONE;
        }

        boolean firstUpdate = !initialized;
        initialized = true;
        if (!weapon) {
            return inCombat && (features.autoExitOnItemSwitch()
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
