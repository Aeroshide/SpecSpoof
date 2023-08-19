package com.aeroshide.specspoof;

import com.aeroshide.specspoof.config.Config;
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


    public static boolean configIssues = false;

    // don't ask me why this shit's hardcoded lmao
    // spoiler alert: it prevents crashes from my shitty config backend
    public static String daCPUName = "32x 13th Gen Intel(R) Core(TM) i9-13900K";
    public static String daGPUName = "NVIDIA GeForce RTX 4090/PCIe/SSE2";
    public static int daFPS = 1000;
    public static Config config = Config.getInstance();
    public static int disableFPSThreshold = 100;

    @Override
    public void onInitializeClient() {
        fetchConfig();
    }

    public static void fetchConfig()
    {
        daCPUName = (String) config.getOption("CPU");
        daGPUName = (String) config.getOption("GPU");
        daFPS = (int) config.getOption("FPS");
        disableFPSThreshold = (int) config.getOption("disableFPSThreshold");
    }


}

