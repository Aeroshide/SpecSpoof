package com.aeroshide.specspoof;

import com.aeroshide.specspoof.config.DataHolder;
import java.lang.ref.WeakReference;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.debugchart.LocalSampleLogger;

public final class FakeFPSManager {
    private static final AtomicLong lastAppliedNano = new AtomicLong(0);
    private static final long HALF_SECOND_NANOS = 500_000_000L;

    private static volatile WeakReference<LocalSampleLogger> registeredFrameLogRef = new WeakReference<>(null);

    // Cache config values to avoid hot-path lookups
    public static volatile int cachedFps = 60;
    private static volatile int cachedThreshold = 30;

    // State tracking for the 1-second HUD update interval
    private static long lastFpsUpdateTime = 0L;
    private static int currentDisplayedFps = 0;

    private FakeFPSManager() {}

    public static void updateCache() {
        cachedFps = DataHolder.getDaFPS();
        cachedThreshold = DataHolder.getDisableFPSThreshold();
    }

    /**
     * Returns the cached fake FPS, updating it only once per second.
     * Use this method inside your getFps() Mixin.
     */
    public static int getOrComputeFakeFps(int actualFps) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastFpsUpdateTime >= 1000L) {
            currentDisplayedFps = computeFakeIntFps(actualFps);
            lastFpsUpdateTime = currentTime;
        }

        return currentDisplayedFps;
    }

    public static int computeFakeIntFps(int actualFps) {
        if (cachedFps <= 60) return actualFps;
        if (actualFps < cachedThreshold) return actualFps;

        int jitter = (ThreadLocalRandom.current().nextInt() & 0x3F) - 32;
        return cachedFps + jitter;
    }

    public static double computeFakeDoubleFps(int actualFps) {
        return computeFakeIntFps(actualFps);
    }

    public static boolean shouldApplyFakeNow(long now) {
        return lastAppliedNano.getAndUpdate(last ->
                (now - last >= HALF_SECOND_NANOS) ? now : last
        ) != now;
    }

    public static double getMillisecondsPerFrame(double realNanosecondsPerFrame) {
        double realMs = (realNanosecondsPerFrame <= 0.0)
                ? (1000.0 / Math.max(1, cachedFps))
                : realNanosecondsPerFrame / 1_000_000.0;

        int actualFps = (int) Math.round(1000.0 / Math.max(0.000001, realMs));

        if (cachedFps <= 60 || actualFps < cachedThreshold) {
            return realMs;
        }

        int fakeFps = computeFakeIntFps(actualFps);
        if (fakeFps <= 0) {
            return realMs;
        }

        double targetMs = 1000.0 / (double) fakeFps;
        double jitterMs = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.5;
        return Math.max(0.0, targetMs + jitterMs);
    }

    public static void registerFrameLog(LocalSampleLogger instance) {
        registeredFrameLogRef = new WeakReference<>(instance);
    }

    public static void unregisterFrameLog() {
        registeredFrameLogRef = new WeakReference<>(null);
    }

    public static boolean isRegisteredFrameLog(LocalSampleLogger instance) {
        LocalSampleLogger inst = registeredFrameLogRef.get();
        return inst != null && inst == instance;
    }
}