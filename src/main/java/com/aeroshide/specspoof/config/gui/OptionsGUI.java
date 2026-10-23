package com.aeroshide.specspoof.config.gui;

import com.aeroshide.rose_bush.gui.IntFieldWidget;
import com.aeroshide.specspoof.SpecSpoofClient;
import com.aeroshide.specspoof.config.DataHolder;
import com.mojang.renderpearl.backend.opengl.GlStateManager;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.io.IOException;

import static com.aeroshide.specspoof.config.DataHolder.*;

public class OptionsGUI extends Screen {

    private final Screen parent;
    private int pageContext = 0;
    private final int buttonCount = 0;
    private static final Component CPU_NAME_TEXT = Component.translatable("specspoof.cpu");
    private static final Component GPU_NAME_TEXT = Component.translatable("specspoof.gpu");
    private static final Component FPS_NAME_TEXT = Component.translatable("specspoof.fps");
    private static final Component GPU_DRIVER_NAME_TEXT = Component.translatable("specspoof.gpuDriver");
    private static final Component GPU_VENDOR_NAME_TEXT = Component.translatable("specspoof.gpuVendor");
    private static final Component GPU_BACKEND_TEXT = Component.translatable("specspoof.gpuBackend");
    private EditBox cpuNameField;
    private Button resetCPUnameField;
    private EditBox gpuNameField;
    private Button resetGPUnameField;
    private IntFieldWidget fpsValueField;
    private Button resetFPSField;
    private AbstractSliderButton kontol;

    // page 2
    private EditBox gpuDriverField;
    private Button resetgpuDriverField;
    private EditBox gpuVendorField;
    private Button resetgpuVendorField;
    private EditBox gpuBackendField;
    private Button resetgpuBackendField;
    private Button nextPageButton;
    private Button prevPageButton;

    private Number tempDFPST = DataHolder.getDisableFPSThreshold();

    public OptionsGUI(Screen parent) {
        super(Component.translatable("specspoof.configScreen"));
        this.parent = parent;
    }

    @Override
    public void init() {

        this.cpuNameField = new EditBox(this.font, this.width / 2 - 100, 44, 200, 20, Component.translatable("specspoof.cpu"));
        this.cpuNameField.setMaxLength(64);
        this.cpuNameField.setValue(DataHolder.getDaCPUName());

        this.resetCPUnameField = this.addRenderableWidget(Button.builder(Component.literal("R"), (button) -> {
            cpuNameField.setValue(DataHolder.fetchCPUData());
        }).bounds(this.cpuNameField.getX() + 205, this.cpuNameField.getY(), 20, 20).build());

        this.gpuNameField = new EditBox(this.font, this.width / 2 - 100, 44 + 35, 200, 20, Component.translatable("specspoof.gpu"));
        this.gpuNameField.setMaxLength(64);
        this.gpuNameField.setValue(DataHolder.getDaGPUName());

        this.resetGPUnameField = this.addRenderableWidget(Button.builder(Component.literal("R"), (button) -> {
            gpuNameField.setValue(DataHolder.getRealGPUName());
        }).bounds(this.gpuNameField.getX() + 205, this.gpuNameField.getY(), 20, 20).build());


        this.fpsValueField = new IntFieldWidget(this.font, this.width / 2 - 100, 44 + 70, 200, 20, Component.translatable("specspoof.fps"));
        this.fpsValueField.setMaxLength(6);
        this.fpsValueField.setValue(String.valueOf(DataHolder.getDaFPS()));

        this.resetFPSField = this.addRenderableWidget(Button.builder(Component.literal("R"), (button) -> {
            fpsValueField.setValue("1000");
        }).bounds(this.fpsValueField.getX() + 205, this.fpsValueField.getY(), 20, 20).build());

        this.kontol = new AbstractSliderButton(this.width / 2 - 100, 44 + 105, 200, 20, Component.translatable("specspoof.fpsThres", getTextForSlider(DataHolder.getDisableFPSThreshold())), (DataHolder.getDisableFPSThreshold() - 1.0) / (260.0 - 1.0)) {
            protected void updateMessage() {
                setMessage(Component.translatable("specspoof.fpsThres", getTextForSlider((value * (260.0 - 1.0) + 1.0))));
            }

            protected void applyValue() {
                if ((value * (260.0 - 1.0) + 1.0) >= 260)
                {
                    tempDFPST = 999999;
                }
                else
                {
                    tempDFPST = (value * (260.0 - 1.0) + 1.0);
                }
            }
        };

        Button discardButton = this.addRenderableWidget(Button.builder(Component.translatable("specspoof.discard"), (button) -> {
            minecraft.gui.setScreen(this.parent);
        }).bounds(this.width / 2 - 110, this.height / 2 + 90, 100, 20).build());

        Button acceptButton = this.addRenderableWidget(Button.builder(Component.translatable("specspoof.save"), (button) -> {
            try {
                writeConfig();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            DataHolder.fetchOptions();
            minecraft.gui.setScreen(this.parent);
        }).bounds(this.width / 2 + 20, this.height / 2 + 90, 100, 20).build());

        this.gpuDriverField = new EditBox(this.font, this.width / 2 - 100, 44, 200, 20, Component.translatable("specspoof.gpuDriver"));
        this.gpuDriverField.setMaxLength(64);
        this.gpuDriverField.setValue(DataHolder.getDaGPUDriver());

        this.resetgpuDriverField = this.addRenderableWidget(Button.builder(Component.literal("R"), (button) -> {
            gpuDriverField.setValue(DataHolder.getRealGPUDriver());
        }).bounds(this.gpuDriverField.getX() + 205, this.gpuDriverField.getY(), 20, 20).build());

        this.gpuVendorField = new EditBox(this.font, this.width / 2 - 100, 44 + 35, 200, 20, Component.translatable("specspoof.gpuVendor"));
        this.gpuVendorField.setMaxLength(64);
        this.gpuVendorField.setValue(DataHolder.getDaGPUVendor());

        this.resetgpuVendorField = this.addRenderableWidget(Button.builder(Component.literal("R"), (button) -> {
            gpuVendorField.setValue(DataHolder.getRealGPUVendor());
        }).bounds(this.gpuVendorField.getX() + 205, this.gpuVendorField.getY(), 20, 20).build());

        this.gpuBackendField = new EditBox(this.font, this.width / 2 - 100, 44 + 70, 200, 20, GPU_BACKEND_TEXT);
        this.gpuBackendField.setMaxLength(64);
        this.gpuBackendField.setValue(DataHolder.getDaGPUBackend());

        this.resetgpuBackendField = this.addRenderableWidget(Button.builder(Component.literal("R"), (button) -> {
            gpuBackendField.setValue(DataHolder.getRealGPUBackend());
        }).bounds(this.gpuBackendField.getX() + 205, this.gpuBackendField.getY(), 20, 20).build());

        this.nextPageButton = this.addRenderableWidget(Button.builder(Component.literal(">"), (button) -> {
            changePage(1);
        }).bounds(this.width / 2 + 70, this.height / 2 + 60, 30, 20).build());

        this.prevPageButton = this.addRenderableWidget(Button.builder(Component.literal("<"), (button) -> {
            changePage(-1);
        }).bounds(this.width / 2 - 100, this.height / 2 + 60, 30, 20).build());

        this.setInitialFocus(this.cpuNameField);

        this.addRenderableWidget(this.cpuNameField);
        this.addRenderableWidget(this.gpuNameField);
        this.addRenderableWidget(this.fpsValueField);
        this.addRenderableWidget(this.kontol);
        this.addRenderableWidget(this.gpuDriverField);
        this.addRenderableWidget(this.gpuVendorField);
        this.addRenderableWidget(this.gpuBackendField);

        renderPage();
    }

    @Override
    public void extractRenderState(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFFFF);

        if (pageContext == 0) {
            graphics.text(this.font, CPU_NAME_TEXT, this.width / 2 - 100, this.cpuNameField.getY() - 10, 0xFFA0A0A0);
            graphics.text(this.font, GPU_NAME_TEXT, this.width / 2 - 100, this.gpuNameField.getY() - 10, 0xFFA0A0A0);
            graphics.text(this.font, FPS_NAME_TEXT, this.width / 2 - 100, this.fpsValueField.getY() - 10, 0xFFA0A0A0);
        } else if (pageContext == 1) {
            graphics.text(this.font, GPU_VENDOR_NAME_TEXT, this.width / 2 - 100, this.gpuVendorField.getY() - 10, 0xFFA0A0A0);
            graphics.text(this.font, GPU_DRIVER_NAME_TEXT, this.width / 2 - 100, this.gpuDriverField.getY() - 10, 0xFFA0A0A0);
            graphics.text(this.font, GPU_BACKEND_TEXT, this.width / 2 - 100, this.gpuBackendField.getY() - 10, 0xFFA0A0A0);
        }
    }

