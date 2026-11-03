package nl.tnk.combatpunchy;

/** Use the packet's IDs so an attacker outside tracking range still counts. */
public final class IncomingHit {
    private IncomingHit() {}

    public static boolean isCombatHit(int localPlayer, int damagedEntity, int cause, int direct) {
        if (damagedEntity != localPlayer || cause == localPlayer) return false;
        return cause >= 0 || (direct >= 0 && direct != localPlayer);
    }
}
