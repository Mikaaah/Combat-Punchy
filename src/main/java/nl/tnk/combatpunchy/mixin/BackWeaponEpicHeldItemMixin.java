package nl.tnk.combatpunchy.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import nl.tnk.combatpunchy.back.BackWeapons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/** Epic Fight's hand layer bypasses Minecraft's ItemInHandLayer completely. */
@Mixin(value = PatchedItemInHandLayer.class, remap = false)
public abstract class BackWeaponEpicHeldItemMixin {
    @WrapWithCondition(method = "renderLayer", at = @At(value = "INVOKE", target =
            "Lyesman/epicfight/client/renderer/patched/item/RenderItemBase;renderItemInHand(Lnet/minecraft/world/item/ItemStack;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/world/InteractionHand;[Lyesman/epicfight/api/utils/math/OpenMatrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IF)V"), require = 2)
    private boolean combatpunchy$singleOffhandOwner(RenderItemBase renderer, ItemStack stack,
            LivingEntityPatch<?> patch, InteractionHand hand, OpenMatrix4f[] joints,
            MultiBufferSource buffers, PoseStack poses, int light, float partial) {
        return hand != InteractionHand.OFF_HAND
                || !(patch.getOriginal() instanceof AbstractClientPlayer player)
                || !BackWeapons.offhandStowed(player);
    }
}
