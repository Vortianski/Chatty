package xox.labvorty.chatty.data.handler;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VoiceSoundRegistry {
    public static final ResourceLocation DEFAULT_ID = ResourceLocation.fromNamespaceAndPath("minecraft", "bell");

    private static final Map<ResourceLocation, Entry> loaded = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Entry> fixed = new LinkedHashMap<>();
    private static boolean pending = true;

    public record Entry(ResourceLocation id, Component label, SoundEvent event) {
    }

    public static boolean add(ResourceLocation id, Component label, SoundEvent event) {
        if (loaded.containsKey(id) || fixed.containsKey(id)) return false;
        loaded.put(id, new Entry(id, label, event));
        return true;
    }

    public static boolean addFixed(ResourceLocation id, Component label, SoundEvent event) {
        if (fixed.containsKey(id)) return false;
        loaded.remove(id);
        fixed.put(id, new Entry(id, label, event));
        return true;
    }

    public static void clear() {
        loaded.clear();
        pending = true;
    }

    private static void dispatchPending() {
        if (!pending) return;
        pending = false;
        NeoForge.EVENT_BUS.post(new VoiceSoundRegistryReloadEvent());
    }

    public static Map<ResourceLocation, Entry> getRegistry() {
        Map<ResourceLocation, Entry> all = new LinkedHashMap<>(fixed);
        all.putAll(loaded);
        return all;
    }

    public static List<Entry> entries() {
        dispatchPending();

        Map<ResourceLocation, Entry> all = getRegistry();
        if (all.isEmpty()) {
            return List.of(fallback());
        }
        return new ArrayList<>(all.values());
    }

    public static Entry get(ResourceLocation id) {
        dispatchPending();

        Map<ResourceLocation, Entry> all = getRegistry();

        Entry entry = all.get(id);
        if (entry != null) return entry;

        entry = all.get(DEFAULT_ID);
        if (entry != null) return entry;

        if (!all.isEmpty()) return all.values().iterator().next();

        return fallback();
    }

    private static Entry fallback() {
        return new Entry(DEFAULT_ID, Component.literal("Bit"), SoundEvents.NOTE_BLOCK_BIT.value());
    }
}