package xox.labvorty.chatty.data.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import xox.labvorty.chatty.data.handler.VoiceSoundRegistry;
import xox.labvorty.chatty.data.network.VoiceSettingsPacket;
import xox.labvorty.vortylib.data.network.FloatingTextPacket;
import xox.labvorty.vortylib.data.text.ClientFloatingTexts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT)
public final class ClientChatBubbles {
    private static final float BASE_SPEED = 0.75f;
    private static final int MAX_TYPE_TICKS = 100;
    private static final int HOLD_TICKS = 60;
    private static final float SOUND_VOLUME = 0.6f;
    private static final double HEAD_OFFSET = 0.5;
    private static final Map<Integer, Bubble> BUBBLES = new HashMap<>();
    private static ClientLevel lastLevel;

    private ClientChatBubbles() {
    }

    public static void sendSettings() {
        if (Minecraft.getInstance().getConnection() == null) {
            return;
        }
        PacketDistributor.sendToServer(new VoiceSettingsPacket(
                VoiceSettings.color(),
                VoiceSettings.sound(),
                VoiceSettings.pitch(),
                VoiceSettings.frequency()
        ));
    }

    public static void show(int entityId, String text, int color, ResourceLocation sound, float pitch, int frequency) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }

        Entity entity = level.getEntity(entityId);
        String trimmed = text.strip();
        if (entity == null || trimmed.isEmpty()) {
            return;
        }

        Bubble previous = BUBBLES.remove(entityId);
        if (previous != null) {
            ClientFloatingTexts.remove(previous.packet);
        }

        float speed = Math.max(BASE_SPEED, trimmed.length() / (float) MAX_TYPE_TICKS);
        int typeTicks = (int) Math.ceil(trimmed.length() / speed);

        Component component = Component.literal(trimmed).withStyle(Style.EMPTY.withColor(color & 0xFFFFFF));

        FloatingTextPacket packet = FloatingTextPacket.builder(component, Vec3.ZERO)
                .follow(entity, new Vec3(0.0, entity.getBbHeight() + HEAD_OFFSET, 0.0))
                .typewriter(speed)
                .followXRotation(true)
                .dropShadow(true)
                .lifetime(typeTicks + HOLD_TICKS)
                .fade(5, 15)
                .build();

        ClientFloatingTexts.add(packet);
        BUBBLES.put(entityId, new Bubble(
                packet,
                trimmed,
                VoiceSoundRegistry.get(sound).event(),
                VoiceProfile.of(entity.getUUID(), pitch),
                speed,
                VoiceProfile.sanitizeFrequency(frequency)
        ));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level != lastLevel) {
            BUBBLES.clear();
            lastLevel = level;
        }

        if (level == null || minecraft.isPaused() || BUBBLES.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<Integer, Bubble>> iterator = BUBBLES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Bubble> entry = iterator.next();
            Bubble bubble = entry.getValue();
            Entity entity = level.getEntity(entry.getKey());

            bubble.age++;
            if (entity == null || bubble.age >= bubble.packet.lifetime()) {
                iterator.remove();
                continue;
            }

            int revealed = Math.min(bubble.text.length(), (int) (bubble.age * bubble.speed));
            if (revealed <= bubble.soundIndex) {
                continue;
            }

            char spoken = 0;
            for (int i = bubble.soundIndex; i < revealed; i++) {
                char c = bubble.text.charAt(i);
                if (Character.isWhitespace(c)) {
                    continue;
                }
                bubble.visible++;
                if (bubble.visible % bubble.frequency == 0) {
                    spoken = c;
                }
            }
            bubble.soundIndex = revealed;

            if (spoken != 0) {
                level.playLocalSound(
                        entity.getX(),
                        entity.getEyeY(),
                        entity.getZ(),
                        bubble.sound,
                        SoundSource.PLAYERS,
                        SOUND_VOLUME,
                        bubble.profile.pitchFor(spoken),
                        false
                );
            }
        }
    }

    @SubscribeEvent
    public static void onLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        sendSettings();
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        BUBBLES.clear();
        lastLevel = null;
    }

    private static final class Bubble {
        final FloatingTextPacket packet;
        final String text;
        final SoundEvent sound;
        final VoiceProfile profile;
        final float speed;
        final int frequency;
        int age;
        int soundIndex;
        int visible;

        Bubble(FloatingTextPacket packet, String text, SoundEvent sound, VoiceProfile profile, float speed, int frequency) {
            this.packet = packet;
            this.text = text;
            this.sound = sound;
            this.profile = profile;
            this.speed = speed;
            this.frequency = frequency;
        }
    }
}