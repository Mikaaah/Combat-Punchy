package nl.tnk.combatpunchy;

import java.io.IOException;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

/** Validate exact native mining targets and keep physical tools independent of weapon presets. */
public final class CombatMiningContractTest {
    private static int checks;

    public static void main(String[] args) throws IOException {
        CombatMiningPolicyTest.main(args);
        weaponContracts();
        itemSwitchGraceHook();
        ClassNode gameMode = type("net/minecraft/client/multiplayer/MultiPlayerGameMode");
        method(gameMode, "startDestroyBlock", "(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z");
        method(gameMode, "continueDestroyBlock", "(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z");
        method(gameMode, "destroyBlock", "(Lnet/minecraft/core/BlockPos;)Z");
        method(gameMode, "stopDestroyBlock", "()V");
        MethodNode nativeAttack = method(type("yesman/epicfight/client/world/capabilites/entitypatch/player/LocalPlayerPatch"),
                "canPlayAttackAnimation", "()Z");

        ClassNode mixin = type("nl/tnk/combatpunchy/mixin/CombatMiningMixin");
        MethodNode block = namedMethod(mixin, "combatpunchy$blockCombatMining");
        injection(block, "startDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z");
        injection(block, "continueDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z");
        boolean stopsProgress = false;
        for (AbstractInsnNode instruction : block.instructions) {
            if (instruction instanceof MethodInsnNode call && call.name.equals("stopDestroyBlock")) stopsProgress = true;
        }
        check(stopsProgress, "entering combat cancels any mining progress inherited from vanilla mode");
        injection(namedMethod(mixin, "combatpunchy$blockDirectCombatDestruction"),
                "destroyBlock(Lnet/minecraft/core/BlockPos;)Z");
        MethodNode preference = namedMethod(type("nl/tnk/combatpunchy/mixin/CombatMiningAttackMixin"),
                "combatpunchy$preferAttacksForRestrictedItems");
        String strategy = "yesman/epicfight/config/ClientConfig$PreferenceWork";
        preferenceHook(preference, strategy);
        int nativePreferenceCalls = 0;
        for (AbstractInsnNode instruction : nativeAttack.instructions) {
            if (instruction instanceof MethodInsnNode call && call.owner.equals(strategy)
                    && call.name.equals("checkHitResult") && call.desc.equals("()Z")) nativePreferenceCalls++;
        }
        check(nativePreferenceCalls == 1, "the mining-preference hook must match exactly one native decision");

        MethodNode policy = method(type("nl/tnk/combatpunchy/CombatMining"), "blocksMining", "()Z");
        boolean axeClass = false, pickaxeClass = false, axeTag = false, pickaxeTag = false;
        for (AbstractInsnNode instruction : policy.instructions) {
            if (instruction instanceof TypeInsnNode test && test.getOpcode() == Opcodes.INSTANCEOF) {
                if (test.desc.equals("net/minecraft/world/item/AxeItem")) axeClass = true;
                if (test.desc.equals("net/minecraft/world/item/PickaxeItem")) pickaxeClass = true;
            }
            if (instruction instanceof FieldInsnNode field && field.owner.equals("net/minecraft/tags/ItemTags")) {
                if (field.name.equals("AXES")) axeTag = true;
                if (field.name.equals("PICKAXES")) pickaxeTag = true;
            }
            if (instruction instanceof MethodInsnNode call) {
                check(!call.owner.equals("nl/tnk/combatpunchy/WeaponEligibility")
                        && !call.name.equals("getItemStackCapability"),
                        "a physical axe/pickaxe cannot lose mining permission because of its Epic Fight preset");
            }
        }
        check(axeClass && pickaxeClass && axeTag && pickaxeTag,
                "vanilla subclasses and standard tool tags both grant mining permission");
        System.out.println("PASS: " + checks + " native mining hook and tool classification contracts");
    }

