package nl.tnk.combatpunchy.mixin.moves;

import nl.tnk.combatpunchy.compat.RenderOwnership;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "punchymoves.client.camera.ShoulderCameraController", remap = false)
public abstract class MovesCameraMixin {
    @Shadow private static boolean freeCamera;

    @Inject(method = {"isActive()Z", "isFreeCameraActive()Z", "shouldHideLocalPlayerDuringTransition()Z",
            "shouldSkipLocalAvatarForCameraCollision()Z"}, at = @At("HEAD"), cancellable = true, require = 1)
    private static void combatpunchy$yieldCameraAndVisibility(CallbackInfoReturnable<Boolean> cir) {
        if (RenderOwnership.epicCamera()) {
            freeCamera = false;
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "localAvatarAlpha()F", at = @At("HEAD"), cancellable = true, require = 1)
    private static void combatpunchy$solidNativeAvatar(CallbackInfoReturnable<Float> cir) {
        if (RenderOwnership.epicCamera()) cir.setReturnValue(1.0F);
    }

    @Inject(method = "turnFreeCamera(DD)Z", at = @At("HEAD"), cancellable = true, require = 1)
    private static void combatpunchy$releaseOrbitLook(double yaw, double pitch, CallbackInfoReturnable<Boolean> cir) {
        if (RenderOwnership.epicCamera()) {
            freeCamera = false;
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateActivationTransition(F)V", at = @At("HEAD"), cancellable = true, require = 1)
    private static void combatpunchy$keepSavedCameraSettings(float elapsed, CallbackInfo ci) {
        if (RenderOwnership.epicCamera()) ci.cancel();
    }

    @Inject(method = "updateSpyglassCameraTransition(Z)V", at = @At("HEAD"), cancellable = true, require = 1)
    private static void combatpunchy$yieldContextCamera(boolean spyglass, CallbackInfo ci) {
        if (RenderOwnership.epicCamera()) ci.cancel();
    }

    @Inject(method = {"toggle()V", "toggleFreeCamera()V"}, at = @At("HEAD"), cancellable = true, require = 1)
    private static void combatpunchy$keepCombatCameraOwner(CallbackInfo ci) {
        if (RenderOwnership.epicCamera()) ci.cancel();
    }
}
