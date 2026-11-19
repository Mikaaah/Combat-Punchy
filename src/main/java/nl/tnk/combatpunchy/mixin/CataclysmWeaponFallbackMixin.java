package nl.tnk.combatpunchy.mixin;

import nl.tnk.combatpunchy.CataclysmWeaponFallbacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.world.capabilities.provider.CommonItemCapabilityProvider;

/** Run after native defaults/datapacks on either side; no assignment is overwritten. */
@Mixin(value = CommonItemCapabilityProvider.class, remap = false)
public abstract class CataclysmWeaponFallbackMixin {
    @Inject(method = "addDefaultItems()V", at = @At("TAIL"), require = 1)
    private void combatpunchy$missingCataclysmWeapons(CallbackInfo ci) {
        CataclysmWeaponFallbacks.registerMissing();
    }
}
