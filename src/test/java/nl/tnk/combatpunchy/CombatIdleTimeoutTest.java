package nl.tnk.combatpunchy;

public final class CombatIdleTimeoutTest {
    private static int checks;

    public static void main(String[] args) {
        CombatIdleTimeout timer = new CombatIdleTimeout();
        check(timer.expired(0, true, false, 200), false, "new combat gets a full timeout");
        check(timer.expired(199, true, false, 200), false, "does not expire early");
        check(timer.expired(200, true, false, 200), true, "expires at the configured deadline");
        timer.activity(200);
        check(timer.expired(200, true, false, 200), false, "attack at deadline refreshes before expiry");
        check(timer.expired(399, true, false, 200), false, "refreshed deadline is respected");
        check(timer.expired(400, true, true, 200), false, "an active action cannot time out");
        check(timer.expired(599, true, false, 200), false, "full delay starts after active action");
        check(timer.expired(600, true, false, 200), true, "times out after the action ends");
        timer.activity(700);
        check(timer.expired(700, true, false, 200), false, "paused player tick does not advance timeout");
        check(timer.expired(700, true, false, 200), false, "repeated polling cannot consume timeout");
        check(timer.expired(800, true, true, 200), false, "guard or hurt refreshes activity");
        check(timer.expired(999, true, false, 200), false, "guard release gets the full interval");
        check(timer.expired(1000, true, false, 200), true, "expires after guard release");
        check(timer.expired(1100, false, false, 200), false, "vanilla resets timer");
        check(timer.expired(2000, true, false, 200), false, "new combat cannot inherit expired timer");
        check(timer.expired(2001, true, false, 0), false, "zero disables automatic expiry");
        check(timer.expired(9000, true, false, 0), false, "disabled timeout stays disabled");
        check(timer.expired(9000, true, false, 200), false, "re-enabling starts a fresh timer");
        check(timer.expired(5, true, false, 200), false, "tick reset cannot trigger immediate expiry");
        timer.reset();
        check(timer.expired(10000, true, false, 200), false, "player/world reset clears history");
        System.out.println("PASS: " + checks + " combat timeout checks");
    }

    private static void check(boolean actual, boolean expected, String description) {
        checks++;
        if (actual != expected) throw new AssertionError(description);
    }
}
