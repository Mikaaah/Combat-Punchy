package nl.tnk.combatpunchy;

public final class CombatMiningPolicyTest {
    private static int checks;

    public static void main(String[] args) {
        check(true, true, false, false, false, "swords and unclassified items cannot mine in battle mode");
        check(true, true, true, false, true, "axes can mine while in battle mode");
        check(true, true, false, true, true, "pickaxes can mine while in battle mode");
        check(true, true, true, true, true, "tools with both axe and pickaxe traits remain allowed");
        check(true, false, false, false, true, "ordinary item mining is unchanged outside battle mode");
        check(true, false, true, false, true, "normal axe mining is unchanged");
        check(true, false, false, true, true, "normal pickaxe mining is unchanged");
        check(false, true, false, false, true, "disabling the restriction restores native battle mining");
        check(false, false, false, false, true, "disabled restriction has no effect outside battle mode");
        check(true, true, false, false, false, "empty hands cannot mine in battle mode");
        check(true, false, false, false, true, "leaving combat immediately permits ordinary mining");
        check(true, true, false, false, false, "returning to combat immediately suppresses ordinary mining");
        check(true, true, false, true, true, "a physical pickaxe stays allowed with a heavy weapon moveset");
        System.out.println("PASS: " + checks + " combat mining policy checks");
    }

    private static void check(boolean enabled, boolean combat, boolean axe, boolean pickaxe,
                              boolean expected, String label) {
        checks++;
        if (CombatMiningPolicy.mayMine(enabled, combat, axe, pickaxe) != expected) throw new AssertionError(label);
    }
}
