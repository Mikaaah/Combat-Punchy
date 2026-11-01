package nl.tnk.combatpunchy;

public final class FirstPersonHandoffTest {
    private static int checks;

    public static void main(String[] args) {
        FirstPersonHandoff handoff = new FirstPersonHandoff();
        check(handoff.update(10, true, false, 2), false, "idle uses Punchy");
        check(handoff.update(11, true, true, 2), true, "attack immediately uses native hands");
        check(handoff.update(11, true, false, 2), true, "multiple callbacks agree during handoff");
        check(handoff.update(12, true, false, 2), true, "brief blend-out is preserved");
        check(handoff.update(13, true, false, 2), false, "handoff expires; no permanently hidden hands");
        check(handoff.update(14, true, true, 2), true, "guard reacquires native hands");
        check(handoff.update(14, false, true, 2), false, "selecting a tool bypasses any release delay");
        check(handoff.update(14, true, false, 2), false, "re-equipping has no stale suppression");
        check(handoff.update(15, true, true, 0), true, "zero-delay mode still renders an active attack");
        check(handoff.update(15, true, false, 0), false, "zero-delay mode releases immediately");
        check(handoff.update(16, true, true, 2), true, "another action works after zero-delay mode");
        handoff.reset();
        check(handoff.update(0, true, false, 2), false, "new player/world cannot inherit the old renderer");
        check(handoff.update(1, true, true, 2), true, "first attack in the new session works");
        check(handoff.update(100, true, false, 2), false, "large time jumps cannot latch the renderer");
        System.out.println("PASS: " + checks + " first-person handoff checks");
    }

    private static void check(boolean actual, boolean expected, String description) {
        checks++;
        if (actual != expected) throw new AssertionError(description);
    }
}