    private void writeConfig() throws IOException {
        SpecSpoofClient.config.setOption("CPU", this.cpuNameField.getValue());
        SpecSpoofClient.config.setOption("GPU", this.gpuNameField.getValue());
        SpecSpoofClient.config.setOption("FakeFPS", Integer.parseInt(this.fpsValueField.getValue()));
        SpecSpoofClient.config.setOption("DisableFakeFPSThreshold", tempDFPST);
        SpecSpoofClient.config.setOption("GPUDriverVersion", this.gpuDriverField.getValue());
        SpecSpoofClient.config.setOption("GPUVendor", this.gpuVendorField.getValue());
        SpecSpoofClient.config.setOption("GPUBackend", this.gpuBackendField.getValue());
    }

    private void changePage(int a) {
        pageContext += a;
        renderPage();
    }

    private void renderPage() {
        if (pageContext == 0) {
            this.cpuNameField.visible = true;
            this.resetCPUnameField.visible = true;
            this.gpuNameField.visible = true;
            this.resetGPUnameField.visible = true;
            this.fpsValueField.visible = true;
            this.resetFPSField.visible = true;
            this.kontol.visible = true;

            this.gpuVendorField.visible = false;
            this.gpuDriverField.visible = false;
            this.gpuBackendField.visible = false;
            this.resetgpuBackendField.visible = false;

            this.prevPageButton.active = false;
            this.nextPageButton.active = true;
        } else if (pageContext == 1) {
            this.cpuNameField.visible = false;
            this.resetCPUnameField.visible = false;
            this.resetFPSField.visible = false;
            this.gpuNameField.visible = false;
            this.resetGPUnameField.visible = false;
            this.fpsValueField.visible = false;
            this.kontol.visible = false;

            this.gpuVendorField.visible = true;
            this.gpuDriverField.visible = true;
            this.gpuBackendField.visible = true;
            this.resetgpuBackendField.visible = true;

            this.prevPageButton.active = true;
            this.nextPageButton.active = false;
        }
    }

    private Component getTextForSlider(double a) {
        if (a <= 1) {
            return Component.translatable("specspoof.fraud");
        } else if (a >= 260) {
            return Component.translatable("specspoof.honest");
        }
        return Component.literal((int) a + " FPS");
    }
}