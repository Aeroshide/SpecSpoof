package com.aeroshide.specspoof.config.screen;

import com.aeroshide.specspoof.SpecSpoofClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class OptionsErrorScreen extends Screen {
    private MultilineText message;
    private final Screen originalParent;

    public OptionsErrorScreen(Screen parent) {
        super(Text.translatable("specspoof.configissues"));
        this.message = MultilineText.EMPTY;
        this.originalParent = parent;
    }

    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.TO_TITLE, (button) -> {
            client.setScreen(this.originalParent);
        }).dimensions(this.width / 2 - 155, this.height / 4 + 120 + 12, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("specspoof.resetOptions"), (button) -> {
            SpecSpoofClient.configIssues = true;
            client.setScreen(new OptionsScreen(this.originalParent));
            SpecSpoofClient.config.initConfig(true);
            SpecSpoofClient.LOG.info("Reloading Config!");
            SpecSpoofClient.fetchConfig();
        }).dimensions(this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, 150, 20).build());
        this.message = MultilineText.create(this.textRenderer,Text.translatable( "specspoof.issueMessage").formatted(Formatting.RED), 295);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 4 - 60 + 20, 16777215);
        this.message.drawWithShadow(context, this.width / 2 - 145, this.height / 4, 9, 10526880);
        super.render(context, mouseX, mouseY, delta);
    }
}
