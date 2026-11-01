package nl.tnk.combatpunchy.compat;

import java.util.function.BooleanSupplier;
import nl.tnk.combatpunchy.CombatPunchyMod;
import org.slf4j.LoggerFactory;

public final class PunchyApiCompat {
    private PunchyApiCompat() {}

    public static void register() {
        if (!IntegrationPresence.PUNCHY_API) return;
        try {
            Class<?> api = Class.forName("punchy.api.PunchyApi");
            // This is Punchy's public transient-suppression API, not a saved config change.
            api.getMethod("disable", String.class, BooleanSupplier.class).invoke(null,
                    CombatPunchyMod.MOD_ID, (BooleanSupplier) RenderOwnership::epicFirstPerson);
            LoggerFactory.getLogger("CombatPunchy").info("Registered Punchy first-person handoff through PunchyApi");
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not register the supported Punchy first-person integration", e);
        }
    }
}
