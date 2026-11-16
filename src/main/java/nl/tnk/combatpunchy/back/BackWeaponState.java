package nl.tnk.combatpunchy.back;

/** Tracks a slot, never a copied inventory stack. */
public final class BackWeaponState {
    private Object owner;
    private int weaponSlot = -1;

    public void update(Object currentOwner, int selectedSlot, boolean selectedWeapon) {
        if (owner != currentOwner) {
            owner = currentOwner;
            weaponSlot = -1;
        }
        if (currentOwner != null && selectedWeapon) weaponSlot = selectedSlot;
    }

    public int stowedSlot(Object currentOwner, int selectedSlot, boolean selectedWeapon) {
        return owner == currentOwner && !selectedWeapon && weaponSlot != selectedSlot ? weaponSlot : -1;
    }

    public static boolean stowOffhand(boolean enabled, boolean eligible, boolean battleMode,
            boolean usingOffhand, boolean swingingOffhand) {
        return enabled && eligible && !battleMode && !usingOffhand && !swingingOffhand;
    }
}
