package xox.labvorty.chatty.data;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Double> SOUND_VOLUME = BUILDER
            .comment("Volume of chat sounds")
            .defineInRange("chatVolume", 1f, 0, 1);
    public static final ModConfigSpec.ConfigValue<Double> HEAD_OFFSET = BUILDER
            .comment("Overhead message Y offset")
            .defineInRange("headOffset", 0.75, 0, 2);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
