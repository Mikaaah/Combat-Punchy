package nl.tnk.combatpunchy;

import java.util.List;
import nl.tnk.combatpunchy.back.BackWeaponOptions;

public final class BackWeaponOptionsTest {
    private static int checks;
    public static void main(String[] args) {
        for (String bad : List.of("", "minecraft:sword", "bad id|1|0|0|0", "Sword|1|0|0|0",
                "minecraft:sword|NaN|0|0|0", "minecraft:sword|Infinity|0|0|0", "minecraft:sword|0|0|0|0",
                "minecraft:sword|3.1|0|0|0", "minecraft:sword|1|181|0|0", "minecraft:sword|1|0|-181|0",
                "minecraft:sword|1|0|0|Infinity", "minecraft:sword|1|0|0|0|extra", "minecraft:sword|x|0|0|0"))
            check(!BackWeaponOptions.validOverride(bad), "Reject invalid config: " + bad);
        check(!BackWeaponOptions.validOverride(7), "Reject non-string entries");
        check(BackWeaponOptions.validOverride(" test:large_blade | 0.45 | 12 | -5 | 90 "), "Accept custom item and whitespace");
        check(BackWeaponOptions.validOverride("test:weapons/blade|0.1|-180|180|0"), "Accept legal paths and angle bounds");
        var items = BackWeaponOptions.overrides(List.of("test:large_blade|0.45|12|-5|90",
                "minecraft:shield|0.7|0|0|0", "test:large_blade|0.5|8|0|0"));
        check(items.size() == 2, "Independent per-item adjustments");
        check(items.get("test:large_blade").scale() == 0.5F, "Last duplicate wins");
        check(items.get("minecraft:shield").scale() == 0.7F, "Shield override remains independent");
        check(!items.containsKey("minecraft:diamond_sword"), "Unlisted items fall back to defaults");
        check(BackWeaponOptions.overrides(List.of()).isEmpty(), "Clearing overrides restores global defaults");
        for (float tilt : new float[]{-10, 0, 6, 15, 45}) {
            float right = BackWeaponOptions.roll(false, 1, tilt);
            float left = BackWeaponOptions.roll(false, -1, tilt);
            check(Math.abs(right + left - 90) < 0.001, "Mirrored weapons remain symmetric");
        }
        double oldHandle = Math.abs(0.12 - 0.5 * Math.cos(Math.toRadians(45)));
        double newHandle = Math.abs(0.12 - 0.85 * 0.5 * Math.cos(Math.toRadians(45 + BackWeaponOptions.roll(false, 1, 6))));
        check(newHandle < oldHandle, "Revised sword handle is closer to the spine/head");
        check(BackWeaponOptions.roll(true, 1, 0) == 0 && BackWeaponOptions.roll(true, -1, 0) == 0,
                "Shields stay upright in either hand");
        check(BackWeaponOptions.roll(true, -1, 17) == 17, "Shield rotation does not acquire a weapon diagonal");
        System.out.println("PASS: " + checks + " back placement and per-item config checks");
    }
    private static void check(boolean value, String message) {
        checks++;
        if (!value) throw new AssertionError(message);
    }
}
