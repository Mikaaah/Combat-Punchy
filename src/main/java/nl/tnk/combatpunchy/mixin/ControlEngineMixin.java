package nl.tnk.combatpunchy.mixin;

import nl.tnk.combatpunchy.CombatModeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.events.engine.ControlEngine;

/** Runs before Epic Fight processes the same attack; it never replays an attack itself. */
@Mixin(value = ControlEngine.class, remap = false)
public abstract class ControlEngineMixin {
    @Inject(method = "handleEpicFightKeyMappings()V", at = @At("HEAD"), require = 1)
    private void combatpunchy$beforeInputs(CallbackInfo ci) {
        CombatModeController.beforeInput();
    }

    @Inject(method = "maybeAttack()V", at = @At("HEAD"), require = 1)
    private void combatpunchy$beforeAttack(CallbackInfo ci) {
        CombatModeController.beforeAttack();
    }
}
