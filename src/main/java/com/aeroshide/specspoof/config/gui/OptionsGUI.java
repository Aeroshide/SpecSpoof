package com.aeroshide.specspoof.config.gui;

import com.aeroshide.specspoof.SpecSpoofClient;
import com.aeroshide.specspoof.config.DataHolder;
import com.aeroshide.specspoof.config.IntFieldWidget;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.util.Locale;

import static com.aeroshide.specspoof.config.DataHolder.*;

public class OptionsGUI extends Screen {

    private final Screen parent;
    private int pageContext = 0;
    private final int buttonCount = 0;
    private static final Text CPU_NAME_TEXT = Text.translatable("specspoof.cpu");
    private static final Text GPU_NAME_TEXT = Text.translatable("specspoof.gpu");
    private static final Text FPS_NAME_TEXT = Text.translatable("specspoof.fps");
    private static final Text GPU_DRIVER_NAME_TEXT = Text.translatable("specspoof.gpuDriver");
    private static final Text GPU_VENDOR_NAME_TEXT = Text.translatable("specspoof.gpuVendor");
    private TextFieldWidget cpuNameField;
    private ButtonWidget resetCPUnameField;
    private TextFieldWidget gpuNameField;
    private ButtonWidget resetGPUnameField;
    private IntFieldWidget fpsValueField;
    private ButtonWidget resetFPSField;
    private SliderWidget kontol;
    //private ButtonWidget cfgButton;

    // page 2
    private TextFieldWidget gpuDriverField;
    private ButtonWidget resetgpuDriverField;
    private TextFieldWidget gpuVendorField;
    private ButtonWidget resetgpuVendorField;
    private ButtonWidget nextPageButton;
    private ButtonWidget prevPageButton;

    private Number tempDFPST = DataHolder.getDisableFPSThreshold();
    // current task: trying to have config in-outs from my new UI system :)

    public OptionsGUI(Screen parent) {
        super(Text.translatable("specspoof.configScreen"));
        this.parent = parent;
    }

    @Override
    public void init()
    {

        this.cpuNameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 44, 200, 20, Text.translatable("specspoof.cpu"));
        this.cpuNameField.setMaxLength(64);
        this.cpuNameField.setText(DataHolder.getDaCPUName());

        this.resetCPUnameField = this.addDrawableChild(ButtonWidget.builder(Text.literal("R"), (button) -> {
            CentralProcessor centralProcessor = (new SystemInfo()).getHardware().getProcessor();
            String cpuInfo = String.format(Locale.ROOT, "%dx %s", centralProcessor.getLogicalProcessorCount(), centralProcessor.getProcessorIdentifier().getName()).replaceAll("\\s+", " ");
            cpuNameField.setText(cpuInfo);
        }).dimensions(this.cpuNameField.getX() + 205, this.cpuNameField.getY(), 20, 20).build());

