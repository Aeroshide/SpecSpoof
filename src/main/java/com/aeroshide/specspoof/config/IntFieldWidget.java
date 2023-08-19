package com.aeroshide.specspoof.config;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;

public class IntFieldWidget extends TextFieldWidget {
    public IntFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, MutableText message) {
        super(textRenderer, x, y, width, height, message);
    }

    public void write(String text) {
        writeSynchronized(text);
    }

    private synchronized void writeSynchronized(String text) {
        String oldText = this.getText();
        super.write(text);
        String newText = this.getText();

        while (newText.length() > 1 && newText.startsWith("0")) {
            newText = newText.substring(1);
        }

        try {
            long number = Long.parseLong(newText);
        } catch (NumberFormatException e) {
            this.setText(oldText);
            return;
        }
        this.setText(newText);
    }

    public int getInt() {
        synchronized(this) {
            return Integer.parseInt(this.getText());
        }

    }

}

