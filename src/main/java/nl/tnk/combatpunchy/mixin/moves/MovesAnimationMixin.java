package nl.tnk.combatpunchy.mixin.moves;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.AbstractClientPlayer;
import nl.tnk.combatpunchy.compat.RenderOwnership;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import punchymoves.client.animation.ExternalLocomotionControl;

@Pseudo
@Mixin(targets = "punchymoves.client.PlayerAnimationEngine", remap = false)
public abstract class MovesAnimationMixin {
    @WrapOperation(method = "updateExternalLocomotion", at = @At(value = "INVOKE",
            target = "Lpunchymoves/client/animation/ExternalLocomotionControl;update(ZIZZZ)Z"), require = 1)
    private static boolean combatpunchy$nativeAnimationOwner(ExternalLocomotionControl control,
            boolean nativeActive, int tick, boolean explicit, boolean coveredBase, boolean specialAction,
            Operation<Boolean> original, @Local(argsOnly = true) AbstractClientPlayer player) {
        if (RenderOwnership.epicBody(player)) {
            // Use Moves' own native-animation handoff, including its input discard and pose reset.
            return original.call(control, true, tick, false, false, true);
        }
        return original.call(control, nativeActive, tick, explicit, coveredBase, specialAction);
    }
}
