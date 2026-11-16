package nl.tnk.combatpunchy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.item.UseAnim;
import nl.tnk.combatpunchy.back.BackWeapons;
import nl.tnk.combatpunchy.compat.RenderOwnership;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.gamerule.EpicFightGameRules;
import yesman.epicfight.api.animation.LivingMotions;

@EventBusSubscriber(modid = CombatPunchyMod.MOD_ID, value = Dist.CLIENT)
public final class CombatModeController {
    // Input, damage callbacks and rendering all run on Minecraft's client thread.
    // One holder belongs to the current local player/world; no remote-player state leaks into it.
    private static final ClientState STATE = new ClientState();

    private static final class ClientState {
        final ModeSession session = new ModeSession();
        final CombatIdleTimeout timeout = new CombatIdleTimeout();
        final AttackPrediction prediction = new AttackPrediction();
        LocalPlayer player;
        ClientLevel level;
        LocalPlayerPatch patch;
        int patchLookupTick = Integer.MIN_VALUE;
        Item previousItem;
        int previousSlot = -1;

        void resetSession() {
            session.reset();
            timeout.reset();
            prediction.reset();
            previousItem = null;
            previousSlot = -1;
        }
    }

    private CombatModeController() {}

    @SubscribeEvent
    public static void afterClientTick(ClientTickEvent.Post event) {
        refresh(false, true);
    }

    public static void beforeInput() {
        refresh(false, false);
    }

    public static void beforeAttack() {
        Minecraft client = Minecraft.getInstance();
        if (client.screen != null || client.isPaused()) return;
        boolean aimingAtBlock = client.hitResult != null && client.hitResult.getType() == HitResult.Type.BLOCK;
        refresh(!aimingAtBlock || CombatPunchyMod.ENTER_WHILE_TARGETING_BLOCK.get(), false);
    }

