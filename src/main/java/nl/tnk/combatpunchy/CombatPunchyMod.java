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
    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.BooleanValue AUTO_ENTER_COMBAT;
    public static final ForgeConfigSpec.BooleanValue AUTO_ENTER_ON_HIT;
    public static final ForgeConfigSpec.BooleanValue AUTO_EXIT_ON_ITEM_SWITCH;
    public static final ForgeConfigSpec.BooleanValue START_IN_VANILLA_MODE;
    public static final ForgeConfigSpec.BooleanValue AUTO_EXIT_ON_TIMEOUT;
    public static final ForgeConfigSpec.BooleanValue ENTER_WHILE_TARGETING_BLOCK;
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
        AUTO_EXIT_ON_ITEM_SWITCH = builder.comment("Return to vanilla when selecting a tool, non-melee item or empty hand.")
                .define("auto_exit_on_item_switch", true);
        START_IN_VANILLA_MODE = builder.comment("Start each world/player session in vanilla, even if the server starts you in combat.")
                .define("start_in_vanilla_mode", true);
        AUTO_EXIT_ON_TIMEOUT = builder.comment("Return to vanilla after combat inactivity; uses combat_timeout_seconds.")
                .define("auto_exit_on_timeout", true);
        ENTER_WHILE_TARGETING_BLOCK = builder.comment(
                "Also enter combat when pressing attack while looking at a block.",
                "False keeps normal block mining/chopping in vanilla until you attack an entity or swing into air.")
                .define("enter_while_targeting_block", false);
        PUNCHY_BRIDGE = builder.comment("Let Punchy render normal first-person actions; Epic Fight renders combat poses.")
                .define("punchy_first_person_bridge", true);
        MOVES_BRIDGE = builder.comment("Let Moves yield body animation, targeting and camera control to Epic Fight.")
                .define("moves_third_person_bridge", true);
        MOVES_CAMERA_BRIDGE = builder.comment(
                "Suspend Moves camera, orbit and targeting while Epic Fight owns the player model.",
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
                "Return to vanilla after this many seconds without combat activity. Zero disables the timeout.",
                "Attacks, active combat animations/skills, guarding and receiving hits refresh the timer.",
                "Uses player ticks; pausing does not consume the timer.")
                .defineInRange("combat_timeout_seconds", 7, 0, 300);
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
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            PunchyApiCompat.register();
            MovesApiCompat.register();
        }));
    }
}
