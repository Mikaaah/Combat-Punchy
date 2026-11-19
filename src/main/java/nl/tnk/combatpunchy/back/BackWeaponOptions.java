package nl.tnk.combatpunchy.back;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Config parsing without Minecraft initialization; angles are degrees. */
public final class BackWeaponOptions {
    public record Pose(float scale, float rotation, float pitch, float yaw) {}
    private BackWeaponOptions() {}

    public static boolean validOverride(Object value) {
        if (!(value instanceof String text)) return false;
        try { parse(text); return true; }
        catch (IllegalArgumentException e) { return false; }
    }

    public static Map<String, Pose> overrides(List<? extends String> values) {
        Map<String, Pose> result = new LinkedHashMap<>();
        for (String value : values) {
            String[] parts = value.split("\\|", -1);
            result.put(parts[0].trim(), parse(value));
        }
        return Map.copyOf(result);
    }

    private static Pose parse(String text) {
        String[] parts = text.split("\\|", -1);
        if (parts.length != 5 || !parts[0].trim().matches("[a-z0-9_.-]+:[a-z0-9/._-]+"))
            throw new IllegalArgumentException("Expected item_id|scale|rotation|pitch|yaw");
        float scale = number(parts[1], 0.1F, 3F);
        return new Pose(scale, number(parts[2], -180, 180), number(parts[3], -180, 180), number(parts[4], -180, 180));
    }

    private static float number(String text, float min, float max) {
        float value = Float.parseFloat(text.trim());
        if (!Float.isFinite(value) || value < min || value > max)
            throw new IllegalArgumentException("Back item setting out of range");
        return value;
    }

    public static float roll(boolean shield, int side, float rotation) {
        return shield ? rotation : side == 1 ? rotation : 90 - rotation;
    }
}
