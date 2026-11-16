package nl.tnk.combatpunchy.back;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;

public final class BackWeaponRegistration {
    private BackWeaponRegistration() {}
    public static void register(IEventBus bus) {
        bus.addListener((EntityRenderersEvent.AddLayers event) -> {
            for (var skin : event.getSkins()) {
                if (event.getSkin(skin) instanceof PlayerRenderer renderer)
                    renderer.addLayer(new BackWeaponLayer(renderer));
            }
        });
        EpicFightClientEventHooks.Registry.MODIFY_PATCHED_ENTITY.registerEvent(event -> {
            if (event.get(EntityType.PLAYER) instanceof PPlayerRenderer renderer)
                renderer.addPatchedLayerAlways(BackWeaponLayer.class, new BackWeaponPatchedLayer());
        }, "combatpunchy:back_weapons");
    }
}
