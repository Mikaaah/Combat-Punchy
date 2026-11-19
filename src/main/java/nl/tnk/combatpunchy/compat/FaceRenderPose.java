package nl.tnk.combatpunchy.compat;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

/** Client-render-thread snapshot. Reuse it between draws; separate instances support nested renders. */
public final class FaceRenderPose implements AutoCloseable {
    private final SavedPart head = new SavedPart();
    private final SavedPart hat = new SavedPart();
    private boolean active;

    public FaceRenderPose() {}

    public FaceRenderPose(ModelPart head, ModelPart hat) {
        capture(head, hat);
    }

    public FaceRenderPose capture(ModelPart head, ModelPart hat) {
        if (active) throw new IllegalStateException("A face pose snapshot is already in use");
        this.head.capture(head);
        this.hat.capture(hat);
        head.loadPose(PartPose.ZERO);
        hat.loadPose(PartPose.ZERO);
        active = true;
        return this;
    }

    @Override
    public void close() {
        if (!active) return;
        head.restore();
        hat.restore();
        active = false;
    }

    private static final class SavedPart {
        private ModelPart part;
        private float x, y, z, xRot, yRot, zRot, xScale, yScale, zScale;

        private void capture(ModelPart part) {
            this.part = part;
            x = part.x; y = part.y; z = part.z;
            xRot = part.xRot; yRot = part.yRot; zRot = part.zRot;
            xScale = part.xScale; yScale = part.yScale; zScale = part.zScale;
        }

        private void restore() {
            part.x = x; part.y = y; part.z = z;
            part.xRot = xRot; part.yRot = yRot; part.zRot = zRot;
            part.xScale = xScale; part.yScale = yScale; part.zScale = zScale;
            part = null;
        }
    }
}