    /** Called on the client thread before vanilla applies the server's hurt event. */
    public static void onIncomingHit(ClientboundDamageEventPacket packet) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || !IncomingHit.isCombatHit(client.player.getId(), packet.entityId(),
                packet.sourceCauseId(), packet.sourceDirectId())) return;
        refresh(false, false, true);
    }

    /** Allow only the attack mapping to receive a click while its weapon is still in vanilla mode. */
    public static boolean acceptWeaponAttackInVanilla() {
        Minecraft client = Minecraft.getInstance();
        if (client.screen != null || client.isPaused() || client.level == null || client.player == null
                || !client.player.isAlive() || client.player.isSpectator() || client.player.isSleeping()
                || !CombatPunchyMod.ENABLED.get() || !CombatPunchyMod.AUTO_ENTER_COMBAT.get()) return false;
        LocalPlayerPatch patch = currentPatch();
        return patch != null && patch.isVanillaMode()
                && EpicFightGameRules.CAN_SWITCH_PLAYER_MODE.getRuleValue(client.level)
                && WeaponEligibility.isCombatWeapon(client.player.getMainHandItem());
    }

    /** Client-thread cache shared by input and rendering, invalidated on owner/world replacement. */
    public static LocalPlayerPatch currentPatch() {
        Minecraft client = Minecraft.getInstance();
        if (!client.isSameThread()) return null;
        if (client.player != STATE.player || client.level != STATE.level) {
            boolean hadOwner = STATE.player != null || STATE.level != null;
            STATE.resetSession();
            STATE.player = client.player;
            STATE.level = client.level;
            STATE.patch = null;
            STATE.patchLookupTick = Integer.MIN_VALUE;
            // Publish the new owner first: cosmetic stop callbacks may query the context again.
            if (hadOwner) {
                BackWeapons.resetSession();
                RenderOwnership.resetSession();
            }
        }
        if (STATE.player == null || STATE.level == null) return null;
        // Do not cache an absent patch: Epic Fight can attach it later in the same client tick.
        if (STATE.patch == null || STATE.patchLookupTick != STATE.player.tickCount) {
            LocalPlayerPatch patch = EpicFightCapabilities.getLocalPlayerPatch(STATE.player);
            STATE.patchLookupTick = STATE.player.tickCount;
            if (patch != STATE.patch) {
                STATE.resetSession();
                STATE.patch = patch;
            }
        }
        return STATE.patch;
    }

    /** Actual client-predicted battle mode, independent of third-person render settings. */
    public static boolean isCombatActive() {
        LocalPlayerPatch patch = currentPatch();
        return patch != null && patch.isEpicFightMode();
    }

    /** Reserve first-person ownership before Epic Fight's animation state catches up. */
    public static boolean isAttackPredicted() {
        LocalPlayerPatch patch = currentPatch();
        Minecraft client = Minecraft.getInstance();
        if (patch == null) return false;
        if (!CombatPunchyMod.ENABLED.get() || !client.player.isAlive() || client.player.isSpectator()
                || client.player.isSleeping() || client.player.isUsingItem()
                || !EpicFightGameRules.CAN_SWITCH_PLAYER_MODE.getRuleValue(client.level)) {
            STATE.prediction.reset();
            return false;
        }
        return STATE.prediction.active(client.player.tickCount, patch.isEpicFightMode(),
                client.player.getMainHandItem().getItem(), client.player.getInventory().selected);
    }

    private static void refresh(boolean combatAttack, boolean checkTimeout) {
        refresh(combatAttack, checkTimeout, false);
    }

    private static void refresh(boolean combatAttack, boolean checkTimeout, boolean incomingHit) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayerPatch patch = currentPatch();
        if (patch == null) return;
        if (!client.player.isAlive() || client.player.isSpectator() || client.player.isSleeping()) {
            STATE.resetSession();
            return;
        }

        boolean allowed = EpicFightGameRules.CAN_SWITCH_PLAYER_MODE.getRuleValue(client.level);
        boolean weapon = WeaponEligibility.isCombatWeapon(client.player.getMainHandItem());
        Item item = client.player.getMainHandItem().getItem();
        int slot = client.player.getInventory().selected;
        boolean heldItemChanged = item != STATE.previousItem || slot != STATE.previousSlot;
        STATE.previousItem = item;
        STATE.previousSlot = slot;
        boolean enabled = CombatPunchyMod.ENABLED.get();
        ModeSession.Decision decision = STATE.session.update(enabled, allowed,
                weapon, patch.isEpicFightMode(), combatAttack, incomingHit, heldItemChanged,
                CombatPunchyMod.AUTO_ENTER_COMBAT.get(), CombatPunchyMod.AUTO_EXIT_ON_ITEM_SWITCH.get(),
                CombatPunchyMod.START_IN_VANILLA_MODE.get(), CombatPunchyMod.AUTO_ENTER_ON_HIT.get());
        if (!enabled || !allowed) {
            STATE.timeout.reset();
            STATE.prediction.reset();
        } else if ((weapon && combatAttack) || (incomingHit && CombatPunchyMod.AUTO_ENTER_ON_HIT.get())) {
            STATE.timeout.activity(client.player.tickCount);
        }
        // Evaluate expiry after input processing, so a new attack at the deadline wins.
        if (checkTimeout && enabled && allowed && decision == ModeSession.Decision.NONE) {
            var state = patch.getEntityState();
            boolean active = state.attacking() || state.inaction() || state.hurt() || state.knockDown()
                    || patch.isHoldingAny() || patch.currentCompositeMotion == LivingMotions.BLOCK
                    || client.player.hurtTime > 0
                    || (client.player.isUsingItem() && client.player.getUseItem().getUseAnimation() == UseAnim.BLOCK);
            if (STATE.timeout.expired(client.player.tickCount, patch.isEpicFightMode(), active,
                    CombatPunchyMod.AUTO_EXIT_ON_TIMEOUT.get() ? CombatPunchyMod.COMBAT_TIMEOUT_SECONDS.get() * 20 : 0)) {
                decision = ModeSession.Decision.ENTER_VANILLA;
            }
        }
        switch (decision) {
            case ENTER_COMBAT -> patch.toEpicFightMode(true);
            case ENTER_VANILLA -> {
                STATE.timeout.reset();
                STATE.prediction.reset();
                patch.toVanillaMode(true);
            }
            case NONE -> { }
        }
        // Mode changes are local immediately; Epic Fight queues its native mode packet before
        // the original attack code sends the skill request. Never consume or replay the click.
        if (enabled && allowed && weapon && combatAttack && patch.isEpicFightMode()
                && !client.player.isUsingItem() && patch.canPlayAttackAnimation()) {
            STATE.prediction.begin(client.player.tickCount, item, slot);
        }
        if (!patch.isEpicFightMode() || heldItemChanged) {
            // A new eligible attack after a selection change owns this tick's prediction.
            if (!(enabled && allowed && weapon && combatAttack && patch.isEpicFightMode())) {
                STATE.prediction.reset();
            }
        }
    }
}
