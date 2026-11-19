package nl.tnk.combatpunchy;

import java.io.IOException;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/** Executable contracts for the shipped native input target and our non-replaying injections. */
public final class InputContractTest {
    private static int checks;

    public static void main(String[] args) throws IOException {
        ClassNode engine = type("yesman/epicfight/client/events/engine/ControlEngine");
        method(engine, "handleEpicFightKeyMappings", "()V");
        method(engine, "maybeAttack", "()V");
        ClassNode mixin = type("nl/tnk/combatpunchy/mixin/ControlEngineMixin");
        MethodNode attack = method(mixin, "combatpunchy$beforeAttack",
                "(Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V");
        AnnotationNode injection = annotation(attack, "Lorg/spongepowered/asm/mixin/injection/Inject;");
        check(value(injection, "method") instanceof List<?> targets && targets.contains("maybeAttack()V"),
                "prediction intercepts the original attack target");
        check(Integer.valueOf(1).equals(value(injection, "require")), "missing input target must fail instead of silently dropping entry");
        check(!Boolean.TRUE.equals(value(injection, "cancellable")), "mode entry cannot cancel Epic Fight's original attack");
        check(value(injection, "at") instanceof List<?> locations && locations.size() == 1
                && "HEAD".equals(value((AnnotationNode) locations.get(0), "value")),
                "client mode/prediction must run before the native attack gate");
        int calls = 0;
        for (AbstractInsnNode instruction : attack.instructions) {
            if (instruction instanceof MethodInsnNode call) {
                calls++;
                check(call.owner.equals("nl/tnk/combatpunchy/CombatModeController")
                        && call.name.equals("beforeAttack") && call.desc.equals("()V"),
                        "input callback forwards the original input exactly once");
            }
        }
        check(calls == 1, "input callback cannot generate/replay an additional attack");

        ClassNode keyMixin = type("nl/tnk/combatpunchy/mixin/CombatAttackKeyMixin");
        MethodNode key = method(keyMixin, "combatpunchy$acceptFirstWeaponAttack",
                "(Lcom/mojang/blaze3d/platform/InputConstants$Key;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;)V");
        boolean matchesNormally = false;
        for (AbstractInsnNode instruction : key.instructions) {
            if (!(instruction instanceof MethodInsnNode call)) continue;
            if (call.owner.equals("net/minecraft/client/KeyMapping") && call.name.equals("isActiveAndMatches")
                    && call.getOpcode() == Opcodes.INVOKESPECIAL) matchesNormally = true;
            check(!call.name.equals("consumeClick") && !call.name.equals("click") && !call.name.equals("setDown"),
                    "first-click acceptance must not consume, duplicate or hold the physical input");
        }
        check(matchesNormally, "first click retains native context, modifiers and key matching");
        ClassNode controller = type("nl/tnk/combatpunchy/CombatModeController");
        MethodNode context = method(controller, "currentPatch",
                "()Lyesman/epicfight/client/world/capabilites/entitypatch/player/LocalPlayerPatch;");
        boolean playerPublished = false, levelPublished = false, clearsBack = false, clearsRendering = false;
        for (AbstractInsnNode instruction : context.instructions) {
            if (instruction instanceof FieldInsnNode field && field.getOpcode() == Opcodes.PUTFIELD
                    && field.owner.equals("nl/tnk/combatpunchy/CombatModeController$ClientState")) {
                if (field.name.equals("player")) playerPublished = true;
                if (field.name.equals("level")) levelPublished = true;
            }
            if (!(instruction instanceof MethodInsnNode call) || !call.name.equals("resetSession")) continue;
            if (call.owner.equals("nl/tnk/combatpunchy/back/BackWeapons")) clearsBack = true;
            else if (call.owner.equals("nl/tnk/combatpunchy/compat/RenderOwnership")) clearsRendering = true;
            else continue;
            check(playerPublished && levelPublished,
                    "new context must be published before cosmetic stop callbacks can re-enter it");
        }
        check(clearsBack && clearsRendering, "disconnect/world replacement clears both cosmetic owner caches");
        System.out.println("PASS: " + checks + " native input, prediction and lifecycle contracts");
    }

    private static ClassNode type(String name) throws IOException {
        try (var stream = InputContractTest.class.getClassLoader().getResourceAsStream(name + ".class")) {
            check(stream != null, "Missing input target " + name);
            ClassNode node = new ClassNode();
            new ClassReader(stream.readAllBytes()).accept(node, 0);
            return node;
        }
    }

    private static MethodNode method(ClassNode type, String name, String descriptor) {
        MethodNode result = type.methods.stream().filter(m -> m.name.equals(name) && m.desc.equals(descriptor))
                .findFirst().orElse(null);
        check(result != null, type.name + "." + name + descriptor);
        return result;
    }

    private static AnnotationNode annotation(MethodNode method, String descriptor) {
        if (method.visibleAnnotations != null) {
            for (AnnotationNode annotation : method.visibleAnnotations) if (annotation.desc.equals(descriptor)) return annotation;
        }
        if (method.invisibleAnnotations != null) {
            for (AnnotationNode annotation : method.invisibleAnnotations) if (annotation.desc.equals(descriptor)) return annotation;
        }
        throw new AssertionError("Missing annotation " + descriptor);
    }

    private static Object value(AnnotationNode annotation, String name) {
        for (int i = 0; annotation.values != null && i < annotation.values.size(); i += 2) {
            if (annotation.values.get(i).equals(name)) return annotation.values.get(i + 1);
        }
        return null;
    }

    private static void check(boolean condition, String detail) {
        checks++;
        if (!condition) throw new AssertionError(detail);
    }
}
