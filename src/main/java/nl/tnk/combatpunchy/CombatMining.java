package nl.tnk.combatpunchy;

import net.minecraft.client.Minecraft;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;

/** Client input policy only; normal mining outside battle mode is untouched. */
public final class CombatMining {
    private CombatMining() {}

    public static boolean blocksMining() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null || !client.player.isAlive()
                || client.player.isSpectator() || !CombatPunchyMod.ENABLED.get()
                || !CombatPunchyMod.COMBAT_MINING_RESTRICTION.get()) return false;
        if (!CombatModeController.isCombatActive()) return false;
        ItemStack stack = client.player.getMainHandItem();
        return !CombatMiningPolicy.mayMine(true, true,
                stack.getItem() instanceof AxeItem || stack.is(ItemTags.AXES),
                stack.getItem() instanceof PickaxeItem || stack.is(ItemTags.PICKAXES));
    }

}
