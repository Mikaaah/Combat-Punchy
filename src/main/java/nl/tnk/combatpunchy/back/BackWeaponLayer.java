package nl.tnk.combatpunchy.back;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import nl.tnk.combatpunchy.CombatPunchyMod;

/** The vanilla torso receives Moves' poses before this layer renders. */
public final class BackWeaponLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static List<? extends String> previousOverrides = List.of();
    private static Map<Item, BackWeaponOptions.Pose> itemOverrides = Map.of();
    private static BackWeaponOptions.Pose defaultWeapon, defaultShield;
    private static float lastScale, lastRotation, lastPitch, lastYaw, lastShieldScale, lastShieldRotation;
    public BackWeaponLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poses, MultiBufferSource buffers, int light, AbstractClientPlayer player,
            float limbSwing, float limbAmount, float partial, float age, float yaw, float pitch) {
        if (!BackWeapons.enabled() || !BackWeapons.local(player) || player.isInvisible()) return;
        poses.pushPose();
        try {
            getParentModel().body.translateAndRotate(poses);
            renderAttached(poses, buffers, light, player);
        } finally {
            poses.popPose();
        }
    }

    public void renderAttached(PoseStack poses, MultiBufferSource buffers, int light, AbstractClientPlayer player) {
        if (!BackWeapons.enabled() || !BackWeapons.local(player) || player.isInvisible()) return;
        boolean leftMain = player.getMainArm() == HumanoidArm.LEFT;
        draw(BackWeapons.mainBackStack(player), leftMain ? -1 : 1, false, poses, buffers, light, player);
        if (BackWeapons.offhandStowed(player))
            draw(player.getOffhandItem(), leftMain ? 1 : -1, true, poses, buffers, light, player);
    }

    private static void draw(ItemStack item, int side, boolean offhand, PoseStack poses,
            MultiBufferSource buffers, int light, AbstractClientPlayer player) {
        if (item.isEmpty()) return;
        boolean shield = BackWeapons.shield(item);
        var entries = CombatPunchyMod.BACK_ITEM_OVERRIDES.get();
        if (!entries.equals(previousOverrides)) {
            Map<Item, BackWeaponOptions.Pose> resolved = new HashMap<>();
            BackWeaponOptions.overrides(entries).forEach((id, pose) -> {
                ResourceLocation key = ResourceLocation.tryParse(id);
                if (key != null) BuiltInRegistries.ITEM.getOptional(key).ifPresent(value -> resolved.put(value, pose));
            });
            itemOverrides = Map.copyOf(resolved);
            previousOverrides = List.copyOf(entries);
        }
        BackWeaponOptions.Pose setting = itemOverrides.get(item.getItem());
        if (setting == null) setting = defaultPose(shield);
        poses.pushPose();
        try {
            poses.translate(shield ? 0 : side * 0.12, shield ? 0.38 : 0.34,
                    CombatPunchyMod.BACK_OFFSET.get() + (shield ? 0.10 : offhand ? 0.025 : 0));
            poses.mulPose(Axis.ZP.rotationDegrees(BackWeaponOptions.roll(shield, side, setting.rotation())));
            poses.mulPose(Axis.XP.rotationDegrees(setting.pitch()));
            // Vanilla's built-in shield has an origin-based model, unlike generated item sprites.
            boolean originShield = shield && Minecraft.getInstance().getItemRenderer()
                    .getModel(item, player.level(), player, player.getId()).isCustomRenderer();
            poses.mulPose(Axis.YP.rotationDegrees((originShield ? 0 : 180) + setting.yaw()));
            float scale = setting.scale();
            poses.scale(scale, scale, scale);
            if (originShield) poses.translate(0.5, 0.5, 0.5);
            Minecraft.getInstance().getItemRenderer().renderStatic(player, item,
                    originShield ? ItemDisplayContext.NONE : ItemDisplayContext.FIXED,
                    false, poses, buffers, player.level(), light, OverlayTexture.NO_OVERLAY, player.getId());
        } finally {
            poses.popPose();
        }
    }

    /** Config changes rebuild defaults; ordinary frames do not allocate pose records. */
    private static BackWeaponOptions.Pose defaultPose(boolean shield) {
        if (shield) {
            float scale = CombatPunchyMod.BACK_SHIELD_SCALE.get().floatValue();
            float rotation = CombatPunchyMod.BACK_SHIELD_ROTATION.get().floatValue();
            if (defaultShield == null || scale != lastShieldScale || rotation != lastShieldRotation) {
                lastShieldScale = scale;
                lastShieldRotation = rotation;
                defaultShield = new BackWeaponOptions.Pose(scale, rotation, 0, 0);
            }
            return defaultShield;
        }
        float scale = CombatPunchyMod.BACK_SCALE.get().floatValue();
        float rotation = CombatPunchyMod.BACK_ROTATION.get().floatValue();
        float pitch = CombatPunchyMod.BACK_PITCH.get().floatValue();
        float yaw = CombatPunchyMod.BACK_YAW.get().floatValue();
        if (defaultWeapon == null || scale != lastScale || rotation != lastRotation
                || pitch != lastPitch || yaw != lastYaw) {
            lastScale = scale;
            lastRotation = rotation;
            lastPitch = pitch;
            lastYaw = yaw;
            defaultWeapon = new BackWeaponOptions.Pose(scale, rotation, pitch, yaw);
        }
        return defaultWeapon;
    }
}
