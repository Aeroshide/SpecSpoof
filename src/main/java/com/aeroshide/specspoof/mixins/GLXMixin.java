package com.aeroshide.specspoof.mixins;

import com.aeroshide.specspoof.SpecSpoofClient;
import com.aeroshide.specspoof.config.DataHolder;
import com.mojang.blaze3d.platform.GLX;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GLX.class)
public class GLXMixin {

    @Inject(method = "_getCpuInfo", at = @At("RETURN"), cancellable = true, remap = false)
    private static void modifyCPUGL(CallbackInfoReturnable<String> cir) {
        if (!SpecSpoofClient.isVulkanmodInstalled)
            cir.setReturnValue(DataHolder.getDaCPUName());
    }

}
