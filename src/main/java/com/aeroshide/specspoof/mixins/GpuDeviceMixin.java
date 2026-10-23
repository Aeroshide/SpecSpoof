package com.aeroshide.specspoof.mixins;

import com.aeroshide.specspoof.config.DataHolder;
import com.mojang.renderpearl.api.device.DeviceInfo;
import com.mojang.renderpearl.frontend.FrontendGpuDevice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FrontendGpuDevice.class)
public class GpuDeviceMixin {

    @Inject(method = "getDeviceInfo", at = @At("RETURN"), cancellable = true)
    private void modifyDevice(CallbackInfoReturnable<DeviceInfo> cir) {
        DeviceInfo realDeviceInfo = cir.getReturnValue();
        DataHolder.setHardwareDeviceInfo(realDeviceInfo);

        DeviceInfo fakeDeviceInfo = new DeviceInfo(
            DataHolder.getDaGPUName(),
            DataHolder.getDaGPUVendor(),
            DataHolder.getDaGPUDriver(),
            realDeviceInfo.isZZeroToOne(),
            DataHolder.getDaGPUBackend(),
            realDeviceInfo.timestampPeriod(),
            realDeviceInfo.limits(),
            realDeviceInfo.features(),
            realDeviceInfo.underlyingExtensions(),
            realDeviceInfo.hintsAndWorkarounds(),
            realDeviceInfo.type()
        );

        cir.setReturnValue(fakeDeviceInfo);
    }
}