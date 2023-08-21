package com.aeroshide.specspoof.config;

import com.aeroshide.specspoof.SpecSpoofClient;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.aeroshide.specspoof.SpecSpoofClient.LOG;

public class Config {
    private static Config instance;
    private Map<String, Object> data;
    private static final File config = FabricLoader.getInstance().getConfigDir().resolve("SpecSpoof.json").toFile();

    private Config() {
        loadConfig();
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public Object getOption(String key) {
        if (data == null)
        {
            return null;
        }

        Object value = data.get(key);
        if (value instanceof Double) {
            Double doubleValue = (Double) value;
            if (doubleValue == Math.floor(doubleValue)) {
                return doubleValue.intValue();
            }
        }
        return value;
    }


    public void setOption(String key, Object value) {
        data.put(key, value);
        saveConfig();
    }

    public boolean doesExists()
    {
        if (!config.exists()) {
            return false;
        }
        return true;
    }

    private void loadConfig() {
        if (!config.exists()) {
            initConfig();
        }
        try (Reader reader = new FileReader(config)) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Double.class, (JsonDeserializer<Number>) (json, typeOfT, context) -> {
                if (json.isJsonPrimitive()) {
                    JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
                    if (jsonPrimitive.isNumber()) {
                        double value = json.getAsDouble();
                        if (value == Math.floor(value)) {
                            return json.getAsInt();
                        } else {
                            return value;
                        }
                    }
                }
                return null;
            });
            Gson gson = builder.create();
            Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
            data = gson.fromJson(reader, type);
            LOG.info("Config Loaded!");
        } catch (IOException | JsonSyntaxException e) {
            LOG.error("Invalid data in configuration file: " + e.getMessage());
        }
    }


    public void initConfig() {
        data = new HashMap<>();
        data.put("CPU", SpecSpoofClient.daCPUName);
        data.put("GPU", SpecSpoofClient.daGPUName);
        data.put("FPS", SpecSpoofClient.daFPS);
        data.put("disableFPSThreshold", SpecSpoofClient.disableFPSThreshold);
        saveConfig();
    }

    private void saveConfig() {
        try (Writer writer = new FileWriter(config)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
