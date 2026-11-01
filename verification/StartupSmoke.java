package nl.tnk.smoke;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ScreenEvent;
import nl.tnk.combatpunchy.compat.IntegrationPresence;
import nl.tnk.combatpunchy.compat.RenderOwnership;
import org.slf4j.LoggerFactory;

@Mod(value="tnkstartup",dist=Dist.CLIENT)
@EventBusSubscriber(modid="tnkstartup",value=Dist.CLIENT)
public class StartupSmoke {
    @SubscribeEvent
    public static void screen(ScreenEvent.Opening event) {
        if(event.getNewScreen() instanceof TitleScreen) {
            try {
                Class.forName("yesman.epicfight.client.events.engine.RenderEngine");
                Class.forName("yesman.epicfight.client.input.CombatKeyMapping");
                if(IntegrationPresence.MOVES) {
                    for(String name:new String[]{"punchymoves.client.PlayerAnimationEngine",
                            "punchymoves.client.camera.ShoulderCameraController",
                            "punchymoves.client.camera.CameraTargeting",
                            "punchymoves.client.render.AvatarRenderStateTracker"}) Class.forName(name);
                }
                if(IntegrationPresence.PUNCHY) Class.forName("punchy.config.PunchyConfig")
                        .getMethod("isModEnabled").invoke(null);
                RenderOwnership.epicFirstPerson();
                RenderOwnership.epicCamera();
                LoggerFactory.getLogger("TNKSmoke").info("TNK_OPTIONAL_STARTUP_PASS punchy={} api={} moves={}",
                        IntegrationPresence.PUNCHY,IntegrationPresence.PUNCHY_API,IntegrationPresence.MOVES);
            } catch(Throwable e) {
                LoggerFactory.getLogger("TNKSmoke").error("TNK_OPTIONAL_STARTUP_FAIL",e);
            }
            Minecraft.getInstance().execute(Minecraft.getInstance()::stop);
        }
    }
}
