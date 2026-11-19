package nl.tnk.combatpunchy.back;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import nl.tnk.combatpunchy.CombatPunchyMod;
import nl.tnk.combatpunchy.CombatModeController;
import nl.tnk.combatpunchy.compat.IntegrationPresence;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

/** Client-thread confined cosmetic state for the current local player. No inventory mutation or packets. */
public final class BackWeapons {
    private static final BackWeaponState STATE = new BackWeaponState();
    private static final OffhandStowState OFFHAND = new OffhandStowState();
    private static Object offhandOwner, offhandItem;
    private static boolean punchyLayerHiding = !IntegrationPresence.PUNCHY;
    private static AbstractClientPlayer classifiedOwner;
    private static int classifiedTick = Integer.MIN_VALUE;
    private static int classifiedSlot = -1;
    private static ItemStack classifiedMain, classifiedOffhand;
    private static boolean mainEligible, offhandEligible;
    private static boolean mainEmpty, offhandEmpty;
    private static ItemStack cachedBackStack;
    private static int cachedBackTick = Integer.MIN_VALUE;
    private static boolean cachedBackEligible;
    private BackWeapons() {}

    /** Release references on world/player replacement, including disconnect without another player tick. */
    public static void resetSession() {
        if (IntegrationPresence.PUNCHY_API) nl.tnk.combatpunchy.compat.PunchyBackAnimation.reset();
        STATE.update(null, -1, false);
        OFFHAND.reset();
        offhandOwner = offhandItem = null;
        classifiedOwner = null;
        classifiedMain = classifiedOffhand = cachedBackStack = null;
        classifiedTick = cachedBackTick = Integer.MIN_VALUE;
        classifiedSlot = -1;
        mainEligible = offhandEligible = cachedBackEligible = false;
    }

    public static boolean enabled() {
        return CombatPunchyMod.ENABLED.get() && CombatPunchyMod.BACK_WEAPONS.get();
    }

    public static void tick(AbstractClientPlayer player) {
        classifyHands(player);
    }

    /** Refresh once per game tick or stack replacement, rather than once per render layer. */
    private static void classifyHands(AbstractClientPlayer player) {
        ItemStack main = player.getMainHandItem(), offhand = player.getOffhandItem();
        if (classifiedOwner == player && classifiedTick == player.tickCount
                && classifiedSlot == player.getInventory().selected
                && classifiedMain == main && classifiedOffhand == offhand
                && mainEmpty == main.isEmpty() && offhandEmpty == offhand.isEmpty()) return;
        if (classifiedOwner != player) {
            cachedBackStack = null;
            cachedBackTick = Integer.MIN_VALUE;
        }
        classifiedOwner = player;
        classifiedTick = player.tickCount;
        classifiedSlot = player.getInventory().selected;
        classifiedMain = main;
        classifiedOffhand = offhand;
        mainEmpty = main.isEmpty();
        offhandEmpty = offhand.isEmpty();
        mainEligible = eligible(main);
        offhandEligible = eligible(offhand);
        STATE.update(player, classifiedSlot, mainEligible);
    }

    public static boolean eligible(ItemStack stack) {
        if (stack.isEmpty()) return false;
        CapabilityItem cap = EpicFightCapabilities.getItemStackCapability(stack);
        if (cap.isEmpty()) return false;
        if (cap.getWeaponCategory() == WeaponCategories.NOT_WEAPON
                || cap.getWeaponCategory() == WeaponCategories.AXE
                || cap.getWeaponCategory() == WeaponCategories.SHOVEL
                || cap.getWeaponCategory() == WeaponCategories.PICKAXE
                || cap.getWeaponCategory() == WeaponCategories.HOE
                || cap.getWeaponCategory() == WeaponCategories.FIST) return false;
        // Addons can register their own glove category instead of inheriting FIST.
        String category = cap.getWeaponCategory().toString();
        return !category.equalsIgnoreCase("glove") && !category.equalsIgnoreCase("gloves");
    }

    public static boolean shield(ItemStack stack) {
        return stack.getItem() instanceof net.minecraft.world.item.ShieldItem
                || EpicFightCapabilities.getItemStackCapability(stack).getWeaponCategory() == WeaponCategories.SHIELD;
    }

    public static boolean local(AbstractClientPlayer player) {
        return player != null && player == Minecraft.getInstance().player && player.isAlive() && !player.isSpectator();
    }

    private static boolean wantsOffhandStowed(AbstractClientPlayer player) {
        if (!local(player)) return false;
        classifyHands(player);
        // Renderer selection is not combat state: Epic Fight may also animate the mining model.
        return BackWeaponState.stowOffhand(enabled() && punchyLayerHiding
                        && nl.tnk.combatpunchy.compat.MovesBackCompat.delayedOffhand(player) == null, offhandEligible,
                CombatModeController.isCombatActive(),
                player.isUsingItem() && player.getUsedItemHand() == InteractionHand.OFF_HAND,
                player.swinging && player.swingingArm == InteractionHand.OFF_HAND);
    }

    public static void tickOffhand(AbstractClientPlayer player) {
        Object item = player.getOffhandItem().getItem();
        if (offhandOwner != player || offhandItem != item) {
            if (IntegrationPresence.PUNCHY_API) nl.tnk.combatpunchy.compat.PunchyBackAnimation.reset();
            offhandOwner = player;
            offhandItem = item;
        }
        var change = OFFHAND.update(player, item, player.tickCount, wantsOffhandStowed(player));
        if (change != OffhandStowState.Change.NONE) {
            int duration = IntegrationPresence.PUNCHY_API
                    ? nl.tnk.combatpunchy.compat.PunchyBackAnimation.play(player,
                            change == OffhandStowState.Change.STOW) : 0;
            if (change == OffhandStowState.Change.STOW) {
                if (duration > 0) OFFHAND.lowerUntil(player.tickCount + duration);
                else OFFHAND.finishLowering();
            }
        }
    }

    public static void finishOffhandLowering() { OFFHAND.finishLowering(); }

    public static boolean offhandStowed(AbstractClientPlayer player) {
        return local(player) && OFFHAND.hidden(player, player.getOffhandItem().getItem(),
                wantsOffhandStowed(player));
    }

    public static boolean hiddenArm(AbstractClientPlayer player, HumanoidArm arm) {
        return arm != player.getMainArm() && offhandStowed(player);
    }

    public static ItemStack mainBackStack(AbstractClientPlayer player) {
        if (!enabled() || !local(player)) return ItemStack.EMPTY;
        if (eligible(nl.tnk.combatpunchy.compat.MovesBackCompat.delayedMainHand(player))) return ItemStack.EMPTY;
        // Stack/slot identity invalidates immediately; steady renders reuse the last tick's classification.
        classifyHands(player);
        int slot = STATE.stowedSlot(player, player.getInventory().selected, mainEligible);
        if (slot < 0 || slot >= 9) return ItemStack.EMPTY;
        ItemStack actual = player.getInventory().getItem(slot);
        if (cachedBackStack != actual || cachedBackTick != player.tickCount) {
            cachedBackStack = actual;
            cachedBackTick = player.tickCount;
            cachedBackEligible = eligible(actual);
        }
        return cachedBackEligible && !actual.isEmpty() ? actual : ItemStack.EMPTY;
    }

    /** Called only after the optional Punchy bridge has registered its native layer mask. */
    public static void punchyLayersRegistered() {
        punchyLayerHiding = true;
    }
}
