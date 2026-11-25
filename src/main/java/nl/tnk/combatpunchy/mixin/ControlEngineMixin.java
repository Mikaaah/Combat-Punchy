package nl.tnk.combatpunchy.mixin;

import nl.tnk.combatpunchy.CombatModeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.client.input.action.EpicFightInputAction;
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

    @Inject(method = "maybeDodge()V", at = @At("HEAD"), require = 1)
    private void combatpunchy$beforeDodge(CallbackInfo ci) {
        CombatModeController.beforeCombatAction(EpicFightInputAction.DODGE);
    }

    @Inject(method = "maybeGuard()V", at = @At("HEAD"), require = 1)
    private void combatpunchy$beforeGuard(CallbackInfo ci) {
        CombatModeController.beforeCombatAction(EpicFightInputAction.GUARD);
    }

    @Inject(method = "maybePerformMoverSkill()V", at = @At("HEAD"), require = 1)
    private void combatpunchy$beforePerformMoverSkill(CallbackInfo ci) {
        CombatModeController.beforeCombatAction(EpicFightInputAction.MOBILITY);
    }

    @Inject(method = "handleSeparateWeaponInnateSkill()V", at = @At("HEAD"), require = 1)
    private void combatpunchy$beforeSeparateWeaponInnateSkill(CallbackInfo ci) {
        CombatModeController.beforeCombatAction(EpicFightInputAction.WEAPON_INNATE_SKILL);
    }
}
