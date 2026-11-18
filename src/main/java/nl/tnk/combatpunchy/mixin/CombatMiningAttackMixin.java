package nl.tnk.combatpunchy.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import nl.tnk.combatpunchy.CombatMining;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

/** Disable only mining preference; native mode, target and attack eligibility gates still run. */
@Mixin(value = LocalPlayerPatch.class, remap = false)
public abstract class CombatMiningAttackMixin {
    @ModifyExpressionValue(method = "canPlayAttackAnimation()Z",
            at = @At(value = "INVOKE", target = "Lyesman/epicfight/config/ClientConfig$PreferenceWork;checkHitResult()Z"),
            require = 1)
    private boolean combatpunchy$preferAttacksForRestrictedItems(boolean original) {
        return original && !CombatMining.blocksMining();
    }
}
