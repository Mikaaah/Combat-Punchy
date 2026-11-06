package nl.tnk.combatpunchy.mixin.moves;

import nl.tnk.combatpunchy.compat.RenderOwnership;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Moves 1.0.7's inventory handoff queries saved camera ownership separately from isActive(). */
@Pseudo
@Mixin(targets = "punchymoves.client.camera.ShoulderCameraController", remap = false)
public abstract class MovesSpatialCameraMixin {
    @Inject(method = "hasCustomCamera()Z", at = @At("HEAD"), cancellable = true, require = 1)
    private static void combatpunchy$yieldInventoryCameraHandoff(CallbackInfoReturnable<Boolean> cir) {
        // Keep Spatial GUI's own camera intact, but do not seed or restore a Moves
        // shoulder-camera offset while Epic Fight owns the player's combat camera.
        if (RenderOwnership.epicCamera()) cir.setReturnValue(false);
    }
}
