package nl.tnk.combatpunchy;

import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.RangedWeaponCapability;

public final class WeaponEligibility {
    private static final ResourceLocation PICKAXE_CAPABILITY = ResourceLocation.parse("epicfight:pickaxe");
    private static final ResourceLocation SHOVEL_CAPABILITY = ResourceLocation.parse("epicfight:shovel");
    private static final ResourceLocation HOE_CAPABILITY = ResourceLocation.parse("epicfight:hoe");
    private WeaponEligibility() {}

    public static boolean isCombatWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;
        // A registered melee capability takes precedence over Java tool inheritance/tags.
        // Cataclysm's Infernal Forge, Void Forge and Brontes extend PickaxeItem, while
        // packs can assign them an Epic Fight axe/greatsword/etc. Ordinary pickaxe
        // capabilities stay excluded unless the item also declares itself a sword.
        // Never guess a moveset from damage or rewrite a pack-assigned capability.
        CapabilityItem capability = EpicFightCapabilities.getItemStackCapability(stack);
        if (!(capability instanceof WeaponCapability weapon) || capability instanceof RangedWeaponCapability
                || capability.isEmpty()) return false;
        // Neo's native tool presets inherit AXE, with their actual tool identity in
        // capability tags. An item's mining tag alone must not override a custom moveset.
        boolean swordTagged = stack.is(ItemTags.SWORDS);
        if ((!swordTagged && weapon.hasMatchingTag(PICKAXE_CAPABILITY))
                || weapon.hasMatchingTag(SHOVEL_CAPABILITY) || weapon.hasMatchingTag(HOE_CAPABILITY)) return false;
        return !capability.isWeaponCategory(WeaponCategories.NOT_WEAPON)
                && (!capability.isWeaponCategory(WeaponCategories.PICKAXE) || swordTagged)
                && !capability.isWeaponCategory(WeaponCategories.SHOVEL)
                && !capability.isWeaponCategory(WeaponCategories.HOE)
                && !capability.isWeaponCategory(WeaponCategories.SHIELD)
                && !capability.isWeaponCategory(WeaponCategories.RANGED);
    }
}
