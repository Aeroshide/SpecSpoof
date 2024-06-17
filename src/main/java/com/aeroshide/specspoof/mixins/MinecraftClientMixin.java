package com.aeroshide.specspoof.mixins;

import com.aeroshide.specspoof.SpecSpoofClient;
import com.aeroshide.specspoof.config.DataHolder;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    private int fpsCounter;




    @ModifyVariable(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;fpsCounter:I"), method = "render(Z)V", index = 1, argsOnly = true)
    private boolean addFakeFPS(boolean value) {

        if (DataHolder.getDaFPS() != 0 && this.fpsCounter >= DataHolder.getDisableFPSThreshold())
        {
            this.fpsCounter = DataHolder.getDaFPS();
            this.fpsCounter += (int) (Math.random() * 100 * 2) - 50;
        }
        return true;
    }
}