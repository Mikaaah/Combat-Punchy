package nl.tnk.combatpunchy;

/** Combat mining is limited by the physical tool, not its Epic Fight moveset category. */
public final class CombatMiningPolicy {
    private CombatMiningPolicy() {}

    public static boolean mayMine(boolean restrictionEnabled, boolean inCombat,
                                  boolean axe, boolean pickaxe) {
        return !restrictionEnabled || !inCombat || axe || pickaxe;
    }
}
