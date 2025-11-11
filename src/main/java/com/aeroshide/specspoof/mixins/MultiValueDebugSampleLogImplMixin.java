package com.aeroshide.specspoof.mixins;

// package com.aeroshide.specspoof.mixins;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.aeroshide.specspoof.FakeFPSManager;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;

@Mixin(MultiValueDebugSampleLogImpl.class)
public abstract class MultiValueDebugSampleLogImplMixin {
    @Final
    @Shadow private long[][] multiValues;
    @Shadow private int start;
    @Shadow private int length;
    @Shadow protected abstract int wrap(int index);

    @Inject(method = "onPush", at = @At("TAIL"))
    private void afterOnPush(CallbackInfo ci) {
        try {
            // Only operate on the registered HUD instance
            if (!FakeFPSManager.isRegisteredFrameLog((MultiValueDebugSampleLogImpl) (Object) this)) {
                return;
            }
            if (this.length <= 0) return;

            int insertedIndex = this.wrap(this.start + this.length - 1);
            long originalNs = this.multiValues[insertedIndex][0];

            double spoofedMs = FakeFPSManager.getMillisecondsPerFrame((double) originalNs);
            long spoofedNs = Math.max(0L, (long) (spoofedMs * 1_000_000.0));

            this.multiValues[insertedIndex][0] = spoofedNs;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

