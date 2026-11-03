package nl.tnk.combatpunchy;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import nl.tnk.combatpunchy.compat.PunchyApiCompat;
import nl.tnk.combatpunchy.compat.MovesApiCompat;

@Mod(value = CombatPunchyMod.MOD_ID, dist = Dist.CLIENT)
public final class CombatPunchyMod {
    public static final String MOD_ID = "combatpunchy";
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLED;
    public static final ModConfigSpec.BooleanValue AUTO_ENTER_COMBAT;
    public static final ModConfigSpec.BooleanValue AUTO_EXIT_ON_ITEM_SWITCH;
    public static final ModConfigSpec.BooleanValue START_IN_VANILLA_MODE;
    public static final ModConfigSpec.BooleanValue AUTO_EXIT_ON_TIMEOUT;
    public static final ModConfigSpec.BooleanValue ENTER_WHILE_TARGETING_BLOCK;
    public static final ModConfigSpec.BooleanValue PUNCHY_BRIDGE;
    public static final ModConfigSpec.BooleanValue MOVES_BRIDGE;
    public static final ModConfigSpec.BooleanValue MOVES_CAMERA_BRIDGE;
    public static final ModConfigSpec.BooleanValue NATIVE_STANCES;
    public static final ModConfigSpec.IntValue FIRST_PERSON_RELEASE_TICKS;
    public static final ModConfigSpec.IntValue COMBAT_TIMEOUT_SECONDS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ENABLED = builder.comment("Master switch for all Combat Punchy automatic mode changes and compatibility handoffs.")
                .define("enabled", true);
        AUTO_ENTER_COMBAT = builder.comment("Enter combat when attacking with an eligible Epic Fight melee weapon.")
                .define("auto_enter_combat", true);
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

    public CombatPunchyMod(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, SPEC);
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            PunchyApiCompat.register();
            MovesApiCompat.register();
        }));
    }
}
