package nl.tnk.combatpunchy.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import nl.tnk.combatpunchy.back.BackWeapons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemInHandRenderer.class, priority = 1100)
public abstract class BackWeaponFirstPersonMixin {
    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void combatpunchy$stowedOffhand(AbstractClientPlayer player, float partial, float pitch,
            InteractionHand hand, float swing, ItemStack item, float equip, PoseStack poses,
            MultiBufferSource buffers, int light, CallbackInfo ci) {
        if (hand == InteractionHand.OFF_HAND && BackWeapons.offhandStowed(player)) ci.cancel();
    }
}
