package com.aeroshide.specspoof.mixins;

import com.aeroshide.specspoof.SpecSpoofClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = DebugHud.class, priority = 999)
public class DebugHudMixin {

    @Unique
    SpecSpoofClient main = new SpecSpoofClient();

    @Inject(method = "getRightText", at = @At("RETURN"))
    private void getRightText(CallbackInfoReturnable<List<String>> info) {
        List<String> rightText = info.getReturnValue();

        int cpuIndex = -1;
        int gpuIndex = -1;
        for (int i = 0; i < rightText.size(); i++) {
            String text = rightText.get(i);
            if (text.contains("CPU")) {
                cpuIndex = i;
            } else if (text.contains("SSE2") || text.contains("GPU")) {
                gpuIndex = i;
            }
        }

        if (cpuIndex != -1) {
            rightText.set(cpuIndex, SpecSpoofClient.daCPUName);
        }
        if (gpuIndex != -1) {
            rightText.set(gpuIndex, SpecSpoofClient.daGPUName);
        }

        // so i dont override areesggee
        if (!main.daMods.isEmpty())
        {
            rightText.add("");
            rightText.add("CHEATING! v" + FabricLoader.getInstance().getModContainer("areessgee").get().getMetadata().getVersion().getFriendlyString());
        }
    }
}
