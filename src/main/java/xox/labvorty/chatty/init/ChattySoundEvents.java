package xox.labvorty.chatty.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import xox.labvorty.chatty.Chatty;

public class ChattySoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Chatty.MOD_ID);
    public static final DeferredHolder<SoundEvent, SoundEvent> SANS = SOUND_EVENTS.register(
            "sans",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath("chatty", "sans")
            )
    );
    public static final DeferredHolder<SoundEvent, SoundEvent> PAPYRUS = SOUND_EVENTS.register(
            "papyrus",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath("chatty", "papyrus")
            )
    );
    public static final DeferredHolder<SoundEvent, SoundEvent> TRUMPET = SOUND_EVENTS.register(
            "trumpet",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath("chatty", "trumpet")
            )
    );
    public static final DeferredHolder<SoundEvent, SoundEvent> TRUMPET_EXPOSED = SOUND_EVENTS.register(
            "trumpet_exposed",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath("chatty", "trumpet_exposed")
            )
    );
    public static final DeferredHolder<SoundEvent, SoundEvent> TRUMPET_WEATHERED = SOUND_EVENTS.register(
            "trumpet_weathered",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath("chatty", "trumpet_weathered")
            )
    );
    public static final DeferredHolder<SoundEvent, SoundEvent> TRUMPET_OXIDIZED = SOUND_EVENTS.register(
            "trumpet_oxidized",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath("chatty", "trumpet_oxidized")
            )
    );
}
