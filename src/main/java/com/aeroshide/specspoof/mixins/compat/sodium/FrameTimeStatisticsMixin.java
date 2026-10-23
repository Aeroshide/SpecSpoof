package com.aeroshide.specspoof.mixins.compat.sodium;

import com.aeroshide.specspoof.FakeFPSManager;
import net.caffeinemc.mods.sodium.client.util.FrameTimeStatistics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = FrameTimeStatistics.class, remap = false)
public class FrameTimeStatisticsMixin {

    @ModifyVariable(method = "logSample", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private long modifySampleTime(long realNs) {
        if (FakeFPSManager.cachedFps <= 60) {
            return realNs;
        }

        double fakeMs = FakeFPSManager.getMillisecondsPerFrame(realNs);
        return (long)(fakeMs * 1_000_000.0);
    }
}
