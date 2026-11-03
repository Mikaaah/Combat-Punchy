package nl.tnk.combatpunchy;

import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import nl.tnk.combatpunchy.compat.FaceRenderPose;
import nl.tnk.combatpunchy.compat.MovesFaceLayer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.api.utils.math.Vec4f;

public final class MovesFaceTransformTest {
    private static int checks;

    public static void main(String[] args) {
        OpenMatrix4f bind = new OpenMatrix4f().translate(0, 1.514F, 0);
        OpenMatrix4f inverseBind = new OpenMatrix4f(bind).invert();
        for (float yaw : new float[]{0, 45, -90}) {
            for (float tilt : new float[]{0, -35, 70}) {
                OpenMatrix4f animated = new OpenMatrix4f().translate(0.2F, -0.3F, 0.1F)
                        .rotateDeg(yaw, new Vec3f(0, 1, 0)).rotateDeg(tilt, new Vec3f(0, 0, 1))
                        .mulBack(bind);
                Matrix4f before = OpenMatrix4f.exportToMojangMatrix(animated);
                Matrix4f face = MovesFaceLayer.headTransform(animated, inverseBind);
                OpenMatrix4f skin = OpenMatrix4f.mul(animated, inverseBind, null);
                // Each face point must stay on the corresponding skinned head/hat
                // point, even when combat rotates/translates the head away from Root.
                for (float extent : new float[]{0.25F, 0.28125F}) {
                    for (float x : new float[]{-extent, extent}) {
                        for (float y : new float[]{0, -0.5F}) {
                            Vector3f actual = face.transformPosition(new Vector3f(x, y, -extent));
                            Vec4f mesh = OpenMatrix4f.transform(skin, new Vec4f(-x, 1.5F - y, -extent, 1), null);
                            check(actual.distance(mesh.x, mesh.y, mesh.z) < 0.00001F,
                                    "face follows the same skinning transform as the head");
                        }
                    }
                }
                check(before.equals(OpenMatrix4f.exportToMojangMatrix(animated)), "shared skeleton pose is not modified");
            }
        }

        ModelPart head = new ModelPart(List.of(), Map.of());
        ModelPart hat = new ModelPart(List.of(), Map.of());
        head.loadPose(PartPose.offsetAndRotation(1, 2, 3, 0.2F, 0.4F, 0.6F));
        head.xScale = 1.2F;
        head.yScale = 0.8F;
        head.zScale = 1.1F;
        hat.copyFrom(head);
        hat.yRot = -0.7F;
        hat.visible = false;
        float[] savedHead = values(head);
        float[] savedHat = values(hat);
        try {
            try (FaceRenderPose ignored = new FaceRenderPose(head, hat)) {
                check(zeroPose(head) && zeroPose(hat), "vanilla head/hat transforms are not applied twice");
                check(!hat.visible, "hat visibility is preserved");
                throw new IllegalStateException("simulated render failure");
            }
        } catch (IllegalStateException expected) {
            check(java.util.Arrays.equals(savedHead, values(head)), "head pose and scale restored after render failure");
            check(java.util.Arrays.equals(savedHat, values(hat)), "hat pose and scale restored after render failure");
        }
        try (FaceRenderPose outer = new FaceRenderPose(head, hat)) {
            try (FaceRenderPose inner = new FaceRenderPose(head, hat)) {
                check(zeroPose(head), "nested render starts with a neutral head");
            }
            check(zeroPose(head), "nested render restores the enclosing pose");
        }
        check(java.util.Arrays.equals(savedHead, values(head)), "normal render restores original head");
        check(java.util.Arrays.equals(savedHat, values(hat)), "normal render restores original hat");
        System.out.println("PASS: " + checks + " facial attachment/pose checks");
    }

    private static boolean zeroPose(ModelPart p) {
        return p.x == 0 && p.y == 0 && p.z == 0 && p.xRot == 0 && p.yRot == 0 && p.zRot == 0
                && p.xScale == 1 && p.yScale == 1 && p.zScale == 1;
    }

    private static float[] values(ModelPart p) {
        return new float[]{p.x, p.y, p.z, p.xRot, p.yRot, p.zRot, p.xScale, p.yScale, p.zScale};
    }

    private static void check(boolean ok, String message) {
        checks++;
        if (!ok) throw new AssertionError(message);
    }
}