    private static void itemSwitchGraceHook() throws IOException {
        String owner = "yesman/epicfight/client/world/capabilites/entitypatch/player/LocalPlayerPatch";
        String target = "updateHeldItem(Lyesman/epicfight/world/capabilities/item/CapabilityItem;Lyesman/epicfight/world/capabilities/item/CapabilityItem;)V";
        MethodNode nativeHeldItem = method(type(owner), "updateHeldItem",
                "(Lyesman/epicfight/world/capabilities/item/CapabilityItem;Lyesman/epicfight/world/capabilities/item/CapabilityItem;)V");
        int exits = 0;
        for (AbstractInsnNode instruction : nativeHeldItem.instructions) {
            if (instruction instanceof MethodInsnNode call && call.owner.equals(owner)
                    && call.name.equals("toVanillaMode") && call.desc.equals("(Z)V")) exits++;
        }
        check(exits == 1, "native item-switch preference must have exactly one exit call to defer");
        MethodNode hook = namedMethod(type("nl/tnk/combatpunchy/mixin/CombatItemSwitchMixin"),
                "combatpunchy$deferNativeItemExit");
        AnnotationNode wrap = null;
        if (hook.visibleAnnotations != null) {
            for (AnnotationNode annotation : hook.visibleAnnotations) if (annotation.desc.endsWith("/WrapOperation;")) wrap = annotation;
        }
        if (hook.invisibleAnnotations != null) {
            for (AnnotationNode annotation : hook.invisibleAnnotations) if (annotation.desc.endsWith("/WrapOperation;")) wrap = annotation;
        }
        check(wrap != null, "native item preference must be wrapped without canceling unrelated item updates");
        check(value(wrap, "method") instanceof List<?> targets && targets.size() == 1 && targets.contains(target),
                "manual and general mode-switch paths must remain outside the item-preference hook");
        check(Integer.valueOf(1).equals(value(wrap, "require")), "missing native preference exit must fail visibly");
        Object location = value(wrap, "at");
        AnnotationNode at = location instanceof AnnotationNode direct ? direct
                : location instanceof List<?> locations && locations.size() == 1
                && locations.get(0) instanceof AnnotationNode node ? node : null;
        check(at != null && "INVOKE".equals(value(at, "value"))
                && ("L" + owner + ";toVanillaMode(Z)V").equals(value(at, "target")),
                "only the native mining-preference mode exit is deferred");
        int fallbackCalls = 0;
        boolean checksOwnership = false;
        for (AbstractInsnNode instruction : hook.instructions) {
            if (!(instruction instanceof MethodInsnNode call)) continue;
            if (call.owner.equals("nl/tnk/combatpunchy/CombatModeController")
                    && call.name.equals("deferNativeItemSwitch")) checksOwnership = true;
            if (call.owner.equals("com/llamalad7/mixinextras/injector/wrapoperation/Operation")
                    && call.name.equals("call")) fallbackCalls++;
        }
        check(checksOwnership && fallbackCalls == 1,
                "unowned/disabled sessions keep exactly one original native exit without replay");
    }

