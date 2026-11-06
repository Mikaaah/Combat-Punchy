package nl.tnk.combatpunchy.compat;

/** Inspect resources without initializing optional mods during Mixin discovery. */
public final class IntegrationPresence {
    public static final boolean PUNCHY = has("punchy/config/PunchyConfig.class");
    public static final boolean PUNCHY_API = has("punchy/api/PunchyApi.class");
    public static final boolean MOVES = has("punchymoves/client/PlayerAnimationEngine.class");
    public static final boolean MOVES_SPATIAL_CAMERA = MOVES
            && has("punchymoves/client/compat/spatial/SpatialCameraEntry.class");
    public static final boolean MOVES_FACES = MOVES && has("punchymoves/client/face/FaceLayer.class");

    private IntegrationPresence() {}

    private static boolean has(String resource) {
        return IntegrationPresence.class.getClassLoader().getResource(resource) != null;
    }
}
