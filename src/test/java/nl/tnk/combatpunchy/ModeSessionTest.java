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

        session.reset();
        var hitDefaults = ModeSession.Features.DEFAULT;
        check(session.update(true, true, false, false, false, true, false, hitDefaults), ENTER_COMBAT,
                "an incoming hit enters combat with a tool or empty hand");
        check(session.update(true, true, false, true, false, false, false, hitDefaults), NONE,
                "holding the same non-weapon cannot immediately cancel hit entry");
        check(session.update(true, true, false, true, false, true, false, hitDefaults), NONE,
                "repeat hits retain combat without resending mode changes");
        check(session.update(true, true, false, true, false, false, true, hitDefaults), ENTER_VANILLA,
                "selecting another non-weapon still exits combat");
        check(session.update(true, true, false, false, false, false, false, hitDefaults), NONE,
                "a completed combat session cannot re-enter without another hit");
        check(session.update(true, true, true, false, false, true, false, hitDefaults), ENTER_COMBAT,
                "hits also enter while a weapon is equipped");
        check(session.update(true, true, false, true, false, false, true, hitDefaults), ENTER_VANILLA,
                "weapon-to-tool switching retains the original exit behavior");
        session.reset();
        check(session.update(true, true, false, true, false, true, true, hitDefaults), NONE,
                "a first-session hit wins over startup and item-exit rules");
        check(session.update(true, true, false, true, false, false, false, hitDefaults), NONE,
                "a startup hit remains active with the original held item");
        check(session.update(true, true, false, false, false, false, false, hitDefaults), NONE,
                "manual exit or timeout releases hit-triggered protection");
        check(session.update(true, true, false, true, false, false, false, hitDefaults), ENTER_VANILLA,
                "later manual combat cannot inherit protection from an earlier hit session");
        var noHitEntry = new ModeSession.Features(true, true, true, false);
        session.reset();
        check(session.update(true, true, true, false, false, true, false, noHitEntry), NONE,
                "hit activation can be disabled independently");
        check(session.update(true, true, true, false, true, false, false, noHitEntry), ENTER_COMBAT,
                "disabling hit activation preserves attack activation");
        var hitOnly = new ModeSession.Features(false, true, true, true);
        session.reset();
        check(session.update(true, true, false, false, false, true, false, hitOnly), ENTER_COMBAT,
                "disabling attack entry preserves hit activation");
        check(session.update(false, true, false, false, false, true, false, hitDefaults), NONE,
                "the master switch overrides incoming hits");
        check(session.update(true, false, false, false, false, true, false, hitDefaults), NONE,
                "server mode restrictions override incoming hits");
        check(session.update(true, true, false, true, false, false, false, hitDefaults), ENTER_VANILLA,
                "disabled or restricted sessions discard their hit state");
        session.reset();
        check(session.update(true, true, false, false, false, true, false, hitDefaults), ENTER_COMBAT,
                "a fresh hit can re-enter after a restriction");
        check(session.update(true, true, false, true, false, false, false, noHitEntry), ENTER_VANILLA,
                "disabling the hit feature releases non-weapon protection");
        checkHit(IncomingHit.isCombatHit(1, 1, 2, 2), true, "another entity hits the local player");
        checkHit(IncomingHit.isCombatHit(1, 1, 2, 3), true, "a projectile from another entity counts");
        checkHit(IncomingHit.isCombatHit(1, 1, -1, 3), true, "an ownerless projectile still counts");
        checkHit(IncomingHit.isCombatHit(1, 4, 2, 2), false, "damage to another player must not switch local mode");
        checkHit(IncomingHit.isCombatHit(1, 1, -1, -1), false, "fall/fire damage without an attacker is ignored");
        checkHit(IncomingHit.isCombatHit(1, 1, 1, 3), false, "the player's own projectile is ignored");
        checkHit(IncomingHit.isCombatHit(1, 1, -1, 1), false, "direct self damage is ignored");
        checkHit(IncomingHit.isCombatHit(0, 0, 2, -1), true, "entity ID zero is a valid local player");
        System.out.println("PASS: " + checks + " transition checks");
    }


    private static void check(ModeSession.Decision actual, ModeSession.Decision expected, String label) {
        checks++;
        if (actual != expected) throw new AssertionError(label + ": " + actual + " != " + expected);
    }

    private static void checkHit(boolean actual, boolean expected, String label) {
        checks++;
        if (actual != expected) throw new AssertionError(label);
    }
}
