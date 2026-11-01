package nl.tnk.combatpunchy.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import nl.tnk.combatpunchy.CombatModeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.client.input.CombatKeyMapping;
import yesman.epicfight.client.input.EpicFightKeyMappings;

/** Keep normal key/context/modifier matching, without requiring combat before the first attack. */
@Mixin(value = CombatKeyMapping.class, remap = false)
public abstract class CombatAttackKeyMixin extends KeyMapping {
    private CombatAttackKeyMixin(String name, int key, String category) {
        super(name, key, category);
    }

    @Inject(method = "isActiveAndMatches(Lcom/mojang/blaze3d/platform/InputConstants$Key;)Z",
            at = @At("HEAD"), cancellable = true, require = 1)
    private void combatpunchy$acceptFirstWeaponAttack(InputConstants.Key key, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == EpicFightKeyMappings.ATTACK && CombatModeController.acceptWeaponAttackInVanilla()) {
            cir.setReturnValue(super.isActiveAndMatches(key));
        }
    }
}
