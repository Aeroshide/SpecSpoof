package com.aeroshide.specspoof.mixins;

import com.aeroshide.specspoof.SpecSpoofClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    private int fpsCounter;



    @ModifyVariable(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;fpsCounter:I"), method = "render(Z)V", index = 1, argsOnly = true)
    private boolean addFakeFPS(boolean value) {

        if (SpecSpoofClient.daFPS != 0 && this.fpsCounter >= SpecSpoofClient.disableFPSThreshold)
        {
            this.fpsCounter = SpecSpoofClient.daFPS;
            this.fpsCounter += (int) (Math.random() * 100 * 2) - 50;
        }
        return true;
    }
}