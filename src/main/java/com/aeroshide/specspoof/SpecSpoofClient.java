package com.aeroshide.specspoof;

import com.aeroshide.rose_bush.config.Config;
import com.aeroshide.specspoof.config.DataHolder;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.io.*;
import java.util.Locale;
import java.util.function.Supplier;

public class SpecSpoofClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */

    public static final Logger LOG = LogManager.getLogger("SpecSpoof");
    public static Config config = Config.getInstance("config/SpecSpoof.json");





    @Override
    public void onInitializeClient() {
    }





}

