package nl.tnk.combatpunchy;

import static nl.tnk.combatpunchy.ModeSession.Decision.*;

public final class ModeSessionTest {
    private static int checks;

    public static void main(String[] args) {
        ModeSession session = new ModeSession();
        check(session.update(true, true, true, false, false), NONE, "equip weapon stays vanilla");
        check(session.update(true, true, true, false, true), ENTER_COMBAT, "first attack enters combat");
        check(session.update(true, true, true, true, true), NONE, "successive attacks do not resend mode");
        check(session.update(true, true, true, true, false), NONE, "another weapon keeps combat");
        check(session.update(true, true, false, true, false), ENTER_VANILLA, "selecting a tool exits immediately");
        check(session.update(true, true, false, false, true), NONE, "tool attack stays vanilla");
        check(session.update(true, true, true, false, false), NONE, "re-equipping does not auto-enter");
        check(session.update(true, true, true, false, true), ENTER_COMBAT, "attack after re-equipping re-enters");
        check(session.update(true, true, false, true, true), ENTER_VANILLA, "tool click cleans stale combat state");
        session.reset();
        check(session.update(true, true, true, true, false), ENTER_VANILLA, "new session starts vanilla even if server starts combat");
        check(session.update(true, true, true, false, true), ENTER_COMBAT, "first attack after reconnect works");
        check(session.update(true, false, false, true, false), NONE, "server restriction is respected");
        check(session.update(false, true, false, true, true), NONE, "disabled addon leaves mode alone");
        session.reset();
        check(session.update(true, true, true, false, true), ENTER_COMBAT, "attack can initialize a new session");
        check(session.update(true, true, false, true, false), ENTER_VANILLA, "empty hand exits");
        check(session.update(true, true, false, false, false), NONE, "idle tools cause no mode packet spam");
        var noEntry = new ModeSession.Features(false, true, true);
        check(session.update(true, true, true, false, true, noEntry), NONE, "attack activation can be disabled");
        check(session.update(true, true, false, true, false, noEntry), ENTER_VANILLA, "disabling activation keeps item exit enabled");
        var noItemExit = new ModeSession.Features(true, false, true);
        check(session.update(true, true, false, true, false, noItemExit), NONE, "item exit can be disabled");
        check(session.update(true, true, false, true, true, noItemExit), NONE, "disabled item exit ignores utility attacks");
        check(session.update(true, true, true, false, true, noItemExit), ENTER_COMBAT, "disabled item exit keeps attack entry enabled");
        var noStartReset = new ModeSession.Features(true, true, false);
        session.reset();
        check(session.update(true, true, true, true, false, noStartReset), NONE, "startup vanilla reset can be disabled");
        check(session.update(true, true, false, true, false, noStartReset), ENTER_VANILLA, "disabled startup reset keeps normal item exit");
        var manual = new ModeSession.Features(false, false, false);
        session.reset();
        check(session.update(true, true, true, true, false, manual), NONE, "all mode features off preserves server initial combat");
        check(session.update(true, true, false, true, true, manual), NONE, "all mode features off preserves manual combat for tools");
        check(session.update(true, true, true, false, true, manual), NONE, "all mode features off preserves manual vanilla");
        session.reset();
        check(session.update(true, true, false, true, false, noItemExit), ENTER_VANILLA, "startup reset remains independent of item exit");
        check(session.update(false, true, true, false, true, ModeSession.Features.DEFAULT), NONE, "master switch overrides individual features");
        check(session.update(true, false, true, false, true, ModeSession.Features.DEFAULT), NONE, "server restriction overrides individual features");
        check(session.update(true, true, true, false, true, ModeSession.Features.DEFAULT), ENTER_COMBAT, "restored features work after a restriction");
        System.out.println("PASS: " + checks + " transition checks");
    }


    private static void check(ModeSession.Decision actual, ModeSession.Decision expected, String label) {
        checks++;
        if (actual != expected) throw new AssertionError(label + ": " + actual + " != " + expected);
    }
}
