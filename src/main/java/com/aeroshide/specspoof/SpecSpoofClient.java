package com.aeroshide.specspoof;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SpecSpoofClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */

    private static final File config = FabricLoader.getInstance().getConfigDir().resolve("SpecSpoof.txt").toFile();
    public static String daCPUName = "CPU: 32x 13th Gen Intel(R) Core(TM) i9-13900K";
    public static String daGPUName = "NVIDIA GeForce RTX 4090/PCIe/SSE2";

    public final List<IncompatibleMods> daMods = checkFor();


    @Override
    public void onInitializeClient() {
        // Create the default configuration file if it doesn't exist
        if (!config.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(config))) {
                writer.println("CPU=" + daCPUName);
                writer.println("GPU=" + daGPUName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Read data from the configuration file
        try (BufferedReader reader = new BufferedReader(new FileReader(config))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into key and value
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    // Set the value of the corresponding variable
                    if (key.equals("CPU")) {
                        daCPUName = value;
                    } else if (key.equals("GPU")) {
                        daGPUName = value;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<IncompatibleMods> checkFor() {
        List<IncompatibleMods> result = new ArrayList<>();
        if (FabricLoader.getInstance().isModLoaded("areessgee")) {
            result.add(IncompatibleMods.AreEssGee);
        }
        return result;
    }

}
// list may get bigger, and I will make more workarounds
enum IncompatibleMods
{
    AreEssGee

}
