package nl.tnk.combatpunchy;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.RangedWeaponCapability;

public final class WeaponEligibility {
    private WeaponEligibility() {}

    public static boolean isCombatWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;
        // A registered melee capability takes precedence over Java tool inheritance/tags.
        // Cataclysm's Infernal Forge, Void Forge and Brontes extend PickaxeItem, while
        // packs can assign them an Epic Fight axe/greatsword/etc. Ordinary pickaxe
        // capabilities stay excluded unless the item also declares itself a sword.
        // Never guess a moveset from damage or rewrite a pack-assigned capability.
        CapabilityItem capability = EpicFightCapabilities.getItemStackCapability(stack);
        if (!(capability instanceof WeaponCapability) || capability instanceof RangedWeaponCapability
                || capability.isEmpty()) return false;
        return !capability.isWeaponCategory(WeaponCategories.NOT_WEAPON)
                && (!capability.isWeaponCategory(WeaponCategories.PICKAXE) || stack.is(ItemTags.SWORDS))
                && !capability.isWeaponCategory(WeaponCategories.SHOVEL)
                && !capability.isWeaponCategory(WeaponCategories.HOE)
                && !capability.isWeaponCategory(WeaponCategories.SHIELD)
                && !capability.isWeaponCategory(WeaponCategories.RANGED);
    }
}
