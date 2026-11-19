package nl.tnk.combatpunchy.compat;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.HumanoidArm;
import nl.tnk.combatpunchy.CombatPunchyMod;
import nl.tnk.combatpunchy.back.BackWeapons;
import org.slf4j.LoggerFactory;
import punchy.api.PunchyApi;

/** Optional integration entry point; the typed bridge loads only when Punchy is present. */
public final class PunchyApiCompat {
    private PunchyApiCompat() {}

    public static void register() {
        if (IntegrationPresence.PUNCHY_API) Bridge.register();
    }

    private static final class Bridge {
        private static void register() {
            // Replace only Punchy's whole-battle-mode gate. Keep its other integrations.
            if (IntegrationPresence.PUNCHY_NATIVE_EPICFIGHT) PunchyApi.remove("punchy/epicfight");
            PunchyApi.disable(CombatPunchyMod.MOD_ID, RenderOwnership::punchyDisabledForEpicFight);
            PunchyApi.hideLayers("combatpunchy/back_weapons", (owner, leftArm) -> {
                if (!(owner instanceof AbstractClientPlayer player)) return 0;
                return BackWeapons.hiddenArm(player, leftArm ? HumanoidArm.LEFT : HumanoidArm.RIGHT)
                        ? PunchyApi.LAYER_ARM | PunchyApi.LAYER_ITEM : 0;
            });
            BackWeapons.punchyLayersRegistered();
            PunchyBackAnimation.register();
            LoggerFactory.getLogger("CombatPunchy").info("Registered typed Punchy v3 handoff and sheath layers");
        }
    }
}
