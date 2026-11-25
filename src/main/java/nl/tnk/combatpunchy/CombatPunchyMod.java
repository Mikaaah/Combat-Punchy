package nl.tnk.combatpunchy;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import nl.tnk.combatpunchy.compat.PunchyApiCompat;
import nl.tnk.combatpunchy.compat.MovesApiCompat;
import nl.tnk.combatpunchy.compat.MovesFaceCompat;

@Mod(CombatPunchyMod.MOD_ID)
public final class CombatPunchyMod {
    public static final String MOD_ID = "combatpunchy";
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue BACK_WEAPONS;
    public static final ForgeConfigSpec.DoubleValue BACK_OFFSET;
    public static final ForgeConfigSpec.DoubleValue BACK_SCALE;
    public static final ForgeConfigSpec.DoubleValue BACK_ROTATION, BACK_PITCH, BACK_YAW;
    public static final ForgeConfigSpec.DoubleValue BACK_SHIELD_SCALE, BACK_SHIELD_ROTATION;
    public static final ForgeConfigSpec.ConfigValue<java.util.List<? extends String>> BACK_ITEM_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.BooleanValue AUTO_ENTER_COMBAT;
    public static final ForgeConfigSpec.BooleanValue AUTO_ENTER_ON_HIT;
    public static final ForgeConfigSpec.BooleanValue AUTO_ENTER_ON_COMBAT_ACTION;
    public static final ForgeConfigSpec.BooleanValue AUTO_EXIT_ON_ITEM_SWITCH;
    public static final ForgeConfigSpec.BooleanValue START_IN_VANILLA_MODE;
    public static final ForgeConfigSpec.BooleanValue AUTO_EXIT_ON_TIMEOUT;
    public static final ForgeConfigSpec.BooleanValue ENTER_WHILE_TARGETING_BLOCK;
    public static final ForgeConfigSpec.BooleanValue COMBAT_MINING_RESTRICTION;
    public static final ForgeConfigSpec.BooleanValue PUNCHY_BRIDGE;
    public static final ForgeConfigSpec.BooleanValue MOVES_BRIDGE;
    public static final ForgeConfigSpec.BooleanValue MOVES_CAMERA_BRIDGE;
    public static final ForgeConfigSpec.BooleanValue MOVES_FACIAL_BRIDGE;
    public static final ForgeConfigSpec.BooleanValue NATIVE_STANCES;
    public static final ForgeConfigSpec.IntValue FIRST_PERSON_RELEASE_TICKS;
    public static final ForgeConfigSpec.IntValue COMBAT_TIMEOUT_SECONDS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        ENABLED = builder.comment("Master switch for all Combat Punchy automatic mode changes and compatibility handoffs.")
                .define("enabled", true);
        AUTO_ENTER_COMBAT = builder.comment("Enter combat when attacking with an eligible Epic Fight melee weapon.")
                .define("auto_enter_combat", true);
        AUTO_ENTER_ON_HIT = builder.comment(
                "Enter combat when the server reports a hit from another entity, including projectiles.",
                "Works with tools and empty hands; ignores self-attributed hits and damage without a source entity, such as falling or fire.",
                "The server has already processed the triggering hit, so its damage is not changed retroactively.")
                .define("auto_enter_on_hit", true);
        AUTO_ENTER_ON_COMBAT_ACTION = builder.comment(
                "Enter combat when using an equipped Epic Fight dodge, guard, mobility or innate skill, or when starting to block with a shield.")
                .define("auto_enter_on_combat_action", true);
        AUTO_EXIT_ON_ITEM_SWITCH = builder.comment(
                "Start a fresh combat_timeout_seconds grace period when swapping from a melee weapon to a non-weapon in combat.")
                .define("auto_exit_on_item_switch", true);
        START_IN_VANILLA_MODE = builder.comment("Start each world/player session in vanilla, even if the server starts you in combat.")
                .define("start_in_vanilla_mode", true);
        AUTO_EXIT_ON_TIMEOUT = builder.comment("Return to vanilla after combat inactivity; uses combat_timeout_seconds.")
                .define("auto_exit_on_timeout", true);
        ENTER_WHILE_TARGETING_BLOCK = builder.comment(
                "Also enter combat when pressing attack while looking at a block.",
                "False keeps normal block mining/chopping in vanilla until you attack an entity or swing into air.")
                .define("enter_while_targeting_block", false);
        COMBAT_MINING_RESTRICTION = builder.comment(
                "Only axes and pickaxes may break blocks in Epic Fight combat mode.",
                "Other items attack instead of starting block mining. Normal vanilla-mode mining is unaffected.")
                .define("combat_mining_axes_pickaxes_only", true);
        PUNCHY_BRIDGE = builder.comment("Let Punchy render normal first-person actions; Epic Fight renders combat poses.")
                .define("punchy_first_person_bridge", true);
        MOVES_BRIDGE = builder.comment("Give Epic Fight control of Moves locomotion and body animation throughout combat.")
                .define("moves_third_person_bridge", true);
        MOVES_CAMERA_BRIDGE = builder.comment(
                "Suspend Moves camera, orbit and targeting during Epic Fight combat.",
                "Requires moves_third_person_bridge. Disable to keep Moves camera control during combat.")
                .define("moves_camera_bridge", true);
        MOVES_FACIAL_BRIDGE = builder.comment(
                "Attach Moves animated eyes, brows and facial effects to Epic Fight's animated head.",
                "Prevents displaced/duplicate facial features during combat. Requires moves_third_person_bridge.")
                .define("moves_facial_bridge", true);
        NATIVE_STANCES = builder.comment("Preserve Epic Fight's sheathed-katana and Ochs guard poses in first person.")
                .define("preserve_native_stances", true);
        FIRST_PERSON_RELEASE_TICKS = builder.comment("Hold Epic Fight's first-person renderer briefly after an action to avoid flicker.")
                .defineInRange("first_person_release_ticks", 2, 0, 5);
        COMBAT_TIMEOUT_SECONDS = builder.comment(
                "Return to vanilla after this many seconds without combat activity. Zero disables both inactivity and item-switch exit timers.",
                "Attacks, active combat animations/skills, guarding and receiving hits refresh the timer.",
                "Uses player ticks; pausing does not consume the timer.")
                .defineInRange("combat_timeout_seconds", 7, 0, 300);
        BACK_WEAPONS = builder.comment("Cosmetic back display for your offhand outside combat and last selected hotbar weapon while using tools/blocks.",
                "Client-local only. Does not move inventory items; axes, mining tools and fists/gloves stay in hand.")
                .define("back_weapons", true);
        BACK_OFFSET = builder.comment("Distance behind the torso, in blocks. Increase for bulky armor or backpacks.")
                .defineInRange("back_weapon_offset", 0.22, 0.13, 0.8);
        BACK_SCALE = builder.comment("Default scale of back weapons; individual items can override it below.")
                .defineInRange("back_weapon_scale", 0.85, 0.1, 3.0);
        BACK_ROTATION = builder.comment("Weapon tilt in degrees, mirrored on each side. Positive values bring handles closer to the head.")
                .defineInRange("back_weapon_rotation", 6.0, -180.0, 180.0);
        BACK_PITCH = builder.comment("Extra front/back weapon rotation in degrees.")
                .defineInRange("back_weapon_pitch", 0.0, -180.0, 180.0);
        BACK_YAW = builder.comment("Extra rotation around the vertical axis in degrees.")
                .defineInRange("back_weapon_yaw", 0.0, -180.0, 180.0);
        BACK_SHIELD_SCALE = builder.comment("Scale of centered upright back shields, independent of weapon scale.")
                .defineInRange("back_shield_scale", 0.75, 0.1, 3.0);
        BACK_SHIELD_ROTATION = builder.comment("Rotation of centered shields in degrees. Zero keeps shields upright.")
                .defineInRange("back_shield_rotation", 0.0, -180.0, 180.0);
        BACK_ITEM_OVERRIDES = builder.comment("Per-item overrides: item_id|scale|rotation|pitch|yaw. Angles are degrees; scale is absolute, not multiplied.",
                "For weapons rotation is the inward tilt; for shields it is upright roll. Remaining angles adjust custom models.",
                "Example: minecraft:diamond_sword|0.8|10|0|0. Use actual registered item IDs; last duplicate wins.")
                .defineListAllowEmpty("back_item_overrides", java.util.List.<String>of(),
                        nl.tnk.combatpunchy.back.BackWeaponOptions::validOverride);
        SPEC = builder.build();
    }

    public CombatPunchyMod(FMLJavaModLoadingContext context) {
        if (FMLEnvironment.dist != Dist.CLIENT) return;
        for (String incompatible : new String[]{"tnkcombatmode", "epictweaks", "efpunchycompat"}) {
            if (ModList.get().isLoaded(incompatible)) {
                throw new IllegalStateException("Combat Punchy replaces " + incompatible + "; remove its JAR before launching.");
            }
        }
        IEventBus modBus = context.getModEventBus();
        context.registerConfig(ModConfig.Type.CLIENT, SPEC);
        MovesFaceCompat.register(modBus);
        nl.tnk.combatpunchy.back.BackWeaponRegistration.register(modBus);
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            PunchyApiCompat.register();
            MovesApiCompat.register();
        }));
    }
}
