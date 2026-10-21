package xox.labvorty.chatty.data.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import xox.labvorty.chatty.data.handler.VoiceSoundRegistry;

import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.Function;

public final class VoiceScreen extends Screen {
    private static final int WIDTH = 200;
    private static final int ROW = 24;
    private static final int ARROW = 20;
    private static final int TOP_OFFSET = 85;

    private final Screen parent;
    private int red;
    private int green;
    private int blue;
    private ResourceLocation soundId;
    private float pitch;
    private int frequency;

    private List<VoiceSoundRegistry.Entry> entries = List.of();
    private int soundIndex;
    private Button soundButton;
    private Button leftButton;
    private Button rightButton;
    private Button frequencyButton;

    public VoiceScreen(Screen parent) {
        super(Component.literal("Voice Settings"));
        this.parent = parent;

        int color = VoiceSettings.color();
        this.red = (color >> 16) & 0xFF;
        this.green = (color >> 8) & 0xFF;
        this.blue = color & 0xFF;
        this.soundId = VoiceSettings.sound();
        this.pitch = VoiceSettings.pitch();
        this.frequency = VoiceSettings.frequency();
    }

    @Override
    protected void init() {
        int x = this.width / 2 - WIDTH / 2;
        int y = this.height / 2 - TOP_OFFSET;

        addRenderableWidget(new ValueSlider(x, y, WIDTH, 20, 0.0, 255.0, red,
                value -> Component.literal("Red: " + Math.round(value)),
                value -> red = (int) Math.round(value)));

        addRenderableWidget(new ValueSlider(x, y + ROW, WIDTH, 20, 0.0, 255.0, green,
                value -> Component.literal("Green: " + Math.round(value)),
                value -> green = (int) Math.round(value)));

        addRenderableWidget(new ValueSlider(x, y + ROW * 2, WIDTH, 20, 0.0, 255.0, blue,
                value -> Component.literal("Blue: " + Math.round(value)),
                value -> blue = (int) Math.round(value)));

        entries = VoiceSoundRegistry.entries();
        soundIndex = 0;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).id().equals(soundId)) {
                soundIndex = i;
                break;
            }
        }
        soundId = entries.get(soundIndex).id();

        leftButton = addRenderableWidget(Button.builder(Component.literal("<"), button -> cycleSound(-1))
                .bounds(x, y + ROW * 3, ARROW, 20)
                .build());

        soundButton = addRenderableWidget(Button.builder(soundLabel(), button -> cycleSound(1))
                .bounds(x + ARROW + 4, y + ROW * 3, WIDTH - ARROW * 2 - 8, 20)
                .build());

        rightButton = addRenderableWidget(Button.builder(Component.literal(">"), button -> cycleSound(1))
                .bounds(x + WIDTH - ARROW, y + ROW * 3, ARROW, 20)
                .build());

        addRenderableWidget(new ValueSlider(x, y + ROW * 4, WIDTH, 20, 0.5, 2.0, pitch,
                value -> Component.literal(String.format("Pitch: %.2f", value)),
                value -> pitch = (float) value));

        frequencyButton = addRenderableWidget(Button.builder(frequencyLabel(), button -> cycleFrequency())
                .bounds(x, y + ROW * 5, WIDTH, 20)
                .build());

        addRenderableWidget(Button.builder(Component.literal("Preview Sound"), button -> playPreview())
                .bounds(x, y + ROW * 6, WIDTH, 20)
                .build());

        addRenderableWidget(Button.builder(Component.literal("Done"), button -> save())
                .bounds(x, y + ROW * 7, WIDTH / 2 - 2, 20)
                .build());

        addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> onClose())
                .bounds(x + WIDTH / 2 + 2, y + ROW * 7, WIDTH / 2 - 2, 20)
                .build());
    }

    private Component soundLabel() {
        return entries.get(soundIndex).label();
    }

    private Component frequencyLabel() {
        return Component.literal("Frequency: 1:" + frequency);
    }

    private void cycleSound(int step) {
        if (entries.isEmpty()) {
            return;
        }

        soundIndex = Math.floorMod(soundIndex + step, entries.size());
        soundId = entries.get(soundIndex).id();
        soundButton.setMessage(soundLabel());
    }

    private void cycleFrequency() {
        frequency = frequency >= 3 ? 1 : frequency + 1;
        frequencyButton.setMessage(frequencyLabel());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Object focused = getFocused();
        if (focused != null && (focused == soundButton || focused == leftButton || focused == rightButton)) {
            if (keyCode == GLFW.GLFW_KEY_LEFT) {
                cycleSound(-1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                cycleSound(1);
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int previewY = this.height / 2 - TOP_OFFSET - 20;
        int color = 0xFF000000 | (red << 16) | (green << 8) | blue;

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);
        graphics.drawCenteredString(this.font, Component.literal("Hello World!"), this.width / 2, previewY, color);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    private void playPreview() {
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(VoiceSoundRegistry.get(soundId).event(), VoiceProfile.sanitizePitch(pitch))
        );
    }

    private void save() {
        int color = (red << 16) | (green << 8) | blue;
        VoiceSettings.set(color, soundId, pitch, frequency);
        ClientChatBubbles.sendSettings();
        onClose();
    }

    private static final class ValueSlider extends AbstractSliderButton {
        private final double min;
        private final double max;
        private final Function<Double, Component> label;
        private final DoubleConsumer onChange;

        ValueSlider(int x, int y, int width, int height, double min, double max, double initial,
                    Function<Double, Component> label, DoubleConsumer onChange) {
            super(x, y, width, height, Component.empty(), (initial - min) / (max - min));
            this.min = min;
            this.max = max;
            this.label = label;
            this.onChange = onChange;
            updateMessage();
        }

        private double current() {
            return min + this.value * (max - min);
        }

        @Override
        protected void updateMessage() {
            setMessage(label.apply(current()));
        }

        @Override
        protected void applyValue() {
            onChange.accept(current());
        }
    }
}