        this.gpuNameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 44 + 35, 200, 20, Text.translatable("specspoof.gpu"));
        this.gpuNameField.setMaxLength(64);
        this.gpuNameField.setText(DataHolder.getDaGPUName());

        this.resetGPUnameField = this.addDrawableChild(ButtonWidget.builder(Text.literal("R"), (button) -> {
            gpuNameField.setText(GlStateManager._getString(GPU_RENDERER));
        }).dimensions(this.gpuNameField.getX() + 205, this.gpuNameField.getY(), 20, 20).build());


        this.fpsValueField = new IntFieldWidget(this.textRenderer, this.width / 2 - 100, 44 + 70, 200, 20, Text.translatable("specspoof.fps"));
        this.fpsValueField.setMaxLength(6);
        this.fpsValueField.setText(String.valueOf(DataHolder.getDaFPS()));

        this.resetFPSField = this.addDrawableChild(ButtonWidget.builder(Text.literal("R"), (button) -> {
            fpsValueField.setText("1000");
        }).dimensions(this.fpsValueField.getX() + 205, this.fpsValueField.getY(), 20, 20).build());



        this.kontol = new SliderWidget(this.width / 2 - 100, 44 + 105, 200, 20, Text.translatable("specspoof.fpsThres", getTextForSlider(DataHolder.getDisableFPSThreshold())), (DataHolder.getDisableFPSThreshold() - 1.0) / (260.0 - 1.0)) {
            protected void updateMessage() {

                setMessage(Text.translatable("specspoof.fpsThres", getTextForSlider((value * (260.0 - 1.0) + 1.0))));
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

        ButtonWidget discardButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("specspoof.discard"), (button) -> {
            client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 110, this.height / 2 + 90, 100, 20).build());

        ButtonWidget acceptButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("specspoof.save"), (button) -> {
            writeConfig();
            DataHolder.fetchOptions();
            client.setScreen(this.parent);
        }).dimensions(this.width / 2 + 20, this.height / 2 + 90, 100, 20).build());

        this.gpuDriverField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 44, 200, 20, Text.translatable("specspoof.gpuDriver"));
        this.gpuDriverField.setMaxLength(64);
        this.gpuDriverField.setText(DataHolder.getDaGPUDriver());

        this.resetgpuDriverField = this.addDrawableChild(ButtonWidget.builder(Text.literal("R"), (button) -> {
            gpuDriverField.setText(GlStateManager._getString(GPU_VERSION));
        }).dimensions(this.gpuDriverField.getX() + 205, this.gpuDriverField.getY(), 20, 20).build());

        this.gpuVendorField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 44 + 35, 200, 20, Text.translatable("specspoof.gpuVendor"));
        this.gpuVendorField.setMaxLength(64);
        this.gpuVendorField.setText(DataHolder.getDaGPUVendor());

        this.resetgpuVendorField = this.addDrawableChild(ButtonWidget.builder(Text.literal("R"), (button) -> {
            gpuVendorField.setText(GlStateManager._getString(GPU_VENDOR));
        }).dimensions(this.gpuVendorField.getX() + 205, this.gpuVendorField.getY(), 20, 20).build());

        this.nextPageButton = this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), (button) -> {
            changePage(1);
        }).dimensions(this.width / 2 + 70, this.height / 2 + 60, 30, 20).build());

        this.prevPageButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("<"), (button) -> {
            changePage(-1);
        }).dimensions(this.width / 2 - 100, this.height / 2 + 60, 30, 20).build());


        this.setInitialFocus(this.cpuNameField);
        this.addSelectableChild(this.cpuNameField);
        this.addSelectableChild(this.gpuNameField);
        this.addSelectableChild(this.fpsValueField);
        this.addSelectableChild(this.kontol);
        this.addSelectableChild(this.gpuDriverField);
        this.addSelectableChild(this.gpuVendorField);
        renderPage();
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        if (pageContext == 0) {
            context.drawTextWithShadow(this.textRenderer, CPU_NAME_TEXT, this.width / 2 - 100, this.cpuNameField.getY() - 10, 10526880);
            context.drawTextWithShadow(this.textRenderer, GPU_NAME_TEXT, this.width / 2 - 100, this.gpuNameField.getY() - 10, 10526880);
            context.drawTextWithShadow(this.textRenderer, FPS_NAME_TEXT, this.width / 2 - 100, this.fpsValueField.getY() - 10, 10526880);
        } else if (pageContext == 1)
        {
            context.drawTextWithShadow(this.textRenderer, GPU_VENDOR_NAME_TEXT, this.width / 2 - 100, this.gpuVendorField.getY() - 10, 10526880);
            context.drawTextWithShadow(this.textRenderer, GPU_DRIVER_NAME_TEXT, this.width / 2 - 100, this.gpuDriverField.getY() - 10, 10526880);
        }

        this.cpuNameField.render(context, mouseX, mouseY, delta);
        this.gpuNameField.render(context, mouseX, mouseY, delta);
        this.fpsValueField.render(context, mouseX, mouseY, delta);
        this.kontol.render(context, mouseX, mouseY, delta);
        this.gpuDriverField.render(context, mouseX, mouseY, delta);
        this.gpuVendorField.render(context, mouseX, mouseY, delta);

    }

    private void writeConfig()
    {
        SpecSpoofClient.config.setOption("CPU", this.cpuNameField.getText());
        SpecSpoofClient.config.setOption("GPU", this.gpuNameField.getText());
        SpecSpoofClient.config.setOption("FakeFPS", Integer.parseInt(this.fpsValueField.getText()));
        SpecSpoofClient.config.setOption("DisableFakeFPSThreshold", tempDFPST);
        SpecSpoofClient.config.setOption("GPUDriver", this.gpuDriverField.getText());
        SpecSpoofClient.config.setOption("GPUVendor", this.gpuVendorField.getText());

    }

    private void changePage(int a)
    {
        // 1 forward, -1 backward
        pageContext += a;

        renderPage();
    }

    // this is so shit
    private void renderPage()
    {
        if (pageContext == 0)
        {
            this.cpuNameField.visible = true;
            this.resetCPUnameField.visible = true;
            this.gpuNameField.visible = true;
            this.resetGPUnameField.visible = true;
            this.fpsValueField.visible = true;
            this.resetFPSField.visible = true;
            this.kontol.visible = true;

            this.gpuVendorField.visible = false;
            this.gpuDriverField.visible = false;



            this.prevPageButton.active = false;
            this.nextPageButton.active = true;
        }
        else if (pageContext == 1)
        {
            this.cpuNameField.visible = false;
            this.resetCPUnameField.visible = false;
            this.resetFPSField.visible = false;
            this.gpuNameField.visible = false;
            this.resetGPUnameField.visible = false;
            this.fpsValueField.visible = false;
            this.kontol.visible = false;

            this.gpuVendorField.visible = true;
            this.gpuDriverField.visible = true;

            this.prevPageButton.active = true;
            this.nextPageButton.active = false;
        }
    }





    private Text getTextForSlider(double a)
    {
        if (a <= 1)
        {
            return Text.translatable("specspoof.fraud");
        } else if (a >= 260) {
            return Text.translatable("specspoof.honest");
        }

        return Text.literal((int) a + " FPS");
    }


}