
package com.aeroshide.specspoof.mixins.compat.vulkanmod;

import com.aeroshide.specspoof.SpecSpoofClient;
import com.aeroshide.specspoof.config.DataHolder;
import net.vulkanmod.vulkan.SystemInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Mixin(value = SystemInfo.class, remap = false)
public class SystemInfoMixin {

    // Shadow the static final field
    @Final
    @Mutable
    @Shadow
    public static String cpuInfo;

    // This will run after the static block is executed
    @Inject(method = "<clinit>", at = @At("TAIL"), require=0)
    private static void onStaticInit(CallbackInfo info) {
        cpuInfo = DataHolder.getDaCPUName();
    }
}