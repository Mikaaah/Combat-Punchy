package nl.tnk.combatpunchy.back;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.layer.PatchedLayer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

/** Attach to Epic Fight's actual animated chest instead of the vanilla torso. */
public final class BackWeaponPatchedLayer extends PatchedLayer<AbstractClientPlayer,
        AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, BackWeaponLayer> {
    @Override
    protected void renderLayer(AbstractClientPlayerPatch<AbstractClientPlayer> patch, AbstractClientPlayer player,
            BackWeaponLayer layer, PoseStack poses, MultiBufferSource buffers, int light,
            OpenMatrix4f[] joints, float age, float yaw, float pitch, float partial) {
        if (layer == null || !BackWeapons.enabled() || !BackWeapons.local(player) || player.isInvisible()) return;
        var armature = patch.getArmature();
        if (armature != cachedArmature) {
            cachedArmature = armature;
            cachedChest = armature.hasJoint("Chest") ? armature.searchJointByName("Chest") : null;
        }
        Joint chest = cachedChest;
        if (chest == null) return;
        if (chest.getId() < 0 || chest.getId() >= joints.length || joints[chest.getId()] == null) return;
        poses.pushPose();
        try {
            var skinning = OpenMatrix4f.mul(joints[chest.getId()], chest.getToOrigin(), null);
            poses.mulPose(OpenMatrix4f.exportToMojangMatrix(skinning).translate(0, 1.5F, 0).scale(-1, -1, 1));
            layer.renderAttached(poses, buffers, light, player);
        } finally {
            poses.popPose();
        }
    }

    private Object cachedArmature;
    private Joint cachedChest;
}
