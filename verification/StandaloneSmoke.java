package nl.tnk.smoke;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import nl.tnk.combatpunchy.CombatModeController;
import nl.tnk.combatpunchy.WeaponEligibility;
import nl.tnk.combatpunchy.compat.RenderOwnership;
import org.slf4j.LoggerFactory;
import punchy.config.PunchyConfig;
import punchymoves.client.PlayerAnimationEngine;
import punchymoves.client.camera.ShoulderCameraController;
import punchymoves.client.camera.ShoulderCameraSettings;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mod(value="tnksmoke", dist=Dist.CLIENT)
@EventBusSubscriber(modid="tnksmoke", value=Dist.CLIENT)
public class StandaloneSmoke {
    private static boolean started;
    private static int tick;
    private static boolean observedNativeHands;
    private static boolean observedPunchySuppression;
    private static boolean finished;

    @SubscribeEvent
    public static void screen(ScreenEvent.Opening event) {
        if (!started && event.getNewScreen() instanceof TitleScreen) {
            started=true;
            Minecraft client=Minecraft.getInstance();
            client.execute(() -> client.createWorldOpenFlows().createFreshLevel("TNKStandaloneSmoke",
                    new LevelSettings("TNKStandaloneSmoke", GameType.CREATIVE, false, Difficulty.PEACEFUL,
                            true, new GameRules(), WorldDataConfiguration.DEFAULT),
                    new WorldOptions(1234L,false,false),
                    access -> access.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(WorldPresets.FLAT)
                            .value().createWorldDimensions(), null));
        }
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        Minecraft c=Minecraft.getInstance();
        if (finished || c.player==null || c.level==null) return;
        try {
            if (c.screen!=null) c.setScreen(null);
            tick++;
            var patch=EpicFightCapabilities.getLocalPlayerPatch(c.player);
            if (patch==null) return;
            if (tick>=42 && tick<=55) {
                boolean nativeHands=RenderOwnership.epicFirstPerson();
                observedNativeHands |= nativeHands;
                observedPunchySuppression |= nativeHands && !PunchyConfig.isModEnabled();
            }
            switch(tick) {
                case 20 -> {
                    var info=net.neoforged.fml.ModList.get().getModContainerById("combatpunchy").orElseThrow().getModInfo();
                    check(info.getDisplayName().equals("Combat Punchy"),"loader display name is Combat Punchy");
                    check(info.getVersion().toString().equals("1.0"),"loader version is 1.0");
                    check(WeaponEligibility.isCombatWeapon(new ItemStack(Items.DIAMOND_SWORD)),"sword classified");
                    for (Item item : new Item[]{Items.DIAMOND_PICKAXE,Items.DIAMOND_SHOVEL,Items.DIAMOND_HOE,
                            Items.FISHING_ROD,Items.BOW,Items.SHIELD,Items.STONE})
                        check(!WeaponEligibility.isCombatWeapon(new ItemStack(item)),"utility excluded: "+BuiltInRegistries.ITEM.getKey(item));
                    for (String name : new String[]{"agony","antitheus","satsujin","moonless","iron_staff","jabberwocky"})
                        check(WeaponEligibility.isCombatWeapon(new ItemStack(wom(name))),"WOM weapon classified: "+name);
                    equip(Items.DIAMOND_SWORD);
                    log("CLASSIFICATION PASSED");
                }
                case 35 -> {
                    check(patch.isVanillaMode(),"equipping stays vanilla");
                    ShoulderCameraController.setEnabledFromEditor(true);
                    check(ShoulderCameraController.isActive(),"Moves camera active outside combat");
                    ShoulderCameraController.toggleFreeCamera();
                    check(ShoulderCameraController.isFreeCameraActive(),"Moves orbit works outside combat");
                }
                case 42 -> attack(true);
                case 43 -> attack(false);
                case 46 -> {
                    check(patch.isEpicFightMode(),"first physical attack click enters combat");
                    PlayerAnimationEngine.crouchRenderAmount(c.player,0.0F);
                    check(PlayerAnimationEngine.isExternalLocomotion(c.player),"Moves yields through native handoff");
                    check(!ShoulderCameraController.isActive(),"Moves camera yields in combat");
                    check(!ShoulderCameraController.isFreeCameraActive(),"orbit yields in combat");
                    check(ShoulderCameraSettings.enabled(),"saved Moves camera preference preserved");
                    var cameraType=c.options.getCameraType();
                    ShoulderCameraController.toggle();
                    ShoulderCameraController.toggleFreeCamera();
                    check(c.options.getCameraType()==cameraType,"Moves hotkeys cannot steal the combat perspective");
                    nl.tnk.combatpunchy.CombatPunchyMod.MOVES_CAMERA_BRIDGE.set(false);
                    check(!RenderOwnership.epicCamera(),"Moves camera handoff can be disabled");
                    check(RenderOwnership.epicBody(c.player),"disabling Moves camera keeps its body handoff");
                    check(ShoulderCameraController.isActive(),"Moves camera remains usable with its bridge disabled");
                    nl.tnk.combatpunchy.CombatPunchyMod.MOVES_CAMERA_BRIDGE.set(true);
                    check(!ShoulderCameraController.isActive(),"re-enabled Moves camera handoff yields again");
                    nl.tnk.combatpunchy.CombatPunchyMod.MOVES_BRIDGE.set(false);
                    check(!RenderOwnership.epicBody(c.player),"Moves body bridge can be disabled");
                    check(!RenderOwnership.epicCamera(),"Moves master bridge also disables its camera handoff");
                    nl.tnk.combatpunchy.CombatPunchyMod.MOVES_BRIDGE.set(true);
                    nl.tnk.combatpunchy.CombatPunchyMod.PUNCHY_BRIDGE.set(false);
                    check(!RenderOwnership.filterFirstPerson(),"Punchy bridge can be disabled independently");
                    check(RenderOwnership.epicBody(c.player),"disabling Punchy keeps Moves body handoff");
                    nl.tnk.combatpunchy.CombatPunchyMod.PUNCHY_BRIDGE.set(true);
                    log("FIRST ATTACK AND MOVES HANDOFF PASSED");
                }
                case 58 -> {
                    check(observedNativeHands,"native combat pose received first-person ownership");
                    check(observedPunchySuppression,"Punchy API suppressed its hands during the native pose");
                    check(!RenderOwnership.epicFirstPerson(),"idle combat releases native first-person hands");
                    check(PunchyConfig.isModEnabled(),"Punchy resumes between attacks while still in combat");
                }
                case 55 -> {
                    patch.getSkill(yesman.epicfight.skill.SkillSlots.GUARD)
                            .setSkill(yesman.epicfight.registry.entries.EpicFightSkills.GUARD.get());
                    c.getSingleplayerServer().execute(() -> {
                        var sp=c.getSingleplayerServer().getPlayerList().getPlayer(c.player.getUUID());
                        EpicFightCapabilities.getServerPlayerPatch(sp).getSkill(yesman.epicfight.skill.SkillSlots.GUARD)
                                .setSkill(yesman.epicfight.registry.entries.EpicFightSkills.GUARD.get());
                    });
                }
                case 59 -> {
                    net.minecraft.client.KeyMapping.set(EpicFightKeyMappings.GUARD.getKey(),true);
                    yesman.epicfight.client.events.engine.ControlEngine.getInstance().handleEpicFightKeyMappings();
                }
                case 61 -> {
                    check(RenderOwnership.epicFirstPerson(),"blocking reacquires native first-person hands");
                    check(!PunchyConfig.isModEnabled(),"Punchy yields during blocking");
                }
                case 62 -> {
                    net.minecraft.client.KeyMapping.set(EpicFightKeyMappings.GUARD.getKey(),false);
                }
                case 66 -> {
                    equip(Items.FISHING_ROD);
                }
                case 71 -> {
                    check(patch.isVanillaMode(),"fishing rod exits combat");
                    check(!RenderOwnership.epicFirstPerson(),"hands release immediately for utilities");
                    check(PunchyConfig.isModEnabled(),"Punchy enabled again without Epic Tweaks");
                    PlayerAnimationEngine.crouchRenderAmount(c.player,0.0F);
                    check(!PlayerAnimationEngine.isExternalLocomotion(c.player),"Moves body restored");
                    check(ShoulderCameraController.isActive(),"Moves camera restored");
                    check(!ShoulderCameraController.isFreeCameraActive(),"orbit cannot resume with a stale look");
                    equip(wom("antitheus"));
                }
                case 78 -> {
                    check(patch.isVanillaMode(),"WOM re-equip stays vanilla");
                    attack(true);
                }
                case 79 -> attack(false);
                case 84 -> {
                    check(patch.isEpicFightMode(),"WOM first attack enters combat");
                    equip(Items.DIAMOND_PICKAXE);
                }
                case 90 -> {
                    check(patch.isVanillaMode(),"pickaxe exits WOM combat");
                    equip(Items.DIAMOND_SWORD);
                }
                case 96 -> {
                    check(patch.isVanillaMode(),"second sword equip stays vanilla");
                    attack(true);
                }
                case 97 -> attack(false);
                case 102 -> {
                    check(patch.isEpicFightMode(),"weapon-tool-weapon loop re-enters correctly");
                    equip(Items.AIR);
                }
                case 109 -> {
                    check(patch.isVanillaMode(),"empty hand exits combat");
                    check(nl.tnk.combatpunchy.CombatPunchyMod.COMBAT_TIMEOUT_SECONDS.get()==7,
                            "default combat timeout is seven seconds");
                    nl.tnk.combatpunchy.CombatPunchyMod.COMBAT_TIMEOUT_SECONDS.set(2);
                    equip(Items.DIAMOND_SWORD);
                }
                case 115 -> attack(true);
                case 116 -> attack(false);
                case 130 -> check(patch.isEpicFightMode(),"timeout does not exit early");
                case 145 -> attack(true);
                case 146 -> attack(false);
                case 170 -> check(patch.isEpicFightMode(),"another attack refreshes the timeout");
                case 210 -> {
                    check(patch.isVanillaMode(),"idle timeout exits while the weapon remains selected");
                    check(PunchyConfig.isModEnabled(),"Punchy resumes after timer expiry");
                    PlayerAnimationEngine.crouchRenderAmount(c.player,0.0F);
                    check(!PlayerAnimationEngine.isExternalLocomotion(c.player),"Moves body resumes after timer expiry");
                    check(ShoulderCameraController.isActive(),"Moves camera resumes after timer expiry");
                }
                case 215 -> attack(true);
                case 216 -> attack(false);
                case 220 -> check(patch.isEpicFightMode(),"first attack after timeout re-enters combat");
                case 225 -> {
                    net.minecraft.client.KeyMapping.set(EpicFightKeyMappings.GUARD.getKey(),true);
                    yesman.epicfight.client.events.engine.ControlEngine.getInstance().handleEpicFightKeyMappings();
                }
                case 280 -> check(patch.isEpicFightMode(),"holding guard prevents timeout beyond its deadline");
                case 281 -> net.minecraft.client.KeyMapping.set(EpicFightKeyMappings.GUARD.getKey(),false);
                case 335 -> check(patch.isVanillaMode(),"timer expires after guard is released");
                case 340 -> attack(true);
                case 341 -> attack(false);
                case 345 -> {
                    check(patch.isEpicFightMode(),"combat can be re-entered after a second timeout");
                    equip(Items.AIR);
                }
                case 350 -> {
                    check(patch.isVanillaMode(),"item switching still exits after timeout cycles");
                    nl.tnk.combatpunchy.CombatPunchyMod.AUTO_ENTER_COMBAT.set(false);
                    equip(Items.DIAMOND_SWORD);
                }
                case 356 -> attack(true);
                case 357 -> attack(false);
                case 360 -> {
                    check(patch.isVanillaMode(),"disabled auto-entry leaves weapon attack in vanilla");
                    check(!CombatModeController.acceptWeaponAttackInVanilla(),"disabled auto-entry closes the combat key gate");
                    nl.tnk.combatpunchy.CombatPunchyMod.AUTO_ENTER_COMBAT.set(true);
                    attack(true);
                }
                case 361 -> attack(false);
                case 365 -> {
                    check(patch.isEpicFightMode(),"re-enabled auto-entry works on the next attack");
                    nl.tnk.combatpunchy.CombatPunchyMod.AUTO_EXIT_ON_ITEM_SWITCH.set(false);
                    equip(Items.FISHING_ROD);
                }
                case 370 -> {
                    check(patch.isEpicFightMode(),"disabled item exit leaves manual combat active for a utility");
                    nl.tnk.combatpunchy.CombatPunchyMod.AUTO_EXIT_ON_ITEM_SWITCH.set(true);
                    CombatModeController.beforeInput();
                    check(patch.isVanillaMode(),"re-enabled item exit immediately restores utility mode");
                    equip(Items.DIAMOND_SWORD);
                }
                case 376 -> attack(true);
                case 377 -> attack(false);
                case 385 -> nl.tnk.combatpunchy.CombatPunchyMod.AUTO_EXIT_ON_TIMEOUT.set(false);
                case 445 -> {
                    check(patch.isEpicFightMode(),"disabled timeout keeps combat past the configured deadline");
                    nl.tnk.combatpunchy.CombatPunchyMod.AUTO_EXIT_ON_TIMEOUT.set(true);
                }
                case 450 -> check(patch.isEpicFightMode(),"re-enabled timeout starts a fresh countdown");
                case 505 -> {
                    check(patch.isVanillaMode(),"re-enabled timeout eventually returns to vanilla");
                    nl.tnk.combatpunchy.CombatPunchyMod.ENABLED.set(false);
                    patch.toEpicFightMode(true);
                    equip(Items.FISHING_ROD);
                }
                case 510 -> {
                    check(patch.isEpicFightMode(),"master switch disables automatic mode changes");
                    check(!RenderOwnership.filterFirstPerson(),"master switch disables Punchy bridge");
                    check(!RenderOwnership.movesBridge(),"master switch disables Moves bridge");
                    log("TNK_STANDALONE_SMOKE_PASS");
                    finished=true;
                    c.stop();
                }
            }
        } catch(Throwable e) {
            finished=true;
            LoggerFactory.getLogger("TNKSmoke").error("TNK_STANDALONE_SMOKE_FAIL tick="+tick,e);
            c.stop();
        }
    }

