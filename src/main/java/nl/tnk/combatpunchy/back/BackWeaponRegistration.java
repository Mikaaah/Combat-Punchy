package nl.tnk.combatpunchy.back;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.client.event.EntityRenderersEvent;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;

public final class BackWeaponRegistration {
    private BackWeaponRegistration() {}
    public static void register(IEventBus bus) {
        bus.addListener((EntityRenderersEvent.AddLayers event) -> {
            for (var skin : event.getSkins()) {
                if (event.getPlayerSkin(skin) instanceof PlayerRenderer renderer)
                    renderer.addLayer(new BackWeaponLayer(renderer));
            }
        });
        bus.addListener((PatchedRenderersEvent.Modify event) -> {
            if (event.get(EntityType.PLAYER) instanceof PPlayerRenderer renderer)
                renderer.addPatchedLayerAlways(BackWeaponLayer.class, new BackWeaponPatchedLayer());
        });
    }
}
