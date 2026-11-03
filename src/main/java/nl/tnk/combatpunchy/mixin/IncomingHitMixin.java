package nl.tnk.combatpunchy.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import nl.tnk.combatpunchy.CombatModeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class IncomingHitMixin {
    @Inject(method = "handleDamageEvent(Lnet/minecraft/network/protocol/game/ClientboundDamageEventPacket;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;handleDamageEvent(Lnet/minecraft/world/damagesource/DamageSource;)V"),
            require = 1)
    private void combatpunchy$reactToIncomingHit(ClientboundDamageEventPacket packet, CallbackInfo ci) {
        // PacketUtils has already moved execution from the network thread to the client thread.
        CombatModeController.onIncomingHit(packet);
    }
}