    private static Item wom(String name) {
        ResourceLocation id=ResourceLocation.fromNamespaceAndPath("wom",name);
        if (!BuiltInRegistries.ITEM.containsKey(id)) throw new AssertionError("WOM item missing: "+id);
        return BuiltInRegistries.ITEM.get(id);
    }
    private static void equip(Item item) {
        Minecraft c=Minecraft.getInstance();
        c.player.getInventory().selected=0;
        c.player.getInventory().setItem(0,new ItemStack(item));
        if(c.getSingleplayerServer()!=null) c.getSingleplayerServer().execute(() -> {
            var player=c.getSingleplayerServer().getPlayerList().getPlayer(c.player.getUUID());
            if(player!=null) {
                player.getInventory().selected=0;
                player.getInventory().setItem(0,new ItemStack(item));
                player.containerMenu.broadcastChanges();
            }
        });
        CombatModeController.beforeInput();
    }
    private static void attack(boolean pressed) {
        var key=EpicFightKeyMappings.ATTACK.getKey();
        Minecraft c=Minecraft.getInstance();
        if(pressed) log("ATTACK DEBUG accepted="+CombatModeController.acceptWeaponAttackInVanilla()
                +" matches="+EpicFightKeyMappings.ATTACK.isActiveAndMatches(key)
                +" paused="+c.isPaused()+" screen="+c.screen+" target="+c.hitResult
                +" weapon="+c.player.getMainHandItem());
        net.minecraft.client.KeyMapping.set(key,pressed);
        if(pressed) {
            // Supply a deterministic air attack, independent of the test camera's block pick.
            c.hitResult=net.minecraft.world.phys.BlockHitResult.miss(c.player.getEyePosition(),
                    net.minecraft.core.Direction.UP,c.player.blockPosition());
            net.minecraft.client.KeyMapping.click(key);
            yesman.epicfight.client.events.engine.ControlEngine.getInstance().handleEpicFightKeyMappings();
        }
    }
    private static void check(boolean okay,String label) {
        if(!okay) throw new AssertionError(label);
        log("PASS: "+label);
    }
    private static void log(String message) {LoggerFactory.getLogger("TNKSmoke").info(message);}
}
