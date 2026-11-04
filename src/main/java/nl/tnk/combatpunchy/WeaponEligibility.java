package nl.tnk.combatpunchy;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ShovelItem;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.RangedWeaponCapability;

public final class WeaponEligibility {
    private WeaponEligibility() {}

    public static boolean isCombatWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (item instanceof PickaxeItem || item instanceof ShovelItem || item instanceof HoeItem
                || item instanceof FishingRodItem || item instanceof ProjectileWeaponItem
                || item instanceof ShieldItem || stack.is(ItemTags.PICKAXES)
                || stack.is(ItemTags.SHOVELS) || stack.is(ItemTags.HOES)) {
            return false;
        }

        // An actual registered melee capability is required. Damage stats/names are not evidence.
        CapabilityItem capability = EpicFightCapabilities.getItemStackCapability(stack);
        if (!(capability instanceof WeaponCapability) || capability instanceof RangedWeaponCapability
                || capability.isEmpty()) return false;
        return capability.getWeaponCategory() != WeaponCategories.NOT_WEAPON
                && capability.getWeaponCategory() != WeaponCategories.PICKAXE
                && capability.getWeaponCategory() != WeaponCategories.SHOVEL
                && capability.getWeaponCategory() != WeaponCategories.HOE
                && capability.getWeaponCategory() != WeaponCategories.SHIELD
                && capability.getWeaponCategory() != WeaponCategories.BOW
                && capability.getWeaponCategory() != WeaponCategories.CROSSBOW;
    }
}
