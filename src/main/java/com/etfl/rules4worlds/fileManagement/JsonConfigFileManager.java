package com.etfl.rules4worlds.fileManagement;

import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class JsonConfigFileManager implements ConfigFileManager {
    private final String configFileName;
    private final Function<Map<String, Object>, Boolean> configValidator;

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting().create();
    private static final TypeToken<Map<String, Map<String, Object>>> TYPE_TOKEN = new TypeToken<>() {};

    public JsonConfigFileManager(String configFileName, Function<Map<String, Object>, Boolean> configValidator) {
        this.configFileName = configFileName;
        this.configValidator = configValidator;
    }

    @Override
    public Map<String, Object> getConfig() {
        try {
            return _getConfig();
        } catch (IOException e) {
            return new LinkedHashMap<>();
        }
    }

    private Map<String, Object> _getConfig() throws IOException {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(configFileName + ".json");
        boolean fileExists = Files.exists(configPath);

        Map<String, Object> config = null;

        if (fileExists) {
            try (FileReader reader = new FileReader(configPath.toFile())) {
                config = GSON.fromJson(reader, TYPE_TOKEN.getType());
            }
        }

        if (config == null) config = new LinkedHashMap<>();

        boolean changed = configValidator.apply(config);

        if (!fileExists || changed) {
            try (FileWriter writer = new FileWriter(configPath.toFile())) {
                GSON.toJson(config, writer);
            }
        }
        return config;
    }
}
