package nl.tnk.combatpunchy;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.item.UseAnim;
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
    private static final ModeSession SESSION = new ModeSession();
    private static final CombatIdleTimeout IDLE_TIMEOUT = new CombatIdleTimeout();
    private static LocalPlayerPatch previousPatch;
    private static Item previousItem;
    private static int previousSlot = -1;

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
        LocalPlayerPatch patch = EpicFightCapabilities.getLocalPlayerPatch(client.player);
        return patch != null && patch.isVanillaMode()
                && EpicFightGameRules.CAN_SWITCH_PLAYER_MODE.getRuleValue(client.level)
                && WeaponEligibility.isCombatWeapon(client.player.getMainHandItem());
    }

    private static void refresh(boolean combatAttack, boolean checkTimeout) {
        refresh(combatAttack, checkTimeout, false);
    }

    private static void refresh(boolean combatAttack, boolean checkTimeout, boolean incomingHit) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayerPatch patch = EpicFightCapabilities.getLocalPlayerPatch(client.player);
        if (patch != previousPatch) {
            SESSION.reset();
            IDLE_TIMEOUT.reset();
            previousPatch = patch;
            previousItem = null;
            previousSlot = -1;
        }
        if (patch == null || client.level == null || client.player == null) return;
        if (!client.player.isAlive() || client.player.isSpectator() || client.player.isSleeping()) return;

        boolean allowed = EpicFightGameRules.CAN_SWITCH_PLAYER_MODE.getRuleValue(client.level);
        boolean weapon = WeaponEligibility.isCombatWeapon(client.player.getMainHandItem());
        Item item = client.player.getMainHandItem().getItem();
        int slot = client.player.getInventory().selected;
        boolean heldItemChanged = item != previousItem || slot != previousSlot;
        previousItem = item;
        previousSlot = slot;
        boolean enabled = CombatPunchyMod.ENABLED.get();
        ModeSession.Decision decision = SESSION.update(enabled, allowed,
                weapon, patch.isEpicFightMode(), combatAttack, incomingHit, heldItemChanged,
                new ModeSession.Features(CombatPunchyMod.AUTO_ENTER_COMBAT.get(),
                        CombatPunchyMod.AUTO_EXIT_ON_ITEM_SWITCH.get(), CombatPunchyMod.START_IN_VANILLA_MODE.get(),
                        CombatPunchyMod.AUTO_ENTER_ON_HIT.get()));
        if (!enabled || !allowed) {
            IDLE_TIMEOUT.reset();
        } else if ((weapon && combatAttack) || (incomingHit && CombatPunchyMod.AUTO_ENTER_ON_HIT.get())) {
            IDLE_TIMEOUT.activity(client.player.tickCount);
        }
        // Evaluate expiry after input processing, so a new attack at the deadline wins.
        if (checkTimeout && enabled && allowed && decision == ModeSession.Decision.NONE) {
            var state = patch.getEntityState();
            boolean active = state.attacking() || state.inaction() || state.hurt() || state.knockDown()
                    || patch.isHoldingAny() || patch.currentCompositeMotion == LivingMotions.BLOCK
                    || client.player.hurtTime > 0
                    || (client.player.isUsingItem() && client.player.getUseItem().getUseAnimation() == UseAnim.BLOCK);
            if (IDLE_TIMEOUT.expired(client.player.tickCount, patch.isEpicFightMode(), active,
                    CombatPunchyMod.AUTO_EXIT_ON_TIMEOUT.get() ? CombatPunchyMod.COMBAT_TIMEOUT_SECONDS.get() * 20 : 0)) {
                decision = ModeSession.Decision.ENTER_VANILLA;
            }
        }
        switch (decision) {
            case ENTER_COMBAT -> patch.toEpicFightMode(true);
            case ENTER_VANILLA -> {
                IDLE_TIMEOUT.reset();
                patch.toVanillaMode(true);
            }
            case NONE -> { }
        }
    }
}
