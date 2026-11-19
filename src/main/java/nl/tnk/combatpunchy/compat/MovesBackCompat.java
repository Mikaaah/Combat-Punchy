package nl.tnk.combatpunchy.compat;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import punchymoves.client.PlayerAnimationEngine;

/** Respect Moves' visual hand-swap state without a reflective invocation per render. */
public final class MovesBackCompat {
    private MovesBackCompat() {}

    public static ItemStack delayedMainHand(AbstractClientPlayer player) {
        if (player == null) return ItemStack.EMPTY;
        ItemStack visual = delayedHand(player, player.getMainArm());
        return visual == null ? ItemStack.EMPTY : visual;
    }

    /** Null means no delayed swap; EMPTY means a swap is active but the old hand was empty. */
    public static ItemStack delayedOffhand(AbstractClientPlayer player) {
        return player == null ? null : delayedHand(player, player.getMainArm().getOpposite());
    }

    private static ItemStack delayedHand(AbstractClientPlayer player, HumanoidArm arm) {
        return IntegrationPresence.MOVES ? Bridge.delayedHand(player, arm) : null;
    }

    private static final class Bridge {
        private static ItemStack delayedHand(AbstractClientPlayer player, HumanoidArm arm) {
            return PlayerAnimationEngine.delayedHandSwapVisual(player, arm);
        }
    }
}
