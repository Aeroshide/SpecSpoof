package com.aeroshide.specspoof.config;

import com.aeroshide.specspoof.SpecSpoofClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.io.File;

public class OptionsScreen extends Screen {

    private final Screen parent;
    private final int buttonCount = 0;
    private static final Text CPU_NAME_TEXT = Text.translatable("specspoof.cpu");
    private static final Text GPU_NAME_TEXT = Text.translatable("specspoof.gpu");
    private static final Text FPS_NAME_TEXT = Text.translatable("specspoof.fps");
    private TextFieldWidget cpuNameField;
    private TextFieldWidget gpuNameField;
    private IntFieldWidget fpsValueField;
    private SliderWidget kontol;
    private ButtonWidget discardButton;
    private ButtonWidget acceptButton;
    private ButtonWidget cfgButton;
    private int tempDFPST = SpecSpoofClient.disableFPSThreshold;
    // current task: trying to have config in-outs from my new UI system :)
    public OptionsScreen(Screen parent) {
        super(Text.translatable("specspoof.configScreen"));
        this.parent = parent;
    }

    @Override
    public void init()
    {

        this.cpuNameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 44, 200, 20, Text.translatable("specspoof.cpu"));
        this.cpuNameField.setMaxLength(64);
        this.cpuNameField.setText(SpecSpoofClient.daCPUName);
        this.cpuNameField.setChangedListener((cpuName) -> {
            updateAcceptButton();
        });

        this.gpuNameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 44 + 35, 200, 20, Text.translatable("specspoof.gpu"));
        this.gpuNameField.setMaxLength(64);
        this.gpuNameField.setText(SpecSpoofClient.daGPUName);
        this.gpuNameField.setChangedListener((gpuName) -> {
            updateAcceptButton();
        });


        this.fpsValueField = new IntFieldWidget(this.textRenderer, this.width / 2 - 100, 44 + 70, 200, 20, Text.translatable("specspoof.fps"));
        this.fpsValueField.setMaxLength(6);
        this.fpsValueField.setText(String.valueOf(SpecSpoofClient.daFPS));
        this.fpsValueField.setChangedListener((fps) -> {
            updateAcceptButton();
        });


        this.kontol = new SliderWidget(this.width / 2 - 100, 44 + 105, 200, 20, Text.translatable("specspoof.fpsThres", getTextForSlider(SpecSpoofClient.disableFPSThreshold)), (SpecSpoofClient.disableFPSThreshold - 1.0) / (260.0 - 1.0)) {
            protected void updateMessage() {

                setMessage(Text.translatable("specspoof.fpsThres", getTextForSlider((value * (260.0 - 1.0) + 1.0))));
                updateAcceptButton();
            }

            protected void applyValue() {
                if ((value * (260.0 - 1.0) + 1.0) >= 260)
                {
                    tempDFPST = 999999;
                }
                else
                {
                    tempDFPST = (int) (value * (260.0 - 1.0) + 1.0);
                }


            }
        };

        this.discardButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("specspoof.discard"), (button) -> {
            client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 190, this.height / 4 + 120 + 28, 100, 20).build());

        this.cfgButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("specspoof.openCfg"), (button) -> {
            Util.getOperatingSystem().open(new File(System.getProperty("user.dir") + "/config/SpecSpoof.json"));
        }).dimensions(this.width / 2 - 100 + (this.discardButton.getX()), this.height / 4 + 120 + 28, 100, 20).build());

        this.acceptButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("specspoof.save"), (button) -> {
            writeConfig();
            SpecSpoofClient.fetchConfig();
            client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 100 + (this.cfgButton.getX()), this.height / 4 + 120 + 28, 100, 20).build());



        this.setInitialFocus(this.cpuNameField);
        this.addSelectableChild(this.cpuNameField);
        this.addSelectableChild(this.gpuNameField);
        this.addSelectableChild(this.fpsValueField);
        this.addSelectableChild(this.kontol);
        updateAcceptButton();
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        context.drawTextWithShadow(this.textRenderer, CPU_NAME_TEXT, this.width / 2 - 100, this.cpuNameField.getY() - 10, 10526880);
        context.drawTextWithShadow(this.textRenderer, GPU_NAME_TEXT, this.width / 2 - 100, this.gpuNameField.getY() - 10, 10526880);
        context.drawTextWithShadow(this.textRenderer, FPS_NAME_TEXT, this.width / 2 - 100, this.fpsValueField.getY() - 10, 10526880);
        this.cpuNameField.render(context, mouseX, mouseY, delta);
        this.gpuNameField.render(context, mouseX, mouseY, delta);
        this.fpsValueField.render(context, mouseX, mouseY, delta);
        this.kontol.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    private void writeConfig()
    {
        SpecSpoofClient.config.setOption("CPU", this.cpuNameField.getText());
        SpecSpoofClient.config.setOption("GPU", this.gpuNameField.getText());
        SpecSpoofClient.config.setOption("FPS", Integer.parseInt(this.fpsValueField.getText()));
        SpecSpoofClient.config.setOption("disableFPSThreshold", tempDFPST);
    }
    private void updateAcceptButton()
    {
        SpecSpoofClient.LOG.info(shouldButtonBeActive());
        this.acceptButton.active = shouldButtonBeActive();
    }

    public boolean shouldButtonBeActive() {
        boolean shouldActivate = false;

        if (this.fpsValueField.getText() != null && fpsValueField.getInt() != SpecSpoofClient.daFPS) {
            SpecSpoofClient.LOG.info("FPS value field has changed. New value: '{}', Old value: '{}'", this.fpsValueField.getInt(), SpecSpoofClient.daFPS);
            shouldActivate = true;
        }

        if (this.gpuNameField.getText() != null && !this.gpuNameField.getText().trim().equals(SpecSpoofClient.daGPUName.trim())) {
            SpecSpoofClient.LOG.info("GPU name field has changed. New value: '{}', Old value: '{}'", this.gpuNameField.getText(), SpecSpoofClient.daGPUName);
            shouldActivate = true;
        }

        if (this.cpuNameField.getText() != null && !this.cpuNameField.getText().trim().equals(SpecSpoofClient.daCPUName.trim())) {
            SpecSpoofClient.LOG.info("CPU name field has changed. New value: '{}', Old value: '{}'", this.cpuNameField.getText(), SpecSpoofClient.daCPUName);
            shouldActivate = true;
        }

        if (tempDFPST != SpecSpoofClient.disableFPSThreshold) {
            SpecSpoofClient.LOG.info("disableFPSThreshold has changed. New value: '{}', Old value: '{}'", tempDFPST, SpecSpoofClient.disableFPSThreshold);
            shouldActivate = true;
        }

        if (!shouldActivate) {
            SpecSpoofClient.LOG.info("No changes detected.");
        }

        return shouldActivate;
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
