package com.aeroshide.specspoof.mixins;

import com.aeroshide.specspoof.SpecSpoofClient;
import com.aeroshide.specspoof.config.DataHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = net.minecraft.client.gl.GlBackend.class)
public class GlBackend {


    @Inject(method = "getRenderer", at = @At("RETURN"), cancellable = true)
    private void modifyGPUGL(CallbackInfoReturnable<String> cir) {
        if (!SpecSpoofClient.isVulkanmodInstalled)
            cir.setReturnValue(DataHolder.getDaGPUName());
    }

    @Inject(method = "getVersion", at = @At("RETURN"), cancellable = true)
    private void modifyDriverGL(CallbackInfoReturnable<String> cir) {
        if (!SpecSpoofClient.isVulkanmodInstalled)
            cir.setReturnValue(DataHolder.getDaGPUDriver());
    }

    @Inject(method = "getBackendName", at = @At("RETURN"), cancellable = true)
    private void modifyBackendGL(CallbackInfoReturnable<String> cir) {
        if (!SpecSpoofClient.isVulkanmodInstalled)
            cir.setReturnValue(DataHolder.getDaGPUBackend());
    }

    @Inject(method = "getVendor", at = @At("RETURN"), cancellable = true)
    private void modifyVendorGL(CallbackInfoReturnable<String> cir) {
        if (!SpecSpoofClient.isVulkanmodInstalled)
            cir.setReturnValue(DataHolder.getDaGPUVendor());
    }
}
