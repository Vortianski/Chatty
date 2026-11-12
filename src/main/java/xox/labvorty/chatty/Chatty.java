package xox.labvorty.chatty;

import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import xox.labvorty.chatty.data.ClientConfig;
import xox.labvorty.chatty.init.ChattySoundEvents;

@Mod(Chatty.MOD_ID)
public class Chatty {
    public static final String MOD_ID = "chatty";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Chatty(IEventBus modEventBus, ModContainer modContainer) {
        ChattySoundEvents.SOUND_EVENTS.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
