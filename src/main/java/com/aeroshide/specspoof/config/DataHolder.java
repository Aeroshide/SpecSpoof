package com.aeroshide.specspoof.config;

import com.aeroshide.specspoof.SpecSpoofClient;
import com.mojang.blaze3d.platform.GlStateManager;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.util.Locale;
import java.util.function.Supplier;

import static com.aeroshide.specspoof.SpecSpoofClient.config;

public class DataHolder {
    private static String daCPUName;
    private static String daGPUName;
    private static String daGPUVendor;
    private static String daGPUDriver;
    private static int daFPS;
    private static int disableFPSThreshold;

    private static final int GPU_RENDERER = 7937;
    private static final int GPU_VENDOR = 7936;
    private static final int GPU_VERSION = 7938;

    public static String getDaCPUName() {
        if (daCPUName == null) {
            fetchOptions();
        }
        return daCPUName;
    }

    public static String getDaGPUName() {
        if (daGPUName == null) {
            fetchOptions();
        }
        return daGPUName;
    }

    public static String getDaGPUVendor() {
        if (daGPUVendor == null) {
            fetchOptions();
        }
        return daGPUVendor;
    }

    public static String getDaGPUDriver() {
        if (daGPUDriver == null) {
            fetchOptions();
        }
        return daGPUDriver;
    }

    public static int getDaFPS() {
        if (daFPS == 0) {
            fetchOptions();
        }
        return daFPS;
    }

    public static int getDisableFPSThreshold() {
        if (disableFPSThreshold == 0) {
            fetchOptions();
        }
        return disableFPSThreshold;
    }

    public static void fetchOptions() {
        try {
            // Manually fetch CPU data
            CentralProcessor centralProcessor = (new SystemInfo()).getHardware().getProcessor();
            String cpuInfo = String.format(Locale.ROOT, "%dx %s", centralProcessor.getLogicalProcessorCount(), centralProcessor.getProcessorIdentifier().getName()).replaceAll("\\s+", " ");

            // Set options using the generic method
            setOptionIfAbsent("CPU", () -> cpuInfo);
            setOptionIfAbsent("GPU", () -> GlStateManager._getString(GPU_RENDERER));
            setOptionIfAbsent("GPUVendor", () -> GlStateManager._getString(GPU_VENDOR));
            setOptionIfAbsent("GPUDriverVersion", () -> GlStateManager._getString(GPU_VERSION));
            setOptionIfAbsent("FakeFPS", () -> 1000);
            setOptionIfAbsent("DisableFakeFPSThreshold", () -> 100);

            // Retrieve and store options
            daCPUName = (String) config.getOption("CPU");
            daGPUName = (String) config.getOption("GPU");
            daFPS = (Integer) config.getOption("FakeFPS");
            daGPUVendor = (String) config.getOption("GPUVendor");
            daGPUDriver = (String) config.getOption("GPUDriverVersion");
            disableFPSThreshold = (Integer) config.getOption("DisableFakeFPSThreshold");

            System.out.println("Options fetched successfully");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception or set default values
            daCPUName = "Unknown CPU";
            daGPUName = "Unknown GPU";
            daFPS = 0;
            daGPUVendor = "Unknown Vendor";
            daGPUDriver = "Unknown Driver";
            disableFPSThreshold = 0;
        }
    }

    private static <T> void setOptionIfAbsent(String optionKey, Supplier<T> valueSupplier) {
        if (config.getOption(optionKey) == null) {
            config.setOption(optionKey, valueSupplier.get());
        }
    }
}