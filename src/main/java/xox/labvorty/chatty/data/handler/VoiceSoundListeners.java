package xox.labvorty.chatty.data.handler;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber
public final class VoiceSoundListeners {
    @SubscribeEvent
    public static void onRegister(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new VoiceSoundReloadListener());
    }
}