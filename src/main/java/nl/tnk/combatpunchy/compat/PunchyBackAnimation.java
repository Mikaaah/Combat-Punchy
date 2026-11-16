package nl.tnk.combatpunchy.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import nl.tnk.combatpunchy.back.BackWeapons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import punchy.api.PunchyApi;

/** Cosmetic offhand clips never gate combat state or queue an attack. Client-thread confined. */
public final class PunchyBackAnimation {
    private static final String ID = "combatpunchy/back_equip";
    private static final Logger LOGGER = LoggerFactory.getLogger("CombatPunchy");
    private static final boolean TRACE = Boolean.getBoolean("combatpunchy.traceBackEquip");
    private static boolean registered;
    private static long ticket;
    private static boolean lowering;
    private PunchyBackAnimation() {}

    public static void register() {
        if (IntegrationPresence.PUNCHY_API && !registered) Bridge.register();
    }

    /** Returns a watchdog duration. Normal lowering ends on Punchy's clip-completion event. */
    public static int play(AbstractClientPlayer player, boolean stow) {
        return registered ? Bridge.play(player, stow) : 0;
    }

    /** Cancel stale cosmetic playback immediately, including before a predicted attack. */
    public static void reset() {
        if (registered) Bridge.cancel();
    }

    // Keep all optional Punchy types behind a class loaded only after its presence check.
    private static final class Bridge {
        private static void register() {
            PunchyApi.onClip(ID, event -> {
                if (ticket == 0 || event.ticket() != ticket) return;
                String phase = event.phase();
                if (TRACE) LOGGER.info("Offhand equip ticket {}: {}", ticket, phase);
                if (phase.equals("finish") || phase.equals("stop")) {
                    ticket = 0;
                    if (lowering) BackWeapons.finishOffhandLowering();
                    lowering = false;
                }
            });
            registered = true;
        }

        private static int play(AbstractClientPlayer player, boolean stow) {
            cancel();
            if (player == null || !Minecraft.getInstance().options.getCameraType().isFirstPerson()
                    || RenderOwnership.punchyDisabledForEpicFight() || player.isUsingItem()) return 0;
            String type = stow ? "hand_out" : "hand_in";
            String clip = PunchyApi.clipFor(player.getOffhandItem(), false, type);
            if (clip == null) clip = type;
            PunchyApi.ClipInfo info = PunchyApi.clipInfo(clip);
            if (info == null) return 0;
            PunchyApi.ClipRequest request = PunchyApi.ClipRequest.named(clip).hand(false).itemRig(type)
                    .priority(PunchyApi.CLIP_MINING).blendIn(stow ? 0.04F : 0.0F);
            lowering = stow;
            ticket = PunchyApi.playClip(ID, request);
            if (TRACE) LOGGER.info("Offhand equip clip {}: ticket {}", clip, ticket);
            return ticket == 0 ? 0 : Math.max(2, (int) Math.ceil(info.length() * 20) + 4);
        }

        private static void cancel() {
            long previous = ticket;
            ticket = 0;
            lowering = false;
            if (previous != 0) PunchyApi.stopClip(ID, previous);
        }
    }
}
