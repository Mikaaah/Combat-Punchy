package nl.tnk.combatpunchy.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import nl.tnk.combatpunchy.CombatPunchyMod;
import org.joml.Matrix4f;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.renderer.patched.layer.PatchedLayer;
import yesman.epicfight.client.renderer.patched.layer.RenderOriginalModelLayer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

public final class MovesFaceLayer extends PatchedLayer<AbstractClientPlayer,
        AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>,
        RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> {
    private final RenderOriginalModelLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>,
            PlayerModel<AbstractClientPlayer>> original = new RenderOriginalModelLayer<>(
                    "Root", new Vec3f(0, 0.75F, 0), new Vec3f());

    @Override
    protected void renderLayer(AbstractClientPlayerPatch<AbstractClientPlayer> patch, AbstractClientPlayer player,
            RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> layer, PoseStack stack,
            MultiBufferSource buffers, int light, OpenMatrix4f[] poses, float age, float yaw, float pitch,
            float partial) {
        if (!RenderOwnership.movesBridge() || !CombatPunchyMod.MOVES_FACIAL_BRIDGE.get()) {
            original.renderLayer(player, patch, layer, stack, buffers, light, poses, age, yaw, pitch, partial);
            return;
        }
        if (layer == null || !patch.getArmature().hasJoint("Head")) return;
        Joint head = patch.getArmature().searchJointByName("Head");
        if (head.getId() < 0 || head.getId() >= poses.length || poses[head.getId()] == null) return;

        PlayerModel<AbstractClientPlayer> model = layer.getParentModel();
        stack.pushPose();
        try (FaceRenderPose ignored = new FaceRenderPose(model.head, model.hat)) {
            // Match the skinning matrix used by Epic Fight's head mesh, including
            // its inverse bind pose. A Root attachment cannot follow combat head motion.
            stack.mulPose(headTransform(poses[head.getId()], head.getToOrigin()));
            layer.render(stack, buffers, light, player, player.walkAnimation.position(),
                    player.walkAnimation.speed(), partial, age, yaw, pitch);
        } finally {
            stack.popPose();
        }
    }

    public static Matrix4f headTransform(OpenMatrix4f animatedHead, OpenMatrix4f inverseBind) {
        OpenMatrix4f skinning = OpenMatrix4f.mul(animatedHead, inverseBind, null);
        // Vanilla face vertices use pixels below a top-down model origin. The
        // Epic Fight player mesh uses block units above the feet (neck at 24/16).
        return OpenMatrix4f.exportToMojangMatrix(skinning).translate(0, 24.0F / 16.0F, 0).scale(-1, -1, 1);
    }
}
