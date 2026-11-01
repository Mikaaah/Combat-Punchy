package nl.tnk.combatpunchy.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import nl.tnk.combatpunchy.compat.RenderOwnership;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

@Mixin(value = AbstractClientPlayerPatch.class, remap = false)
public abstract class EpicVanillaModelMixin {
    @ModifyExpressionValue(method = "overrideRender()Z", at = @At(value = "FIELD",
            target = "Lyesman/epicfight/config/ClientConfig;enableOriginalModel:Z"), require = 1)
    private boolean combatpunchy$vanillaBaseOutsideCombat(boolean original) {
        // Keep Epic Fight's validation event intact for spell/other native animation overrides.
        return RenderOwnership.movesBridge() || original;
    }
}
