package nl.tnk.combatpunchy.compat;

import net.minecraft.world.entity.EntityType;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;

/** Replace Epic Fight's generic Root attachment only for Moves' optional face layer. */
public final class MovesFaceCompat {
    private MovesFaceCompat() {}

    public static void register() {
        if (!IntegrationPresence.MOVES_FACES) return;
        EpicFightClientEventHooks.Registry.MODIFY_PATCHED_ENTITY.registerEvent(event -> {
            if (!(event.get(EntityType.PLAYER) instanceof PPlayerRenderer renderer)) return;
            try {
                Class<?> faces = Class.forName("punchymoves.client.face.FaceLayer", false,
                        MovesFaceCompat.class.getClassLoader());
                renderer.addPatchedLayerAlways(faces, new MovesFaceLayer());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("The installed Moves facial layer could not be loaded", e);
            }
        }, "combatpunchy:moves_faces");
    }
}
