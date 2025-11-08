package com.aeroshide.specspoof;

import com.aeroshide.specspoof.config.DataHolder;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public final class FakeFPSManager {
    private static final AtomicLong lastAppliedNano = new AtomicLong(0);
    private static final long HALF_SECOND_NANOS = 500_000_000L;

    // Cache config values to avoid hot-path lookups
    public static volatile int cachedFps = 60;
    private static volatile int cachedThreshold = 30;

    private FakeFPSManager() {}

    public static void updateCache() {
        cachedFps = DataHolder.getDaFPS();
        cachedThreshold = DataHolder.getDisableFPSThreshold();
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
}