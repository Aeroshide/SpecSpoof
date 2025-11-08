package com.aeroshide.specspoof.compat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Deque;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SodiumReflectionHelper {
    private static final Logger LOGGER = LogManager.getLogger("SpecSpoof/SodiumCompat");

    private static Class<?> frameCounterClass;
    private static Field samplesField, windowNanosField, updateIntervalNanosField;
    private static Field lastUpdateTimeField, lastFrameTimeField, cachedAverageFpsField;

    private static Class<?> frameSampleClass;
    private static Constructor<?> sampleConstructor;
    private static Method deltaMethod;
    private static Field deltaField;
    private static boolean isRecord;
    private static boolean initialized = false;

    public static boolean initialize() {
        if (initialized) return true;

        try {
            frameCounterClass = Class.forName("me.flashyreese.mods.sodiumextra.client.FrameCounter");


            samplesField = getAndSetAccessible(frameCounterClass, "samples");
            windowNanosField = getAndSetAccessible(frameCounterClass, "windowNanos");
            updateIntervalNanosField = getAndSetAccessible(frameCounterClass, "updateIntervalNanos");
            lastUpdateTimeField = getAndSetAccessible(frameCounterClass, "lastUpdateTime");
            lastFrameTimeField = getAndSetAccessible(frameCounterClass, "lastFrameTime");
            cachedAverageFpsField = getAndSetAccessible(frameCounterClass, "cachedAverageFps");


            for (Class<?> inner : frameCounterClass.getDeclaredClasses()) {
                if (inner.getSimpleName().equals("FrameSample")) {
                    frameSampleClass = inner; break;
                }
            }
            if (frameSampleClass == null) {
                frameSampleClass = Class.forName("me.flashyreese.mods.sodiumextra.client.FrameSample");
            }

            isRecord = frameSampleClass.isRecord();
            sampleConstructor = frameSampleClass.getDeclaredConstructors()[0];
            sampleConstructor.setAccessible(true);

            if (isRecord) {
                deltaMethod = frameSampleClass.getDeclaredMethod("deltaNanos");
                deltaMethod.setAccessible(true);
            } else {

                deltaField = getAndSetAccessible(frameSampleClass, "deltaNanos");
            }

            initialized = true;
            LOGGER.info("SpecSpoof: Reflection cached (record={}, fields={})", isRecord, 7);
            return true;

        } catch (Exception e) {
            LOGGER.error("SpecSpoof: Reflection failed", e);
            return false;
        }
    }

    private static Field getAndSetAccessible(Class<?> clazz, String name) throws NoSuchFieldException {
        Field f = clazz.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }


    public static Deque<?> getSamples(Object instance) {
        try { return (Deque<?>) samplesField.get(instance); }
        catch (Exception e) { return null; }
    }

    public static long getWindowNanos(Object instance) {
        try { return windowNanosField.getLong(instance); }
        catch (Exception e) { return 5_000_000_000L; }
    }

    public static long getUpdateIntervalNanos(Object instance) {
        try { return updateIntervalNanosField.getLong(instance); }
        catch (Exception e) { return 500_000_000L; }
    }

    public static long getLastUpdateTime(Object instance) {
        try { return lastUpdateTimeField.getLong(instance); }
        catch (Exception e) { return 0L; }
    }

    public static void setLastFrameTime(Object instance, long value) {
        try { lastFrameTimeField.setLong(instance, value); }
        catch (Exception ignored) {}
    }

    public static Object createFrameSample(long timestamp, long deltaNanos) throws Exception {
        return sampleConstructor.newInstance(timestamp, deltaNanos);
    }

    public static long getDeltaFromSample(Object sample) throws Exception {
        if (isRecord) {
            return (long) deltaMethod.invoke(sample);
        } else {

            return deltaField.getLong(sample);
        }
    }
}