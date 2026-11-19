package nl.tnk.combatpunchy;

import nl.tnk.combatpunchy.back.BackWeaponState;

public final class BackWeaponStateTest {
    private static int checks;
    public static void main(String[] args) {
        Object firstWorldPlayer = new Object();
        Object respawnedPlayer = new Object();
        BackWeaponState state = new BackWeaponState();
        state.update(firstWorldPlayer, 0, false);
        check(state.stowedSlot(firstWorldPlayer, 0, false) == -1, "No invented weapon before selecting one");
        state.update(firstWorldPlayer, 2, true);
        check(state.stowedSlot(firstWorldPlayer, 2, true) == -1, "Selected sword stays in hand");
        state.update(firstWorldPlayer, 4, false);
        check(state.stowedSlot(firstWorldPlayer, 4, false) == 2, "Tool selection stows actual sword slot");
        state.update(firstWorldPlayer, 6, false);
        check(state.stowedSlot(firstWorldPlayer, 6, false) == 2, "Switching tools keeps remembered weapon");
        state.update(firstWorldPlayer, 3, true);
        check(state.stowedSlot(firstWorldPlayer, 3, true) == -1, "New weapon has no duplicate back display");
        state.update(firstWorldPlayer, 6, false);
        check(state.stowedSlot(firstWorldPlayer, 6, false) == 3, "Most recently selected weapon wins");
        check(state.stowedSlot(firstWorldPlayer, 3, false) == -1, "Replaced selected slot cannot duplicate its contents");
        check(state.stowedSlot(respawnedPlayer, 6, false) == -1, "Old player state is never shown on a new player");
        state.update(respawnedPlayer, 6, false);
        check(state.stowedSlot(respawnedPlayer, 6, false) == -1, "Respawn clears weapon history");
        state.update(null, -1, false);
        check(state.stowedSlot(firstWorldPlayer, 6, false) == -1, "Disconnect clears weapon history");
        for (int flags = 0; flags < 32; flags++) {
            boolean enabled = (flags & 1) != 0, eligible = (flags & 2) != 0;
            boolean battle = (flags & 4) != 0, using = (flags & 8) != 0, swinging = (flags & 16) != 0;
            boolean stowed = BackWeaponState.stowOffhand(enabled, eligible, battle, using, swinging);
            check(stowed == (flags == 3), "Offhand returns for battle/use/swing and respects exclusions: " + flags);
        }
        System.out.println("PASS: " + checks + " back weapon inventory/visibility scenarios");
    }
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
