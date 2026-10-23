package com.aeroshide.specspoof.config;

import com.aeroshide.specspoof.FakeFPSManager;
import com.aeroshide.specspoof.SpecSpoofClient;
import com.mojang.renderpearl.api.device.DeviceInfo;
import com.mojang.renderpearl.backend.opengl.GlStateManager;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.io.IOException;
import java.util.Locale;
import java.util.function.Supplier;

import static com.aeroshide.specspoof.SpecSpoofClient.config;

public class DataHolder {
    private static String daCPUName;
    private static String daGPUName;
    private static String daGPUVendor;
    private static String daGPUDriver;
    private static String daGPUBackend;
    private static int daFPS;
    private static int disableFPSThreshold;

    private static volatile DeviceInfo hardwareDeviceInfo;

    public static final int GPU_RENDERER = 7937;
    public static final int GPU_VENDOR = 7936;
    public static final int GPU_VERSION = 7938;

    public static void setHardwareDeviceInfo(DeviceInfo info) {
        if (hardwareDeviceInfo == null && info != null) {
            hardwareDeviceInfo = info;
        }
    }

    public static String getRealGPUName() {
        if (hardwareDeviceInfo != null) {
            return hardwareDeviceInfo.name();
        }
        try {
            return GlStateManager._getString(GPU_RENDERER);
        } catch (Throwable t) {
            return "Unknown GPU";
        }
    }

    public static String getRealGPUVendor() {
        if (hardwareDeviceInfo != null) {
            return hardwareDeviceInfo.vendorName();
        }
        try {
            return GlStateManager._getString(GPU_VENDOR);
        } catch (Throwable t) {
            return "Unknown Vendor";
        }
    }

    public static String getRealGPUDriver() {
        if (hardwareDeviceInfo != null) {
            return hardwareDeviceInfo.driverInfo();
        }
        try {
            return GlStateManager._getString(GPU_VERSION);
        } catch (Throwable t) {
            return "Unknown Driver";
        }
    }

    public static String getRealGPUBackend() {
        if (hardwareDeviceInfo != null) {
            return hardwareDeviceInfo.backendName();
        }
        return "OpenGL";
    }

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

    public static String getDaGPUBackend(){
        if (daGPUBackend == null) {
            fetchOptions();
        }
        return daGPUBackend;
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
            if (config == null) {
                throw new IllegalStateException("Config is not initialized");
            }

            setOptionIfAbsent("CPU", DataHolder::fetchCPUData);
            setOptionIfAbsent("GPU", DataHolder::getRealGPUName);
            setOptionIfAbsent("GPUVendor", DataHolder::getRealGPUVendor);
            setOptionIfAbsent("GPUDriverVersion", DataHolder::getRealGPUDriver);
            setOptionIfAbsent("GPUBackend", DataHolder::getRealGPUBackend);
            setOptionIfAbsent("FakeFPS", () -> 1000);
            setOptionIfAbsent("DisableFakeFPSThreshold", () -> 100);

            daCPUName = (String) config.getOption("CPU");
            daGPUName = (String) config.getOption("GPU");
            daFPS = config.getOption("FakeFPS") instanceof Number ? ((Number) config.getOption("FakeFPS")).intValue() : 0;
            daGPUVendor = (String) config.getOption("GPUVendor");
            daGPUDriver = (String) config.getOption("GPUDriverVersion");
            daGPUBackend = (String) config.getOption("GPUBackend");
            disableFPSThreshold = config.getOption("DisableFakeFPSThreshold") instanceof Number ? ((Number) config.getOption("DisableFakeFPSThreshold")).intValue() : 0;
            FakeFPSManager.updateCache();
        } catch (Exception e) {
            SpecSpoofClient.LOG.error("Failed to fetch options: " + e.getMessage());
            e.printStackTrace();
            daCPUName = "Unknown CPU";
            daGPUName = "Unknown GPU";
            daFPS = 0;
            daGPUVendor = "Unknown Vendor";
            daGPUDriver = "Unknown Driver";
            daGPUBackend = "Maybe OpenGL";
            disableFPSThreshold = 0;
        }
    }

    public static String fetchCPUData() {
        try {
            CentralProcessor centralProcessor = (new SystemInfo()).getHardware().getProcessor();
            return String.format(Locale.ROOT, "%dx %s", centralProcessor.getLogicalProcessorCount(), centralProcessor.getProcessorIdentifier().getName()).replaceAll("\\s+", " ");
        } catch (NoClassDefFoundError e) {
            SpecSpoofClient.LOG.error("Unable to fetch hardware information, init will be inaccurate!");
            return "1x unknown";
        }
    }

    private static <T> void setOptionIfAbsent(String optionKey, Supplier<T> valueSupplier) throws IOException {
        if (config.getOption(optionKey) == null) {
            config.setOption(optionKey, valueSupplier.get());
        }
    }
}