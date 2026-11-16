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
        AttackPrediction prediction = new AttackPrediction();
        Object sword = new Object(), otherSword = new Object();
        check(prediction.active(10, true, sword, 0), false, "idle battle mode does not predict attacks");
        prediction.begin(10, sword, 0);
        check(prediction.active(10, true, sword, 0), true, "first input owns native hands in the same tick");
        check(prediction.active(11, true, sword, 0), true, "initial state wait retains native ownership");
        check(prediction.active(13, true, sword, 0), true, "bounded prediction covers the first four ticks");
        check(prediction.active(14, true, sword, 0), false, "canceled/rejected attacks cannot latch ownership");
        prediction.begin(20, sword, 0);
        check(prediction.active(20, false, sword, 0), false, "manual mode exit releases prediction immediately");
        check(prediction.active(20, true, sword, 0), false, "manual re-entry cannot inherit an old attack");
        prediction.begin(30, sword, 0);
        check(prediction.active(30, true, otherSword, 0), false, "item replacement releases prediction in the same tick");
        prediction.begin(40, sword, 0);
        check(prediction.active(40, true, sword, 1), false, "slot replacement clears prediction even for the same item");
        prediction.begin(50, sword, 0);
        check(prediction.active(1, true, sword, 0), false, "player tick rewind cannot retain ownership");
        prediction.begin(60, sword, 0);
        prediction.reset();
        check(prediction.active(60, true, sword, 0), false, "disconnect/world reset clears prediction");
        prediction.begin(70, sword, 0);
        prediction.begin(73, sword, 0);
        check(prediction.active(76, true, sword, 0), true, "new input refreshes its own short bridge");
        check(prediction.active(77, true, sword, 0), false, "refreshed prediction has a bounded end");
        System.out.println("PASS: " + checks + " first-person handoff/prediction checks");
    }

    private static void check(boolean actual, boolean expected, String description) {
        checks++;
        if (actual != expected) throw new AssertionError(description);
    }
}
