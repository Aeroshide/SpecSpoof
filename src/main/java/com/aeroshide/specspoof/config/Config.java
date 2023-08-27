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
        SpecSpoofClient.LOG.info(value);
        if (value instanceof Double) {
            double doubleValue = (Double) value;
            if (doubleValue == Math.floor(doubleValue)) {
                return (int) doubleValue;
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
        return config.exists();
    }


    public void loadConfig() {
        if (!config.exists()) {
            initConfig(true);
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
            SpecSpoofClient.LOG.info("Config Loaded!");
            SpecSpoofClient.configIssues = true;
        } catch (IOException | JsonSyntaxException e) {
            SpecSpoofClient.LOG.error("Invalid data in configuration file: " + e.getMessage());
            SpecSpoofClient.configIssues = false;
        }
    }


    public void initConfig(boolean hardreset) {
        if (hardreset)
        {
            data = new HashMap<>();
            data.put("CPU", SpecSpoofClient.daCPUName);
            data.put("GPU", SpecSpoofClient.daGPUName);
            data.put("FPS", SpecSpoofClient.daFPS);
            data.put("disableFPSThreshold", SpecSpoofClient.disableFPSThreshold);
            data.put("GPUVendor", SpecSpoofClient.daGPUVendor);
            data.put("GPUDriver", SpecSpoofClient.daGPUDriver);
            SpecSpoofClient.configIssues = true;
            saveConfig();
            return;
        }

        boolean didFixSomething = false;

        if (data == null) {
            data = new HashMap<>();
        }

        Map<String, Object> defaultConfig = new HashMap<>();
        defaultConfig.put("CPU", SpecSpoofClient.daCPUName);
        defaultConfig.put("GPU", SpecSpoofClient.daGPUName);
        defaultConfig.put("FPS", SpecSpoofClient.daFPS);
        defaultConfig.put("disableFPSThreshold", SpecSpoofClient.disableFPSThreshold);
        defaultConfig.put("GPUVendor", SpecSpoofClient.daGPUVendor);
        defaultConfig.put("GPUDriver", SpecSpoofClient.daGPUDriver);

        for (Map.Entry<String, Object> entry : defaultConfig.entrySet()) {
            if (!data.containsKey(entry.getKey())) {
                data.put(entry.getKey(), entry.getValue());
                SpecSpoofClient.LOG.warn("Missing Key {}, Value: {}. adding them.", entry.getKey(), entry.getValue());
                didFixSomething = true;
            }
        }

        SpecSpoofClient.configIssues = didFixSomething;
        saveConfig();
    }




    private void saveConfig() {
        try (Writer writer = new FileWriter(config)) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                if (src == Math.floor(src)) {
                    return new JsonPrimitive(src.intValue());
                } else {
                    return new JsonPrimitive(src);
                }
            });
            Gson gson = builder.setPrettyPrinting().create();
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
