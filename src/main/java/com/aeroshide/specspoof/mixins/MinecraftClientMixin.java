package com.aeroshide.specspoof.mixins;

import com.aeroshide.specspoof.FakeFPSManager;
import com.aeroshide.specspoof.SpecSpoofClient;
import com.aeroshide.specspoof.config.DataHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {


    @Inject(method = "getFps", at = @At("RETURN"), cancellable = true, remap = false)
    private static void modifyFPS(CallbackInfoReturnable<Integer> cir) {
        int actual = cir.getReturnValue();
        int fakeFps = FakeFPSManager.getOrComputeFakeFps(actual);
        cir.setReturnValue(fakeFps);
    }
}