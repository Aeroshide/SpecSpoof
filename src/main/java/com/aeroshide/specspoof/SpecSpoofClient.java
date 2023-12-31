package com.aeroshide.specspoof;

import com.aeroshide.specspoof.config.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class SpecSpoofClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */

    public static final Logger LOG = LogManager.getLogger("SpecSpoof");


    public static boolean configIssues = true;

    // don't ask me why this shit's hardcoded lmao
    // spoiler alert: it prevents crashes from my shitty config backend
    public static String daCPUName = "32x 13th Gen Intel(R) Core(TM) i9-13900K";
    public static String daGPUName = "NVIDIA GeForce RTX 4090/PCIe/SSE2";
    public static int daFPS = 1000;
    public static String daGPUVendor = "NVIDIA Corporation";
    public static String daGPUDriver = "3.2.0 NVIDIA 531.37";
    public static Config config = Config.getInstance();
    public static int disableFPSThreshold = 100;

    @Override
    public void onInitializeClient() {
        fetchConfig();
    }

    public static void fetchConfig() {
        config.loadConfig();

        if (!(config.getOption("FPS") instanceof Integer) || !(config.getOption("disableFPSThreshold") instanceof Integer)){
            configIssues = false;
            LOG.error("Could not fetch config due to invalid data");
        }
        else if (!(config.getOption("GPUVendor") instanceof String) || !(config.getOption("GPUDriver") instanceof String) || !(config.getOption("GPU") instanceof String) || !(config.getOption("CPU") instanceof String) || config.getOption("FPS") == null || config.getOption("disableFPSThreshold") == null || (Integer) config.getOption("FPS") <= 0 || (Integer) config.getOption("FPS") > 999999 || (Integer) config.getOption("disableFPSThreshold") <= 0 || (Integer) config.getOption("disableFPSThreshold") > 999999 || !configIssues) {
            if ((config.getOption("GPUVendor") == null || config.getOption("GPUDriver") == null || config.getOption("GPU") == null || config.getOption("CPU") == null)) {
                LOG.warn("An error was found in config file, trying to fix.");
                config.initConfig(false, false);
            }
            else
            {
                configIssues = false;
                config.initConfig(false, true);
                LOG.error("Could not fetch config due to invalid data");
            }

        } else {
            configIssues = true;
            daCPUName = (String) config.getOption("CPU");
            daGPUName = (String) config.getOption("GPU");
            daFPS = (int) config.getOption("FPS");
            disableFPSThreshold = (int) config.getOption("disableFPSThreshold");
            daGPUVendor = (String) config.getOption("GPUVendor");
            daGPUDriver = (String) config.getOption("GPUDriver");
        }
    }



}

