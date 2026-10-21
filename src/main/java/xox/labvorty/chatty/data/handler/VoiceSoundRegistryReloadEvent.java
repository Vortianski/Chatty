package xox.labvorty.chatty.data.handler;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.Event;
import org.slf4j.Logger;

public class VoiceSoundRegistryReloadEvent extends Event {
    private static final Logger LOGGER = LogUtils.getLogger();

    public boolean addSound(ResourceLocation id, Component label, SoundEvent event) {
        boolean reg = VoiceSoundRegistry.add(id, label, event);
        if (reg) {
            VoiceSoundReloadListener.sounds++;
        }
        LOGGER.info("Voice sound added via event: {} (registered: {})", id, reg);
        return reg;
    }
}