package nl.tnk.combatpunchy.compat;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

/** The animated skeleton already supplies these transforms; do not apply vanilla head rotation twice. */
public final class FaceRenderPose implements AutoCloseable {
    private final SavedPart head;
    private final SavedPart hat;

    public FaceRenderPose(ModelPart head, ModelPart hat) {
        this.head = new SavedPart(head);
        this.hat = new SavedPart(hat);
        head.loadPose(PartPose.ZERO);
        hat.loadPose(PartPose.ZERO);
    }

    @Override
    public void close() {
        head.restore();
        hat.restore();
    }

    private record SavedPart(ModelPart part, PartPose pose, float xScale, float yScale, float zScale) {
        SavedPart(ModelPart part) {
            this(part, part.storePose(), part.xScale, part.yScale, part.zScale);
        }

        void restore() {
            part.loadPose(pose);
            part.xScale = xScale;
            part.yScale = yScale;
            part.zScale = zScale;
        }
    }
}
