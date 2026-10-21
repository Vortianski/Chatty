package xox.labvorty.chatty.data.handler;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import xox.labvorty.chatty.data.handler.VoiceSoundRegistry;
import xox.labvorty.chatty.data.network.ChatBubblePacket;
import xox.labvorty.chatty.data.network.VoiceSettingsPacket;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber
public final class ChatBubbles {
    private static final VoiceSettingsPacket DEFAULT_SETTINGS = new VoiceSettingsPacket(0xFFFFFF, VoiceSoundRegistry.DEFAULT_ID, 1.0f, 1);
    private static final Map<UUID, VoiceSettingsPacket> SETTINGS = new ConcurrentHashMap<>();

    private ChatBubbles() {
    }

    public static void setSettings(UUID id, VoiceSettingsPacket settings) {
        SETTINGS.put(id, settings);
    }

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        String text = event.getRawText().strip();
        if (text.isEmpty()) {
            return;
        }

        ServerPlayer player = event.getPlayer();
        VoiceSettingsPacket settings = SETTINGS.getOrDefault(player.getUUID(), DEFAULT_SETTINGS);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                new ChatBubblePacket(
                        player.getId(),
                        text,
                        settings.color(),
                        settings.sound(),
                        settings.pitch(),
                        settings.frequency()
                )
        );
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        SETTINGS.remove(event.getEntity().getUUID());
    }
}