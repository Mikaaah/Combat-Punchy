package nl.tnk.combatpunchy;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.WeaponTypeReloadListener;

/** Reload-time fallback shared by logical client/server; never depends on Minecraft's client classes. */
public final class CataclysmWeaponFallbacks {
    private CataclysmWeaponFallbacks() {}

    public static void registerMissing() {
        var preset = WeaponTypeReloadListener.get("epicfight:pickaxe");
        if (preset == null) return;
        var provider = EpicFightCapabilities.ITEM_CAPABILITY_PROVIDER;
        CataclysmFallbackPolicy.installMissing(
                id -> BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(id)).orElse(null),
                provider::get, item -> preset.apply(item).build(), provider::put);
    }
}
