[← Guide home](https://github.com/Mikaaah/Combat-Punchy/wiki) · [Project](https://github.com/Mikaaah/Combat-Punchy) · [Get help](https://github.com/Mikaaah/Combat-Punchy/issues)

# Weapon support

Combat Punchy uses **registered Epic Fight melee weapon capabilities** to decide which held items can trigger automatic combat. Item names and attack-damage values alone do not make an item eligible.

This includes **Weapons of Miracles (WOM)** melee weapons with suitable registered capabilities. WOM is optional.

## Excluded items

- Pickaxes, shovels and hoes, including items in their corresponding vanilla tags.
- Fishing rods.
- Projectile weapons and other ranged weapon capabilities.
- Shields.
- Empty hands, items without a melee weapon capability and capabilities categorized as non-weapons.

## Axes and block targeting

An axe with an eligible melee capability can enter combat when attacking an entity or swinging into air. A normal attack directed at a block keeps vanilla mining/chopping by default.

`enter_while_targeting_block = true` allows block-targeted attack inputs to trigger combat with an eligible weapon. It does not make excluded tools into melee weapons.

## Other weapon addons

A weapon from another addon must expose a suitable Epic Fight melee capability and avoid the excluded types/categories. Compatibility with every weapon addon is not implied.

[Automatic combat](https://github.com/Mikaaah/Combat-Punchy/wiki/Automatic-Combat) · [Wiki home](https://github.com/Mikaaah/Combat-Punchy/wiki/Home)
