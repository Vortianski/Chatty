package xox.labvorty.chatty.init;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import xox.labvorty.chatty.data.handler.VoiceSoundRegistryReloadEvent;
import xox.labvorty.chatty.data.network.ChatBubblePacket;
import xox.labvorty.chatty.data.network.VoiceSettingsPacket;
import xox.labvorty.vortylib.VortyLib;

@EventBusSubscriber
public class ChattyInitialization {
    @SubscribeEvent
    public static void common(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            VortyLib.addNetworkMessage(
                    ChatBubblePacket.TYPE,
                    ChatBubblePacket.CODEC,
                    ChatBubblePacket::handle
            );

            VortyLib.addNetworkMessage(
                    VoiceSettingsPacket.TYPE,
                    VoiceSettingsPacket.CODEC,
                    VoiceSettingsPacket::handle
            );
        });
    }

    @SubscribeEvent
    public static void registerSounds(VoiceSoundRegistryReloadEvent event) {
        event.addSound(
                ResourceLocation.fromNamespaceAndPath("minecraft", "bell"),
                Component.literal("Bell"),
                SoundEvents.NOTE_BLOCK_BELL.value()
        );

        event.addSound(
                ResourceLocation.fromNamespaceAndPath("minecraft", "chime"),
                Component.literal("Chime"),
                SoundEvents.NOTE_BLOCK_CHIME.value()
        );

        event.addSound(
                ResourceLocation.fromNamespaceAndPath("minecraft", "flute"),
                Component.literal("Flute"),
                SoundEvents.NOTE_BLOCK_FLUTE.value()
        );

        event.addSound(
                ResourceLocation.fromNamespaceAndPath("minecraft", "guitar"),
                Component.literal("Guitar"),
                SoundEvents.NOTE_BLOCK_GUITAR.value()
        );

        event.addSound(
                ResourceLocation.fromNamespaceAndPath("minecraft", "pling"),
                Component.literal("Pling"),
                SoundEvents.NOTE_BLOCK_PLING.value()
        );

        event.addSound(
                ResourceLocation.fromNamespaceAndPath("minecraft", "banjo"),
                Component.literal("Banjo"),
                SoundEvents.NOTE_BLOCK_BANJO.value()
        );

        event.addSound(
                ResourceLocation.fromNamespaceAndPath("minecraft", "bass"),
                Component.literal("Bass"),
                SoundEvents.NOTE_BLOCK_BASS.value()
        );

        event.addSound(
                ResourceLocation.fromNamespaceAndPath("minecraft", "didgeridoo"),
                Component.literal("Didgeridoo"),
                SoundEvents.NOTE_BLOCK_DIDGERIDOO.value()
        );

        event.addSound(
                ResourceLocation.fromNamespaceAndPath("minecraft", "basedrum"),
                Component.literal("Basedrum"),
                SoundEvents.NOTE_BLOCK_BASEDRUM.value()
        );

        event.addSound(
                ResourceLocation.fromNamespaceAndPath("minecraft", "xylophone"),
                Component.literal("Xylophone"),
                SoundEvents.NOTE_BLOCK_XYLOPHONE.value()
        );

        event.addSound(
                ResourceLocation.fromNamespaceAndPath("minecraft", "iron_xylophone"),
                Component.literal("Iron Xylophone"),
                SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value()
        );
    }
}
