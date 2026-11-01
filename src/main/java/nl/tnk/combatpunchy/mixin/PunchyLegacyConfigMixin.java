package nl.tnk.combatpunchy.mixin;

import nl.tnk.combatpunchy.compat.RenderOwnership;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Compatibility for the inspected pre-API Punchy 2.8g build; never modifies its saved settings. */
@Pseudo
@Mixin(targets = "punchy.config.PunchyConfig", remap = false)
public abstract class PunchyLegacyConfigMixin {
    @Inject(method = "isModEnabled()Z", at = @At("HEAD"), cancellable = true, require = 1)
    private static void combatpunchy$temporaryNativeHands(CallbackInfoReturnable<Boolean> cir) {
        if (RenderOwnership.epicFirstPerson()) cir.setReturnValue(false);
    }
}
