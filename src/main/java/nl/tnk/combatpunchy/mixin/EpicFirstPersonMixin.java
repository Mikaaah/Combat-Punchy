package nl.tnk.combatpunchy.mixin;

import net.neoforged.neoforge.client.event.RenderHandEvent;
import nl.tnk.combatpunchy.compat.RenderOwnership;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.events.engine.RenderEngine;

@Mixin(value = RenderEngine.class, remap = false)
public abstract class EpicFirstPersonMixin {
    @Inject(method = "epicfight$renderHand(Lnet/neoforged/neoforge/client/event/RenderHandEvent;)V",
            at = @At("HEAD"), cancellable = true, require = 1)
    private void combatpunchy$normalHandsBetweenCombatPoses(RenderHandEvent event, CallbackInfo ci) {
        if (RenderOwnership.filterFirstPerson() && !RenderOwnership.epicFirstPerson()) ci.cancel();
    }
}
