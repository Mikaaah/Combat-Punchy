package nl.tnk.combatpunchy;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/** Validate the exact optional APIs and injection targets without initializing Minecraft. */
public final class UpstreamContractTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        method(localType("net/minecraft/client/player/LocalPlayer"), "tick", "()V");
        method(localType("net/minecraft/client/renderer/entity/layers/ItemInHandLayer"), "renderArmWithItem",
                "(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V");
        method(localType("net/minecraft/client/renderer/ItemInHandRenderer"), "renderArmWithItem",
                "(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V");
        // Epic Fight does not delegate to the vanilla hand layer. Both native calls must be wrapped.
        String heldItemDescriptor = "(Lnet/minecraft/world/item/ItemStack;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/world/InteractionHand;[Lyesman/epicfight/api/utils/math/OpenMatrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IF)V";
        method(localType("yesman/epicfight/client/renderer/patched/item/RenderItemBase"), "renderItemInHand", heldItemDescriptor);
        ClassNode epicHands = localType("yesman/epicfight/client/renderer/patched/layer/PatchedItemInHandLayer");
        long heldItemCalls = epicHands.methods.stream().filter(m -> m.name.equals("renderLayer"))
                .flatMap(m -> java.util.stream.StreamSupport.stream(m.instructions.spliterator(), false))
                .filter(n -> n instanceof MethodInsnNode call
                        && call.owner.equals("yesman/epicfight/client/renderer/patched/item/RenderItemBase")
                        && call.name.equals("renderItemInHand") && call.desc.equals(heldItemDescriptor)).count();
        check(heldItemCalls == 2, "Epic Fight main/offhand call sites must both match the hand suppression mixin");
        try (var mixins = UpstreamContractTest.class.getClassLoader().getResourceAsStream("combatpunchy.mixins.json")) {
            check(mixins != null && new String(mixins.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8)
                    .contains("\"BackWeaponEpicHeldItemMixin\""), "Epic Fight hand suppression mixin must be registered");
        }
        try (ZipFile punchy = new ZipFile(args[0]); ZipFile moves = new ZipFile(args[1])) {
            ClassNode api = type(punchy, "punchy/api/PunchyApi");
            method(api, "remove", "(Ljava/lang/String;)V");
            method(api, "hideLayers", "(Ljava/lang/String;Lpunchy/api/PunchyApi$LayerLook;)V");
            method(type(punchy, "punchy/api/PunchyApi$LayerLook"), "hide", "(Ljava/lang/Object;Z)I");
            field(api, "LAYER_ITEM", "I");
            field(api, "LAYER_ARM", "I");
            method(api, "clipFor", "(Ljava/lang/Object;ZLjava/lang/String;)Ljava/lang/String;");
            method(api, "clipInfo", "(Ljava/lang/String;)Lpunchy/api/PunchyApi$ClipInfo;");
            method(api, "playClip", "(Ljava/lang/String;Lpunchy/api/PunchyApi$ClipRequest;)J");
            method(api, "stopClip", "(Ljava/lang/String;J)V");
            method(api, "onClip", "(Ljava/lang/String;Lpunchy/api/PunchyApi$ClipListener;)V");
            method(type(punchy, "punchy/api/PunchyApi$ClipInfo"), "length", "()F");
            ClassNode request = type(punchy, "punchy/api/PunchyApi$ClipRequest");
            method(request, "named", "(Ljava/lang/String;)Lpunchy/api/PunchyApi$ClipRequest;");
            method(request, "hand", "(Z)Lpunchy/api/PunchyApi$ClipRequest;");
            method(request, "itemRig", "(Ljava/lang/String;)Lpunchy/api/PunchyApi$ClipRequest;");
            method(request, "priority", "(I)Lpunchy/api/PunchyApi$ClipRequest;");
            method(request, "blendIn", "(F)Lpunchy/api/PunchyApi$ClipRequest;");
            ClassNode event = type(punchy, "punchy/api/PunchyApi$ClipEvent");
            method(event, "ticket", "()J");
            method(event, "phase", "()Ljava/lang/String;");
            method(api, "disable", "(Ljava/lang/String;Ljava/util/function/BooleanSupplier;)V");
            method(api, "useMinecraftHands", "(Ljava/lang/String;Ljava/util/function/BooleanSupplier;)V");
            method(type(punchy, "punchy/compat/EpicFightHands"), "drawing", "(Ljava/lang/Object;)Z");
            method(type(punchy, "punchy/config/PunchyConfig"), "isModEnabled", "()Z");

            ClassNode intent = type(moves, "punchymoves/api/client/AnimationIntent");
            method(intent, "register", "(Ljava/util/function/Function;)Ljava/lang/AutoCloseable;");
            ClassNode kind = type(moves, "punchymoves/api/client/AnimationIntent$Kind");
            field(kind, "SPECIAL_ACTION", "Lpunchymoves/api/client/AnimationIntent$Kind;");
            field(kind, "UNSPECIFIED", "Lpunchymoves/api/client/AnimationIntent$Kind;");

            ClassNode engine = type(moves, "punchymoves/client/PlayerAnimationEngine");
            method(engine, "delayedHandSwapVisual",
                    "(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/world/entity/HumanoidArm;)Lnet/minecraft/world/item/ItemStack;");
            MethodNode update = method(engine, "updateExternalLocomotion",
                    "(Lnet/minecraft/client/player/AbstractClientPlayer;Lpunchymoves/client/PlayerAnimationEngine$PlayerState;Z)Z");
            long handoffs = java.util.stream.StreamSupport.stream(update.instructions.spliterator(), false)
                    .filter(n -> n instanceof MethodInsnNode call
                            && call.owner.equals("punchymoves/client/animation/ExternalLocomotionControl")
                            && call.name.equals("update") && call.desc.equals("(ZIZZZ)Z")).count();
            check(handoffs == 1, "Moves animation injection must match exactly one native handoff");
            method(type(moves, "punchymoves/client/animation/ExternalLocomotionControl"), "update", "(ZIZZZ)Z");

            ClassNode camera = type(moves, "punchymoves/client/camera/ShoulderCameraController");
            field(camera, "freeCamera", "Z");
            for (String name : new String[]{"isActive", "isFreeCameraActive", "shouldHideLocalPlayerDuringTransition",
                    "shouldSkipLocalAvatarForCameraCollision", "hasCustomCamera"}) method(camera, name, "()Z");
            method(camera, "localAvatarAlpha", "()F");
            method(camera, "turnFreeCamera", "(DD)Z");
            method(camera, "updateActivationTransition", "(F)V");
            method(camera, "updateSpyglassCameraTransition", "(Z)V");
            method(camera, "toggle", "()V");
            method(camera, "toggleFreeCamera", "()V");
            type(moves, "punchymoves/client/compat/spatial/SpatialCameraEntry");
            method(type(moves, "punchymoves/client/camera/CameraTargeting"), "pick",
                    "(Lnet/minecraft/client/Minecraft;FLnet/minecraft/world/phys/Vec3;Lorg/joml/Vector3fc;)V");
            method(type(moves, "punchymoves/client/render/AvatarRenderStateTracker"), "applyBodyRotationBlend",
                    "(Lnet/minecraft/client/player/AbstractClientPlayer;Lpunchymoves/client/PlayerPoseState;F)V");

            // Expressions is a bundled mod now; the facial layer keeps its compatible class name.
            ZipEntry expressions = moves.stream().filter(e -> e.getName().endsWith(".jar")
                    && e.getName().contains("punchy-expressions")).findFirst().orElseThrow();
            boolean found = false;
            try (ZipInputStream nested = new ZipInputStream(moves.getInputStream(expressions))) {
                for (ZipEntry entry; (entry = nested.getNextEntry()) != null;) {
                    if (!entry.getName().equals("punchymoves/client/face/FaceLayer.class")) continue;
                    ClassNode face = type(nested.readAllBytes());
                    check(face.superName.equals("net/minecraft/client/renderer/entity/layers/RenderLayer"),
                            "Expressions must retain the compatible Minecraft facial render layer");
                    found = true;
                    break;
                }
            }
            check(found, "The bundled Expressions JAR must contain the facial attachment target");
        }
        System.out.println("PASS: " + checks + " upstream API, mixin target and nested facial-layer checks");
    }

    private static ClassNode localType(String name) throws IOException {
        try (var stream = UpstreamContractTest.class.getClassLoader().getResourceAsStream(name + ".class")) {
            check(stream != null, "Missing mapped Minecraft target " + name);
            return type(stream.readAllBytes());
        }
    }

    private static ClassNode type(ZipFile jar, String name) throws IOException {
        ZipEntry entry = jar.getEntry(name + ".class");
        check(entry != null, "Missing upstream class " + name);
        return type(jar.getInputStream(entry).readAllBytes());
    }

    private static ClassNode type(byte[] bytes) {
        ClassNode node = new ClassNode();
        new ClassReader(bytes).accept(node, 0);
        return node;
    }

    private static MethodNode method(ClassNode type, String name, String descriptor) {
        MethodNode method = type.methods.stream().filter(m -> m.name.equals(name) && m.desc.equals(descriptor))
                .findFirst().orElse(null);
        check(method != null, type.name + "." + name + descriptor);
        return method;
    }

    private static void field(ClassNode type, String name, String descriptor) {
        check(type.fields.stream().anyMatch(f -> f.name.equals(name) && f.desc.equals(descriptor)),
                type.name + "." + name + ":" + descriptor);
    }

    private static void check(boolean condition, String detail) {
        checks++;
        if (!condition) throw new AssertionError(detail);
    }
}
