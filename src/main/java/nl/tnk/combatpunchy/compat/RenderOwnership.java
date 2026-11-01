package nl.tnk.combatpunchy.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.UseAnim;
import nl.tnk.combatpunchy.CombatPunchyMod;
import nl.tnk.combatpunchy.FirstPersonHandoff;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.config.ClientConfig;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;

public final class RenderOwnership {
    private static final FirstPersonHandoff HANDOFF = new FirstPersonHandoff();
    private static LocalPlayerPatch lastPatch;
    private static Item lastItem;
    private static int lastSlot = -1;

    private RenderOwnership() {}

    public static boolean filterFirstPerson() {
        return IntegrationPresence.PUNCHY && CombatPunchyMod.ENABLED.get() && CombatPunchyMod.PUNCHY_BRIDGE.get();
    }

    public static boolean movesBridge() {
        return IntegrationPresence.MOVES && CombatPunchyMod.ENABLED.get() && CombatPunchyMod.MOVES_BRIDGE.get();
    }

    public static boolean epicFirstPerson() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayerPatch patch = EpicFightCapabilities.getLocalPlayerPatch(client.player);
        Item item = client.player == null ? null : client.player.getMainHandItem().getItem();
        int slot = client.player == null ? -1 : client.player.getInventory().selected;
        if (patch != lastPatch || item != lastItem || slot != lastSlot) {
            HANDOFF.reset();
            lastPatch = patch;
            lastItem = item;
            lastSlot = slot;
        }
        boolean allowed = filterFirstPerson() && patch != null && patch.isEpicFightMode()
                && ClientConfig.enableAnimatedFirstPersonModel && !forceVanillaItems(client);
        if (!allowed) return HANDOFF.update(0, false, false, 0);

        EntityState state = patch.getEntityState();
        boolean action = state.attacking() || state.inaction() || state.hurt() || state.knockDown()
                || patch.getCurrentLivingMotion() == LivingMotions.INACTION;
        boolean guard = patch.currentCompositeMotion == LivingMotions.BLOCK
                || (client.player.isUsingItem() && client.player.getUseItem().getUseAnimation() == UseAnim.BLOCK);
        boolean stance = false;
        if (CombatPunchyMod.NATIVE_STANCES.get()) {
            CapabilityItem cap = EpicFightCapabilities.getItemStackCapability(client.player.getMainHandItem());
            Style style = cap.getStyle(patch);
            stance = style == CapabilityItem.Styles.SHEATH || style == CapabilityItem.Styles.OCHS;
        }
        return HANDOFF.update(client.player.tickCount, true, action || guard || stance,
                CombatPunchyMod.FIRST_PERSON_RELEASE_TICKS.get());
    }

    private static boolean forceVanillaItems(Minecraft client) {
        if (client.player == null) return true;
        RenderEngine renderer = RenderEngine.getInstance();
        for (InteractionHand hand : InteractionHand.values()) {
            RenderItemBase itemRenderer = renderer.getItemRenderer(client.player.getItemInHand(hand));
            if (itemRenderer != null && itemRenderer.forceVanillaFirstPerson()) return true;
        }
        return false;
    }

    public static boolean epicBody(AbstractClientPlayer player) {
        if (!movesBridge() || player == null) return false;
        AbstractClientPlayerPatch<?> patch = EpicFightCapabilities.getEntityPatch(player, AbstractClientPlayerPatch.class);
        return patch != null && patch.overrideRender();
    }

    public static boolean epicCamera() {
        Minecraft client = Minecraft.getInstance();
        return CombatPunchyMod.MOVES_CAMERA_BRIDGE.get() && epicBody(client.player);
    }
}
