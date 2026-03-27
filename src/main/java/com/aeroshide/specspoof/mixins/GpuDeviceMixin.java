package com.aeroshide.specspoof.mixins;

import com.aeroshide.specspoof.SpecSpoofClient;
import com.aeroshide.specspoof.config.DataHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = com.mojang.blaze3d.systems.GpuDevice.class)
public class GpuDeviceMixin {

    @Inject(method = "getRenderer", at = @At("RETURN"), cancellable = true)
    private void modifyRenderer(CallbackInfoReturnable<String> cir) {
        if (!SpecSpoofClient.isVulkanmodInstalled) {
            cir.setReturnValue(DataHolder.getDaGPUName());
        }
    }

    @Inject(method = "getVersion", at = @At("RETURN"), cancellable = true)
    private void modifyDriver(CallbackInfoReturnable<String> cir) {
        if (!SpecSpoofClient.isVulkanmodInstalled) {
            cir.setReturnValue(DataHolder.getDaGPUDriver());
        }
    }

    @Inject(method = "getBackendName", at = @At("RETURN"), cancellable = true)
    private void modifyBackend(CallbackInfoReturnable<String> cir) {
        if (!SpecSpoofClient.isVulkanmodInstalled) {
            cir.setReturnValue(DataHolder.getDaGPUBackend());
        }
    }

    @Inject(method = "getVendor", at = @At("RETURN"), cancellable = true)
    private void modifyVendor(CallbackInfoReturnable<String> cir) {
        if (!SpecSpoofClient.isVulkanmodInstalled) {
            cir.setReturnValue(DataHolder.getDaGPUVendor());
        }
    }
}