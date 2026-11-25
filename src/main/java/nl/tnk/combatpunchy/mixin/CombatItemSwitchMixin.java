package nl.tnk.combatpunchy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import nl.tnk.combatpunchy.CombatModeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

/** Defer only native mining-preference exit; manual mode changes retain their original path. */
@Mixin(value = LocalPlayerPatch.class, remap = false)
public abstract class CombatItemSwitchMixin {
    @WrapOperation(method = "updateHeldItem(Lyesman/epicfight/world/capabilities/item/CapabilityItem;Lyesman/epicfight/world/capabilities/item/CapabilityItem;)V",
            at = @At(value = "INVOKE", target = "Lyesman/epicfight/client/world/capabilites/entitypatch/player/LocalPlayerPatch;toVanillaMode(Z)V"),
            require = 1)
    private void combatpunchy$deferNativeItemExit(LocalPlayerPatch patch, boolean synchronize,
                                                Operation<Void> original) {
        if (!CombatModeController.deferNativeItemSwitch(patch)) original.call(patch, synchronize);
    }
}
