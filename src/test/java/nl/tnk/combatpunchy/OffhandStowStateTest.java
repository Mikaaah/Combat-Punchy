package nl.tnk.combatpunchy;

import nl.tnk.combatpunchy.back.OffhandStowState;
import nl.tnk.combatpunchy.back.OffhandStowState.Change;

public final class OffhandStowStateTest {
    private static int checks;
    public static void main(String[] args) {
        var state = new OffhandStowState();
        Object player = new Object(), sword = new Object(), shield = new Object();
        check(state.update(player, sword, 10, true) == Change.NONE, "Joining does not invent an equip transition");
        check(state.hidden(player, sword, true), "Initially stowed item hides the hand");
        check(state.update(player, sword, 11, false) == Change.DRAW, "Battle entry requests hand-in despite unchanged inventory");
        check(!state.hidden(player, sword, false), "Draw immediately releases the hand");
        check(state.update(player, sword, 12, false) == Change.NONE, "Draw does not repeat each tick");
        check(state.update(player, sword, 13, true) == Change.STOW, "Leaving combat requests hand-out");
        state.lowerUntil(23);
        check(!state.hidden(player, sword, true), "No back duplicate while lowering");
        state.update(player, sword, 22, true);
        check(!state.hidden(player, sword, true), "Arm remains visible until lowering finishes");
        state.finishLowering();
        check(state.hidden(player, sword, true), "Actual clip completion hides arm and moves item to back");
        check(!state.hidden(player, sword, false), "Use before the next tick must never be hidden");
        state.update(player, sword, 24, false);
        state.update(player, sword, 25, true);
        state.lowerUntil(35);
        check(state.update(player, sword, 26, false) == Change.DRAW, "Rapid combat re-entry interrupts lowering");
        state.finishLowering();
        check(!state.hidden(player, sword, false), "An old clip cannot hide the drawn item");
        state.update(player, sword, 27, true);
        state.lowerUntil(37);
        state.update(player, sword, 37, true);
        check(state.hidden(player, sword, true), "Missing clip event has a bounded watchdog");
        check(state.update(player, shield, 38, true) == Change.NONE, "Changing item resets old transition");
        check(state.hidden(player, shield, true), "Replacement shield stows without a ghost arm");
        Object respawn = new Object();
        check(state.update(respawn, shield, 0, false) == Change.NONE, "Respawn resets animation state");
        check(!state.hidden(respawn, shield, false), "Respawn in combat does not inherit hidden hand");

        // Rendering may query the state several times between ticks. Releasing it must never wait
        // for the tick/animation listener, and a later clip event cannot undo that immediate release.
        state.update(respawn, shield, 140, true);
        state.lowerUntil(150);
        for (int frame = 0; frame < 5; frame++) {
            check(!state.hidden(respawn, shield, false), "Use/attack releases every render pipeline on input frame " + frame);
            check(!state.hidden(respawn, shield, true), "A lowering hand is not also rendered on the back " + frame);
        }
        state.update(respawn, shield, 141, false);
        state.finishLowering();
        check(!state.hidden(respawn, shield, true), "Interrupted stow callback cannot steal hand ownership");
        state.update(respawn, shield, 142, true);
        state.lowerUntil(152);
        state.update(respawn, shield, 151, true);
        check(!state.hidden(respawn, shield, true), "A fresh stow gets its own lowering watchdog");
        state.update(respawn, shield, 152, true);
        check(state.hidden(respawn, shield, true), "Steady updates do not extend the stow deadline");
        state.update(respawn, shield, 3, false);
        check(!state.hidden(respawn, shield, false), "Clock rollback resets hidden state even for the same owner");
        check(!state.hidden(new Object(), shield, false), "Unknown players never inherit a hidden offhand");
        state.reset();
        check(state.update(respawn, shield, 4, true) == Change.NONE, "Explicit disconnect reset clears the old transition");
        check(state.hidden(respawn, shield, true), "The new session chooses its own initial sheath visibility");
        System.out.println("PASS: " + checks + " offhand equip and stow transition checks");
    }
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
