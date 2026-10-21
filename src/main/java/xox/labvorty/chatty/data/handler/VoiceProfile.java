package xox.labvorty.chatty.data.handler;

import net.minecraft.util.Mth;

import java.util.UUID;

public final class VoiceProfile {
    private final float basePitch;
    private final float spread;
    private final long seed;

    private VoiceProfile(long seed, float basePitch) {
        this.seed = seed;
        this.basePitch = basePitch;
        this.spread = 0.05f + unit(mix(seed, 2L)) * 0.2f;
    }

    public static VoiceProfile of(UUID id, float basePitch) {
        return new VoiceProfile(id.getMostSignificantBits() ^ Long.rotateLeft(id.getLeastSignificantBits(), 32), basePitch);
    }

    public static float sanitizePitch(float pitch) {
        if (!Float.isFinite(pitch)) {
            return 1.0f;
        }
        return Mth.clamp(pitch, 0.5f, 2.0f);
    }

    public static int sanitizeFrequency(int frequency) {
        return Mth.clamp(frequency, 1, 3);
    }

    public float pitchFor(char c) {
        float offset = (unit(mix(seed, c)) - 0.5f) * 2.0f * spread;
        return sanitizePitch(basePitch + offset);
    }

    private static long mix(long seed, long value) {
        long z = seed + value * 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    private static float unit(long hash) {
        return (hash >>> 40) / (float) (1L << 24);
    }
}