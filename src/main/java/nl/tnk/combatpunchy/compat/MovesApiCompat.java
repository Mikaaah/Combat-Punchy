package nl.tnk.combatpunchy.compat;

import org.slf4j.LoggerFactory;
import punchymoves.api.client.AnimationIntent;

/** Optional typed registration; no reflective callback invocations during player animation. */
public final class MovesApiCompat {
    private MovesApiCompat() {}

    public static void register() {
        if (IntegrationPresence.MOVES) Bridge.register();
    }

    private static final class Bridge {
        private static void register() {
            AnimationIntent.register(player -> RenderOwnership.epicBody(player)
                    ? AnimationIntent.Kind.SPECIAL_ACTION : AnimationIntent.Kind.UNSPECIFIED);
            LoggerFactory.getLogger("CombatPunchy").info("Registered typed Moves combat ownership through AnimationIntent");
        }
    }
}
