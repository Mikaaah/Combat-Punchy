package nl.tnk.combatpunchy.mixin;

import java.util.List;
import java.util.Set;
import nl.tnk.combatpunchy.compat.IntegrationPresence;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public final class CombatPunchyMixinPlugin implements IMixinConfigPlugin {
    public void onLoad(String mixinPackage) {}
    public String getRefMapperConfig() { return null; }
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains(".moves.")) return IntegrationPresence.MOVES;
        if (mixinClassName.endsWith("PunchyLegacyConfigMixin"))
            return IntegrationPresence.PUNCHY && !IntegrationPresence.PUNCHY_API;
        if (mixinClassName.endsWith("EpicFirstPersonMixin")) return IntegrationPresence.PUNCHY;
        if (mixinClassName.endsWith("EpicVanillaModelMixin")) return IntegrationPresence.MOVES;
        return true;
    }
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    public List<String> getMixins() { return null; }
    public void preApply(String name, ClassNode node, String mixin, IMixinInfo info) {}
    public void postApply(String name, ClassNode node, String mixin, IMixinInfo info) {}
}
