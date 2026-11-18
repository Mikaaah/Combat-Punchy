package nl.tnk.combatpunchy;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.world.capabilities.provider.ItemCapabilityProvider;
import yesman.epicfight.world.capabilities.item.WeaponTypeReloadListener;

/** Reload-time fallback shared by logical client/server; never depends on Minecraft's client classes. */
public final class CataclysmWeaponFallbacks {
    private CataclysmWeaponFallbacks() {}

    public static void registerMissing() {
        var preset = WeaponTypeReloadListener.get("epicfight:pickaxe");
        if (preset == null) return;
        CataclysmFallbackPolicy.installMissing(
                id -> {
                    ResourceLocation key = ResourceLocation.tryParse(id);
                    return key != null && ForgeRegistries.ITEMS.containsKey(key)
                            ? ForgeRegistries.ITEMS.getValue(key) : null;
                },
                ItemCapabilityProvider::get, item -> preset.apply(item).build(), ItemCapabilityProvider::put);
    }
}
