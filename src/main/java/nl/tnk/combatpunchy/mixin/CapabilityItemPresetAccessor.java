package nl.tnk.combatpunchy.mixin;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

/** The native preset ID survives builder inheritance even when categories/tags do not. */
@Mixin(value = CapabilityItem.class, remap = false)
public interface CapabilityItemPresetAccessor {
    @Accessor(value = "id", remap = false)
    ResourceLocation combatpunchy$getPresetId();
}
