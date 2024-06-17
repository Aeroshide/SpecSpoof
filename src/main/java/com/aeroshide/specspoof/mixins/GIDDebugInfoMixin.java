package com.aeroshide.specspoof.mixins;

import com.aeroshide.specspoof.SpecSpoofClient;
import com.aeroshide.specspoof.config.DataHolder;
import com.mojang.blaze3d.platform.GlDebugInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlDebugInfo.class)
public class GIDDebugInfoMixin {

    @Inject(method = "getCpuInfo", at = @At("RETURN"), cancellable = true)
    private static void modifyCPUGL(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(DataHolder.getDaCPUName());
    }

    @Inject(method = "getRenderer", at = @At("RETURN"), cancellable = true)
    private static void modifyGPUGL(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(DataHolder.getDaGPUName());
    }

    @Inject(method = "getVersion", at = @At("RETURN"), cancellable = true)
    private static void modifyDriverGL(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(DataHolder.getDaGPUDriver());
    }

    @Inject(method = "getVendor", at = @At("RETURN"), cancellable = true)
    private static void modifyVendorGL(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(DataHolder.getDaGPUVendor());
    }
}
