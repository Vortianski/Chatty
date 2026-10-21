package xox.labvorty.chatty.data.handler;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Map;
import java.util.TreeMap;

public class VoiceSoundReloadListener extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static int sounds = 0;

    public VoiceSoundReloadListener() {
        super(new GsonBuilder().create(), "chatty");
    }

    @Override
    protected void apply(
            @NotNull Map<ResourceLocation, JsonElement> jsonElementMap,
            @NotNull ResourceManager resourceManager,
            @NotNull ProfilerFiller profilerFiller
    ) {
        LOGGER.info("Voice sound reload started, json files found: {}", jsonElementMap.size());

        VoiceSoundRegistry.clear();
        sounds = 0;

        for (Map.Entry<ResourceLocation, JsonElement> elementEntry : new TreeMap<>(jsonElementMap).entrySet()) {
            ResourceLocation id = elementEntry.getKey();

            try {
                JsonObject jsonObject = elementEntry.getValue().getAsJsonObject();
                if (!jsonObject.has("sound")) {
                    LOGGER.warn("Voice sound {} has no sound field", id);
                    continue;
                }

                ResourceLocation soundId = ResourceLocation.tryParse(jsonObject.get("sound").getAsString());
                if (soundId == null) {
                    LOGGER.warn("Voice sound {} has an invalid sound id", id);
                    continue;
                }

                Component label = jsonObject.has("name")
                        ? Component.literal(jsonObject.get("name").getAsString())
                        : Component.literal(id.getPath());

                if (VoiceSoundRegistry.add(id, label, SoundEvent.createVariableRangeEvent(soundId))) {
                    sounds++;
                }
            } catch (RuntimeException exception) {
                LOGGER.warn("Failed to read voice sound {}", id, exception);
            }
        }

        LOGGER.info("Voice sounds loaded from json: {}", sounds);
    }
}