    private static void weaponContracts() throws IOException {
        MethodNode eligibility = method(type("nl/tnk/combatpunchy/WeaponEligibility"),
                "isCombatWeapon", "(Lnet/minecraft/world/item/ItemStack;)Z");
        boolean respectsCapability = false, swordTag = false, keepsToolExclusion = false;
        for (AbstractInsnNode instruction : eligibility.instructions) {
            if (instruction instanceof TypeInsnNode test && test.getOpcode() == Opcodes.INSTANCEOF) {
                check(!test.desc.startsWith("net/minecraft/world/item/"),
                        "a Java tool superclass must not override an assigned melee capability");
            }
            if (instruction instanceof MethodInsnNode call && call.name.equals("getItemStackCapability")) respectsCapability = true;
            if (instruction instanceof FieldInsnNode field) {
                if (field.owner.equals("net/minecraft/tags/ItemTags") && field.name.equals("SWORDS")) swordTag = true;
                if (field.name.equals("PICKAXE")) keepsToolExclusion = true;
            }
        }
        check(respectsCapability && swordTag && keepsToolExclusion,
                "combat pickaxe weapons honor their sword tag while ordinary mining pickaxes stay excluded");
        try (var stream = CombatMiningContractTest.class.getClassLoader().getResourceAsStream(
                "data/epicfight/capabilities/weapons/item_keyword/spear.json")) {
            check(stream != null, "native Epic Fight spear keyword mapping must be available");
            var regexes = com.google.gson.JsonParser.parseString(new String(stream.readAllBytes(),
                    java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonArray("regexes");
            for (String material : new String[]{"wooden", "stone", "copper", "iron", "golden", "diamond", "netherite"}) {
                String id = "minecraft:" + material + "_spear";
                boolean matched = false;
                for (var regex : regexes) matched |= id.matches(regex.getAsString());
                check(matched, "native spear mapping covers Vanilla Backport item " + id);
            }
        }
    }

    private static void preferenceHook(MethodNode method, String strategy) {
        AnnotationNode modify = null;
        if (method.visibleAnnotations != null) {
            for (AnnotationNode annotation : method.visibleAnnotations) if (annotation.desc.endsWith("/ModifyExpressionValue;")) modify = annotation;
        }
        if (method.invisibleAnnotations != null) {
            for (AnnotationNode annotation : method.invisibleAnnotations) if (annotation.desc.endsWith("/ModifyExpressionValue;")) modify = annotation;
        }
        check(modify != null, "attack eligibility changes only the mining-preference expression");
        check(value(modify, "method") instanceof List<?> targets && targets.contains("canPlayAttackAnimation()Z"),
                "mining preference targets native attack eligibility");
        check(Integer.valueOf(1).equals(value(modify, "require")), "missing preference target must fail visibly");
        Object location = value(modify, "at");
        check(location instanceof List<?> locations && locations.size() == 1
                && locations.get(0) instanceof AnnotationNode, "preference hook must have one expression target");
        AnnotationNode at = (AnnotationNode) ((List<?>) location).get(0);
        check("INVOKE".equals(value(at, "value"))
                && ("L" + strategy + ";checkHitResult()Z").equals(value(at, "target")),
                "native mode, entity and target gates must run before mining-preference override");
    }

    private static void injection(MethodNode method, String target) {
        AnnotationNode inject = null;
        if (method.visibleAnnotations != null) {
            for (AnnotationNode annotation : method.visibleAnnotations) if (annotation.desc.endsWith("/Inject;")) inject = annotation;
        }
        if (method.invisibleAnnotations != null) {
            for (AnnotationNode annotation : method.invisibleAnnotations) if (annotation.desc.endsWith("/Inject;")) inject = annotation;
        }
        check(inject != null, "missing mining injection");
        check(value(inject, "method") instanceof List<?> targets && targets.contains(target), "correct mining target " + target);
        check(Integer.valueOf(1).equals(value(inject, "require")), "missing mining target must fail visibly");
        check(Boolean.TRUE.equals(value(inject, "cancellable")), "mining must be suppressible before any destroy packet");
        check(value(inject, "at") instanceof List<?> locations && locations.size() == 1
                && "HEAD".equals(value((AnnotationNode) locations.get(0), "value")), "mining guard must run before vanilla side effects");
    }

    private static Object value(AnnotationNode annotation, String name) {
        for (int i = 0; annotation.values != null && i < annotation.values.size(); i += 2) {
            if (annotation.values.get(i).equals(name)) return annotation.values.get(i + 1);
        }
        return null;
    }

    private static ClassNode type(String name) throws IOException {
        try (var stream = CombatMiningContractTest.class.getClassLoader().getResourceAsStream(name + ".class")) {
            check(stream != null, "missing mining target " + name);
            ClassNode node = new ClassNode();
            new ClassReader(stream.readAllBytes()).accept(node, 0);
            return node;
        }
    }

    private static MethodNode namedMethod(ClassNode type, String name) {
        MethodNode found = type.methods.stream().filter(m -> m.name.equals(name)).findFirst().orElse(null);
        check(found != null, type.name + "." + name);
        return found;
    }

    private static MethodNode method(ClassNode type, String name, String descriptor) {
        MethodNode found = type.methods.stream().filter(m -> m.name.equals(name) && m.desc.equals(descriptor))
                .findFirst().orElse(null);
        check(found != null, type.name + "." + name + descriptor);
        return found;
    }

    private static void check(boolean condition, String detail) {
        checks++;
        if (!condition) throw new AssertionError(detail);
    }
}
