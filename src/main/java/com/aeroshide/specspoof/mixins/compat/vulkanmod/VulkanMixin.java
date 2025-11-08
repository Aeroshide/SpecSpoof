package com.aeroshide.specspoof.mixins.compat.vulkanmod;

import com.aeroshide.specspoof.SpecSpoofClient;
import com.aeroshide.rose_bush.modifier.FieldModifier;
import com.aeroshide.specspoof.config.DataHolder;
import net.vulkanmod.vulkan.Vulkan;
import net.vulkanmod.vulkan.device.Device;
import net.vulkanmod.vulkan.device.DeviceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Mixin(value = Vulkan.class, remap = false)
public class VulkanMixin {

    // dude you dont know how proud i am for this shit
    @Inject(method = "getDevice", at = @At("RETURN"), cancellable = true, remap=false, require=0)
    private static void modifyDeviceVulkan(CallbackInfoReturnable<Device> cir) {
        Device modifiedDeviceInfo = DeviceManager.device;

        try {
            SpecSpoofClient.Roselib_FieldModifier.modifyFinalField(modifiedDeviceInfo, "deviceName", DataHolder.getDaGPUName());

            // modifyFinalField(modifiedDeviceInfo, "vkVersion", DataHolder.getDaGPUVendor());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cir.setReturnValue(modifiedDeviceInfo);
    }


}
