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
        timer.reset();
        timer.updateHeldItem(0, true, true, true, true);
        timer.activity(0);
        check(timer.expired(130, true, false, 140, true, true), false, "initial combat interval is still active");
        timer.updateHeldItem(130, false, true, true, true);
        check(timer.expired(130, true, false, 140, true, true), false, "switching away from a weapon cannot exit immediately");
        check(timer.expired(140, true, false, 140, true, true), false, "the old combat deadline cannot eat switch grace");
        timer.updateHeldItem(200, false, true, true, true);
        check(timer.expired(269, true, false, 140, true, true), false, "switch grace lasts the full seven seconds");
        check(timer.expired(270, true, false, 140, true, true), true, "tool-to-tool changes do not restart the weapon-switch grace");

        timer.reset();
        timer.updateHeldItem(0, true, true, true, true);
        check(timer.expired(1000, true, false, 140, false, true), false, "disabling general timeout preserves combat while holding a weapon");
        timer.updateHeldItem(1000, false, true, true, true);
        check(timer.expired(1000, true, false, 140, false, true), false, "item-switch exit has its own delayed trigger");
        check(timer.expired(1139, true, false, 140, false, true), false, "disabled general timeout cannot shorten dedicated grace");
        check(timer.expired(1140, true, false, 140, false, true), true, "item-switch exit works with general timeout disabled");
        timer.updateHeldItem(1100, true, true, true, true);
        check(timer.expired(2000, true, false, 140, false, true), false, "re-equipping a weapon cancels only the pending item-switch exit");
        timer.updateHeldItem(2000, false, true, true, true);
        check(timer.expired(2139, true, false, 140, false, true), false, "switching off a newly re-equipped weapon starts a new grace");
        check(timer.expired(2140, true, false, 140, false, true), true, "restarted grace expires at its own deadline");

        timer.reset();
        timer.updateHeldItem(0, true, true, true, false);
        timer.activity(0);
        timer.updateHeldItem(130, false, true, true, false);
        check(timer.expired(140, true, false, 140, true, false), true, "disabling item-switch exit leaves ordinary inactivity expiry unchanged");
        check(timer.expired(1000, true, false, 140, false, false), false, "both exit features disabled preserve combat");
        timer.updateHeldItem(1000, false, true, true, true);
        check(timer.expired(2000, true, false, 140, false, true), false, "tool-to-tool changes cannot arm a newly enabled switch exit");

        timer.reset();
        timer.updateHeldItem(0, false, false, true, true);
        timer.updateHeldItem(1, false, true, false, true);
        check(timer.expired(10000, true, false, 140, false, true), false, "manual combat with a tool does not create a switch deadline");
        timer.updateHeldItem(10001, false, true, true, true);
        check(timer.expired(20000, true, false, 140, false, true), false, "changing tools in manually entered combat does not create a switch deadline");

        timer.reset();
        timer.updateHeldItem(0, true, true, true, true);
        timer.updateHeldItem(1, false, true, true, true);
        timer.activity(100);
        check(timer.expired(239, true, false, 140, false, true), false, "incoming combat activity refreshes pending switch grace");
        check(timer.expired(240, true, true, 140, false, true), false, "an active skill or guard cannot be cut off at the deadline");
        check(timer.expired(379, true, false, 140, false, true), false, "active skill release gets the normal delay");
        check(timer.expired(380, true, false, 140, false, true), true, "switch grace expires after combat activity ends");

        timer.reset();
        timer.updateHeldItem(0, true, true, true, true);
        timer.updateHeldItem(1, false, true, true, true);
        check(timer.expired(1, true, false, 0, false, true), false, "zero interval disables item-switch exit without an instant revert");
        check(timer.expired(10000, true, false, 0, true, true), false, "zero interval also disables general timeout");
        check(timer.expired(10000, true, false, 140, false, true), false, "re-enabling an interval cannot resurrect a switch that happened while timing was disabled");

        timer.reset();
        timer.updateHeldItem(0, true, true, true, true);
        timer.updateHeldItem(1, false, true, true, true);
        timer.updateHeldItem(2, false, false, false, true);
        check(timer.expired(2, false, false, 140, false, true), false, "manual exit clears pending switch grace");
        timer.updateHeldItem(3, false, true, false, true);
        check(timer.expired(10000, true, false, 140, false, true), false, "manual re-entry cannot inherit an earlier pending switch");

        timer.reset();
        timer.updateHeldItem(0, true, true, true, true);
        timer.updateHeldItem(1, false, true, false, true);
        check(timer.expired(10000, true, false, 140, false, true), false, "capability reclassification without a selection change does not arm switch exit");
        timer.reset();
        timer.updateHeldItem(100, false, true, true, true);
        check(timer.expired(10000, true, false, 140, false, true), false, "a new player session cannot inherit the prior player's selected weapon");

        timer.reset();
        timer.updateHeldItem(0, true, true, true, true);
        timer.updateHeldItem(10, false, true, true, true);
        check(timer.expired(79, true, false, 70, false, true), false, "custom configured intervals apply to switch grace");
        check(timer.expired(80, true, false, 70, false, true), true, "custom switch deadline is exact");
        check(timer.expired(5, true, false, 70, false, true), false, "tick rewind restarts a full switch interval");
        check(timer.expired(74, true, false, 70, false, true), false, "rewound switch interval cannot expire early");
        check(timer.expired(75, true, false, 70, false, true), true, "rewound switch interval still expires normally");

        timer.reset();
        check(timer.hasWeaponOrSwitchGrace(), false, "fresh sessions cannot intercept native preference exit");
        timer.updateHeldItem(0, false, true, true, true);
        check(timer.hasWeaponOrSwitchGrace(), false, "manual combat with a tool retains native preference fallback");
        timer.updateHeldItem(1, true, true, true, true);
        check(timer.hasWeaponOrSwitchGrace(), true, "native item callback sees the last observed weapon before controller refresh");
        timer.updateHeldItem(2, false, true, true, true);
        check(timer.hasWeaponOrSwitchGrace(), true, "native item callback sees pending switch grace after controller refresh");
        timer.updateHeldItem(3, false, true, true, true);
        check(timer.hasWeaponOrSwitchGrace(), true, "a later tool capability update cannot undercut the pending grace");
        timer.expired(3, false, false, 140, false, true);
        check(timer.hasWeaponOrSwitchGrace(), false, "manual exit clears native preference interception");
        timer.reset();
        check(timer.hasWeaponOrSwitchGrace(), false, "disconnect clears native preference interception");
        System.out.println("PASS: " + checks + " combat timeout and item-switch grace checks");
    }

    private static void check(boolean actual, boolean expected, String description) {
        checks++;
        if (actual != expected) throw new AssertionError(description);
    }
}
