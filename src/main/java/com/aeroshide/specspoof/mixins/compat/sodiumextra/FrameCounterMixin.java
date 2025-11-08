package com.aeroshide.specspoof.mixins.compat.sodiumextra;

import com.aeroshide.specspoof.FakeFPSManager;
import com.aeroshide.specspoof.compat.SodiumReflectionHelper;
import me.flashyreese.mods.sodiumextra.client.FrameCounter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Deque;

@Mixin(value = FrameCounter.class, remap = false)
public class FrameCounterMixin {
    private static final Logger LOGGER = LogManager.getLogger("SpecSpoof/SodiumCompat");
    private static final boolean DEBUG = false;
    private static boolean reflectionReady = false;

    @Inject(method = "onFrame", at = @At("HEAD"))
    private void replaceSamplesBeforeRecompute(CallbackInfo ci) {

        if (!reflectionReady) {
            reflectionReady = SodiumReflectionHelper.initialize();
            if (!reflectionReady) return;
            if (DEBUG) LOGGER.info("SpecSpoof: Reflection initialized");
        }

        if (FakeFPSManager.cachedFps <= 60) return;

        long now = System.nanoTime();


        long lastUpdateTime = SodiumReflectionHelper.getLastUpdateTime(this);
        long updateIntervalNanos = SodiumReflectionHelper.getUpdateIntervalNanos(this);

        boolean willRecompute = (now - lastUpdateTime) >= updateIntervalNanos;
        if (!willRecompute) return;

        if (!FakeFPSManager.shouldApplyFakeNow(now)) return;

        Deque<Object> samples = (Deque<Object>) SodiumReflectionHelper.getSamples(this);
        if (samples == null) return;

        try {

            int actualFps = calculateAverageFromExistingSamples(samples);
            int fakeFps = FakeFPSManager.computeFakeIntFps(actualFps);

            if (fakeFps == actualFps) return;


            synchronized (samples) {
                samples.clear();
                long windowNanos = SodiumReflectionHelper.getWindowNanos(this);
                long deltaNanos = Math.max(1L, 1_000_000_000L / fakeFps);
                int sampleCount = Math.min(100, (int) (windowNanos / deltaNanos) + 2);

                if (DEBUG) {
                    LOGGER.info("SpecSpoof: Actual={} FPS, Fake={} FPS, Gen={} samples",
                            actualFps, fakeFps, sampleCount);
                }

                for (int i = 0; i < sampleCount; i++) {
                    long timestamp = now - (sampleCount - 1 - i) * deltaNanos;
                    samples.addLast(SodiumReflectionHelper.createFrameSample(timestamp, deltaNanos));
                }
            }

            SodiumReflectionHelper.setLastFrameTime(this, now);

        } catch (Exception e) {
            LOGGER.error("SpecSpoof: Injection failed", e);
        }
    }

    @Unique
    private int calculateAverageFromExistingSamples(Deque<?> samples) {
        if (samples.isEmpty()) return 0;


        long totalDelta = 0;
        int count = 0;

        for (Object sample : samples) {
            try {
                totalDelta += SodiumReflectionHelper.getDeltaFromSample(sample);
                count++;
            } catch (Exception e) {

            }
        }

        return (count > 0 && totalDelta > 0) ?
                (int) Math.round(count * 1_000_000_000.0 / totalDelta) : 0;
    }
}