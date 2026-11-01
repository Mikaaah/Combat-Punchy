package nl.tnk.combatpunchy.mixin.moves;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import nl.tnk.combatpunchy.compat.RenderOwnership;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "punchymoves.client.camera.CameraTargeting", remap = false)
public abstract class MovesTargetingMixin {
    @Inject(method = "pick", at = @At("HEAD"), cancellable = true, require = 1)
    private static void combatpunchy$keepEpicFightTarget(Minecraft client, float partial, Vec3 origin,
                                                        Vector3fc direction, CallbackInfo ci) {
        if (RenderOwnership.epicCamera()) ci.cancel();
    }
}
