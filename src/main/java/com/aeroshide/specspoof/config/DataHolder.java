package com.aeroshide.specspoof.config;

import com.aeroshide.specspoof.FakeFPSManager;
import com.aeroshide.specspoof.SpecSpoofClient;
import com.mojang.blaze3d.opengl.GlStateManager;
import net.fabricmc.loader.api.FabricLoader;
// import net.vulkanmod.vulkan.Vulkan;
// simport net.vulkanmod.vulkan.device.DeviceManager;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.util.Locale;
import java.util.function.Supplier;

import static com.aeroshide.specspoof.SpecSpoofClient.config;
//import static com.aeroshide.specspoof.SpecSpoofClient.isVulkanmodInstalled;

public class DataHolder {
    private static String daCPUName;
    private static String daGPUName;
    private static String daGPUVendor;
    private static String daGPUDriver;
    private static String daGPUBackend;
    private static int daFPS;
    private static int disableFPSThreshold;

    public static final int GPU_RENDERER = 7937;
    public static final int GPU_VENDOR = 7936;
    public static final int GPU_VERSION = 7938;

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

            // Ensure each key has a default value if missing.
            setOptionIfAbsent("CPU", DataHolder::fetchCPUData);
            setOptionIfAbsent("GPU", () -> GlStateManager._getString(GPU_RENDERER));
            setOptionIfAbsent("GPUVendor", () -> GlStateManager._getString(GPU_VENDOR));
            setOptionIfAbsent("GPUDriverVersion", () -> GlStateManager._getString(GPU_VERSION));
            setOptionIfAbsent("GPUBackend", () -> "OpenGL"); // default for GPUBackend
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



    public static String fetchCPUData()
    {
        try
        {
            CentralProcessor centralProcessor = (new SystemInfo()).getHardware().getProcessor();
            if (/*isVulkanmodInstalled/*/ false)
            {
                return String.format("%s", centralProcessor.getProcessorIdentifier().getName()).replaceAll("\\s+", " ");
            }
            return String.format(Locale.ROOT, "%dx %s", centralProcessor.getLogicalProcessorCount(), centralProcessor.getProcessorIdentifier().getName()).replaceAll("\\s+", " ");
        } catch (NoClassDefFoundError e)
        {
            SpecSpoofClient.LOG.error("Unable to fetch hardware information, init will be inaccurate!");
            return "1x unknown";
        }


    }
    private static <T> void setOptionIfAbsent(String optionKey, Supplier<T> valueSupplier) {
        if (config.getOption(optionKey) == null) {
            config.setOption(optionKey, valueSupplier.get());
        }
    }
}