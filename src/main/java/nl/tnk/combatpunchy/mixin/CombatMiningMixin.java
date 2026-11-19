package nl.tnk.combatpunchy.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import nl.tnk.combatpunchy.CombatMining;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Stop mining before creative destruction or START/STOP_DESTROY_BLOCK packets are generated. */
@Mixin(MultiPlayerGameMode.class)
public abstract class CombatMiningMixin {
    @Shadow public abstract void stopDestroyBlock();

    @Inject(method = {
            "startDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
            "continueDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"
    }, at = @At("HEAD"), cancellable = true, require = 1)
    private void combatpunchy$blockCombatMining(BlockPos pos, Direction direction,
                                               CallbackInfoReturnable<Boolean> cir) {
        if (!CombatMining.blocksMining()) return;
        // Clear progress from mining that began before the mode change. Vanilla emits only
        // an abort for an existing operation; no new destroy operation starts here.
        stopDestroyBlock();
        cir.setReturnValue(false);
    }

    @Inject(method = "destroyBlock(Lnet/minecraft/core/BlockPos;)Z",
            at = @At("HEAD"), cancellable = true, require = 1)
    private void combatpunchy$blockDirectCombatDestruction(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (CombatMining.blocksMining()) cir.setReturnValue(false);
    }
}
