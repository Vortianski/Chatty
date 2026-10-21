package xox.labvorty.chatty.data.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class VoiceSettings {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("chatty.json");

    private static int color = 0xFFFFFF;
    private static ResourceLocation sound = VoiceSoundRegistry.DEFAULT_ID;
    private static float pitch = 1.0f;
    private static int frequency = 1;

    static {
        load();
    }

    private VoiceSettings() {
    }

    public static int color() {
        return color;
    }

    public static ResourceLocation sound() {
        return sound;
    }

    public static float pitch() {
        return pitch;
    }

    public static int frequency() {
        return frequency;
    }

    public static void set(int newColor, ResourceLocation newSound, float newPitch, int newFrequency) {
        color = newColor & 0xFFFFFF;
        sound = newSound;
        pitch = VoiceProfile.sanitizePitch(newPitch);
        frequency = VoiceProfile.sanitizeFrequency(newFrequency);
        save();
    }

    private static void load() {
        if (!Files.exists(FILE)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(FILE)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json.has("color")) {
                color = json.get("color").getAsInt() & 0xFFFFFF;
            }
            if (json.has("sound")) {
                ResourceLocation parsed = ResourceLocation.tryParse(json.get("sound").getAsString());
                if (parsed != null) {
                    sound = parsed;
                }
            }
            if (json.has("pitch")) {
                pitch = VoiceProfile.sanitizePitch(json.get("pitch").getAsFloat());
            }
            if (json.has("frequency")) {
                frequency = VoiceProfile.sanitizeFrequency(json.get("frequency").getAsInt());
            }
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Failed to read voice settings", exception);
        }
    }

    private static void save() {
        JsonObject json = new JsonObject();
        json.addProperty("color", color);
        json.addProperty("sound", sound.toString());
        json.addProperty("pitch", pitch);
        json.addProperty("frequency", frequency);

        try (Writer writer = Files.newBufferedWriter(FILE)) {
            GSON.toJson(json, writer);
        } catch (IOException exception) {
            LOGGER.warn("Failed to write voice settings", exception);
        }
    }
}