package nl.tnk.combatpunchy.mixin.moves;

import net.minecraft.client.player.AbstractClientPlayer;
import nl.tnk.combatpunchy.compat.RenderOwnership;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import punchymoves.client.PlayerPoseState;

@Pseudo
@Mixin(targets = "punchymoves.client.render.AvatarRenderStateTracker", remap = false)
public abstract class MovesBodyRotationMixin {
    @Inject(method = "applyBodyRotationBlend", at = @At("HEAD"), cancellable = true, require = 1)
    private static void combatpunchy$keepNativeBodyYaw(AbstractClientPlayer player, PlayerPoseState state,
                                                      float partial, CallbackInfo ci) {
        if (RenderOwnership.epicBody(player)) ci.cancel();
    }
}
