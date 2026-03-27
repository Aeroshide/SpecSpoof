package com.aeroshide.specspoof.mixins;

// package com.aeroshide.specspoof.mixins;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.aeroshide.specspoof.FakeFPSManager;
import net.minecraft.util.debugchart.LocalSampleLogger;

@Mixin(LocalSampleLogger.class)
public abstract class MultiValueDebugSampleLogImplMixin {
    @Final
    @Shadow private long[][] samples;
    @Shadow private int start;
    @Shadow private int size;
    @Shadow protected abstract int wrapIndex(int index);

    @Inject(method = "useSample", at = @At("TAIL"))
    private void afterOnPush(CallbackInfo ci) {
        try {
            // Only operate on the registered HUD instance
            if (!FakeFPSManager.isRegisteredFrameLog((LocalSampleLogger) (Object) this)) {
                return;
            }
            if (this.size <= 0) return;

            int insertedIndex = this.wrapIndex(this.start + this.size - 1);
            long originalNs = this.samples[insertedIndex][0];

            double spoofedMs = FakeFPSManager.getMillisecondsPerFrame((double) originalNs);
            long spoofedNs = Math.max(0L, (long) (spoofedMs * 1_000_000.0));

            this.samples[insertedIndex][0] = spoofedNs;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

