package nl.tnk.combatpunchy.mixin;

import net.minecraft.client.player.LocalPlayer;
import nl.tnk.combatpunchy.back.BackWeapons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class BackWeaponTickMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void combatpunchy$trackSelectedWeapon(CallbackInfo ci) {
        BackWeapons.tick((LocalPlayer) (Object) this);
        BackWeapons.tickOffhand((LocalPlayer) (Object) this);
    }
}
