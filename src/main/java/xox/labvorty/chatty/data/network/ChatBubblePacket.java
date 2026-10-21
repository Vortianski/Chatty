package xox.labvorty.chatty.data.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import xox.labvorty.chatty.data.handler.ClientChatBubbles;
import xox.labvorty.chatty.data.handler.VoiceProfile;
import xox.labvorty.chatty.data.handler.VoiceSoundRegistry;

public record ChatBubblePacket(
        int entityId,
        String text,
        int color,
        ResourceLocation sound,
        float pitch,
        int frequency
) implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Type<ChatBubblePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("chatty", "chat_bubble_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChatBubblePacket> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeVarInt(packet.entityId);
                buf.writeUtf(packet.text, 256);
                buf.writeInt(packet.color);
                buf.writeUtf(packet.sound.toString(), 128);
                buf.writeFloat(packet.pitch);
                buf.writeVarInt(packet.frequency);
            },
            (buf) -> {
                int entityId = buf.readVarInt();
                String text = buf.readUtf(256);
                int color = buf.readInt() & 0xFFFFFF;
                ResourceLocation sound = ResourceLocation.tryParse(buf.readUtf(128));
                if (sound == null) {
                    sound = VoiceSoundRegistry.DEFAULT_ID;
                }
                float pitch = VoiceProfile.sanitizePitch(buf.readFloat());
                int frequency = VoiceProfile.sanitizeFrequency(buf.readVarInt());

                return new ChatBubblePacket(entityId, text, color, sound, pitch, frequency);
            }
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final ChatBubblePacket payload, final IPayloadContext payloadContext) {
        ClientChatBubbles.show(
                payload.entityId(),
                payload.text(),
                payload.color(),
                payload.sound(),
                payload.pitch(),
                payload.frequency()
        );
    }
}