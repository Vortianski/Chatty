package xox.labvorty.chatty.data.handler;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT)
public final class VoiceKeys {
    public static final KeyMapping OPEN = new KeyMapping("key.chatty.voice_settings", GLFW.GLFW_KEY_V, "key.categories.chatty");

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        while (OPEN.consumeClick()) {
            if (minecraft.screen == null && minecraft.level != null) {
                minecraft.setScreen(new VoiceScreen(null));
            }
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static final class Register {
        @SubscribeEvent
        public static void onRegister(RegisterKeyMappingsEvent event) {
            event.register(OPEN);
        }
    }
}