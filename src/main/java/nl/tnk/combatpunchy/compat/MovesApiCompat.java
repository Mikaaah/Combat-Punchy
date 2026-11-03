package nl.tnk.combatpunchy.compat;

import java.util.function.Function;
import net.minecraft.client.player.AbstractClientPlayer;
import org.slf4j.LoggerFactory;

/** Advertise Epic Fight's pose ownership to Moves without a hard Moves dependency. */
public final class MovesApiCompat {
    private MovesApiCompat() {}

    public static void register() {
        if (!IntegrationPresence.MOVES) return;
        try {
            Class<?> api = Class.forName("punchymoves.api.client.AnimationIntent");
            Class<?> kind = Class.forName("punchymoves.api.client.AnimationIntent$Kind");
            Object combat = kind.getField("SPECIAL_ACTION").get(null);
            Object unspecified = kind.getField("UNSPECIFIED").get(null);
            // Mark combat as a special action so flight continuity cannot prefer
            // a Moves base pose. The locomotion mixin still performs the handoff.
            Function<AbstractClientPlayer, Object> intent = player ->
                    RenderOwnership.epicBody(player) ? combat : unspecified;
            api.getMethod("register", Function.class).invoke(null, intent);
            LoggerFactory.getLogger("CombatPunchy").info("Registered Moves combat ownership through AnimationIntent");
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not register the supported Moves animation integration", e);
        }
    }
}
