package nl.tnk.combatpunchy.mixin;

import net.minecraftforge.client.event.RenderHandEvent;
import nl.tnk.combatpunchy.compat.RenderOwnership;
import org.spongepowered.asm.mixin.Mixin;
import yesman.epicfight.client.events.engine.RenderEngine;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderEngine.Events.class, remap = false)
public abstract class EpicFirstPersonMixin {
    @Inject(method = "renderHand(Lnet/minecraftforge/client/event/RenderHandEvent;)V",
            at = @At("HEAD"), cancellable = true, require = 1)
    private static void combatpunchy$normalHandsBetweenCombatPoses(RenderHandEvent event, CallbackInfo ci) {
        if (RenderOwnership.filterFirstPerson() && !RenderOwnership.epicFirstPerson()) ci.cancel();
    }
}
