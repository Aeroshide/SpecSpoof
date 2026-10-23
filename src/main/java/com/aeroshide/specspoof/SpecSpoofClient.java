package com.aeroshide.specspoof;

import com.aeroshide.rose_bush.api.FieldModifierService;
import com.aeroshide.rose_bush.config.Config;
import com.aeroshide.rose_bush.modifier.FieldModifier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

public class SpecSpoofClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */

    public static final Logger LOG = LogManager.getLogger("SpecSpoof");
    public static Config config;

    static {
        try {
            config = new Config(Path.of("config/SpecSpoof.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static FieldModifierService Roselib_FieldModifier = new FieldModifier();



    @Override
    public void onInitializeClient() {
    }





}

