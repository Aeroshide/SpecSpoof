package com.aeroshide.specspoof;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;

public class SpecSpoofClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */

    public static final Logger LOG = LogManager.getLogger("SpecSpoof");
    private static final File config = FabricLoader.getInstance().getConfigDir().resolve("SpecSpoof.json").toFile();
    public static boolean configIssues = false;

    public static String daCPUName = "CPU: 32x 13th Gen Intel(R) Core(TM) i9-13900K";
    public static String daGPUName = "NVIDIA GeForce RTX 4090/PCIe/SSE2";
    public static int daFPS = 1000;
    public static int disableFPSThreshold = 100;

    @Override
    public void onInitializeClient() {
        // Create the default configuration file if it doesn't exist
        if (!config.exists()) {
            try (Writer writer = new FileWriter(config)) {
                ConfigData data = new ConfigData(daCPUName, daGPUName, daFPS, disableFPSThreshold);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(data, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Read data from the configuration file
        try (Reader reader = new FileReader(config)) {
            Gson gson = new Gson();
            ConfigData data = gson.fromJson(reader, ConfigData.class);
            if (!validateJson(data)) {
                configIssues = true;
                return;
            }

            daCPUName = data.getCPU();
            daGPUName = data.getGPU();
            daFPS = data.getFPS();
            disableFPSThreshold = data.getDisableFPSThreshold();
        } catch (IOException e) {
            LOG.error(e);
        } catch (JsonSyntaxException e) {
            LOG.error("Invalid data in configuration file: " + e.getMessage());
            configIssues = true;
        }

    }

    private boolean validateJson(ConfigData data) {
        // if this fires, you're either challenging me, or actually really stupid
        if (data == null)
        {
            // i win
            try (Writer writer = new FileWriter(config)) {
                ConfigData fixedData = new ConfigData(daCPUName, daGPUName, daFPS, disableFPSThreshold);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(fixedData, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        if (data.getCPU() == null || data.getCPU().isEmpty()) return false;
        else if (data.getGPU() == null || data.getGPU().isEmpty()) return false;
        else if (data.getFPS() <= 0 || data.getFPS() >= 999999) return false;
        else if (data.getDisableFPSThreshold() <= 0 || data.getDisableFPSThreshold() >= 999999) return false;

        return true;
    }


    public static List<IncompatibleMods> checkFor() {
        List<IncompatibleMods> result = new ArrayList<>();
        if (FabricLoader.getInstance().isModLoaded("areessgee")) {
            result.add(IncompatibleMods.AreEssGee);
        }
        return result;
    }

}

// list may get bigger, and I will make more workarounds
enum IncompatibleMods {
    AreEssGee
}

// comments in case i forgot in the future lmao
class ConfigData {
    private String CPU; // the CPU name
    private String GPU; // the GPU name
    private int FPS; // desired FPS to be faked as
    private int disableFPSThreshold; // The FPS Threshold for the fake fps to be deactivated, you see, to make it more believable, if your frames are dipping, the fps should actually reflect to that. imagine saying you have 1000 fps but your game runs like a slideshow, that just wouldn't work. (set to 0 if you dont want this feature)

    public ConfigData(String CPU, String GPU, int FPS, int disableFPSThreshold) {
        this.CPU = CPU;
        this.GPU = GPU;
        this.FPS = FPS;
        this.disableFPSThreshold = disableFPSThreshold;
    }

    public String getCPU() {
        return CPU;
    }

    public String getGPU() {
        return GPU;
    }

    public int getFPS() {
        return FPS;
    }

    public int getDisableFPSThreshold() {
        return disableFPSThreshold;
    }
}
