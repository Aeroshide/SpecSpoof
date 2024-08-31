package com.aeroshide.specspoof.config;

import com.aeroshide.specspoof.SpecSpoofClient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.loader.api.FabricLoader;
import net.vulkanmod.vulkan.Vulkan;
import net.vulkanmod.vulkan.device.DeviceManager;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.util.Locale;
import java.util.function.Supplier;

import static com.aeroshide.specspoof.SpecSpoofClient.config;
import static com.aeroshide.specspoof.SpecSpoofClient.isVulkanmodInstalled;

public class DataHolder {
    private static String daCPUName;
    private static String daGPUName;
    private static String daGPUVendor;
    private static String daGPUDriver;
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
            // Ensure config is initialized
            if (config == null) {
                throw new IllegalStateException("Config is not initialized");
            }

            // Set options using the generic method
            setOptionIfAbsent("CPU", () -> fetchCPUData());
            setOptionIfAbsent("GPU", () -> (isVulkanmodInstalled ? DeviceManager.device.deviceName : GlStateManager._getString(GPU_RENDERER)));
            setOptionIfAbsent("GPUVendor", () -> (isVulkanmodInstalled ?DeviceManager.device.vendorIdString:GlStateManager._getString(GPU_VENDOR)));
            setOptionIfAbsent("GPUDriverVersion", () -> (isVulkanmodInstalled ? DeviceManager.device.driverVersion : GlStateManager._getString(GPU_VERSION)));
            setOptionIfAbsent("FakeFPS", () -> 1000);
            setOptionIfAbsent("DisableFakeFPSThreshold", () -> 100);

            // Retrieve and store options
            daCPUName = (String) config.getOption("CPU");
            daGPUName = (String) config.getOption("GPU");
            daFPS = config.getOption("FakeFPS") instanceof Number ? ((Number) config.getOption("FakeFPS")).intValue() : 0;
            daGPUVendor = (String) config.getOption("GPUVendor");
            daGPUDriver = (String) config.getOption("GPUDriverVersion");
            disableFPSThreshold = config.getOption("DisableFakeFPSThreshold") instanceof Number ? ((Number) config.getOption("DisableFakeFPSThreshold")).intValue() : 0;

            System.out.println("Options fetched successfully");
        } catch (Exception e) {
            System.err.println("Failed to fetch options: " + e.getMessage());
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


    public static String fetchCPUData()
    {
        try
        {
            CentralProcessor centralProcessor = (new SystemInfo()).getHardware().getProcessor();
            if (isVulkanmodInstalled)
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