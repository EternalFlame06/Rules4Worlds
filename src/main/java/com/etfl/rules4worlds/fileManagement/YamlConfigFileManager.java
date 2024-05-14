package com.etfl.rules4worlds.fileManagement;

import net.fabricmc.loader.api.FabricLoader;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class YamlConfigFileManager implements ConfigFileManager {
    private final String configFileName;
    private final Consumer<Map<String, Object>> configValidator;

    public YamlConfigFileManager(String configFileName, Consumer<Map<String, Object>> configValidator) {
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
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(configFileName + ".yaml");
        boolean fileExists = Files.exists(configPath);

        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()), new Representer(options), options);

        Map<String, Object> config = null;

        if (fileExists) {
            try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                config = yaml.load(reader);
            }
        }

        if (config == null) config = new LinkedHashMap<>();

        Map<String, Object> currentConfig = new LinkedHashMap<>(config);

        configValidator.accept(config);

        if (!fileExists
                || !config.equals(currentConfig)
                || mapsUnequal(config, currentConfig)) {
            try (PrintWriter writer = new PrintWriter(Files.newOutputStream(configPath))) {
                yaml.dump(config, writer);
            }
        }

        return config;
    }

    @SuppressWarnings("unchecked")
    private boolean mapsUnequal(Map<String, Object> map1, Map<String, Object> map2) {
        if (map1 == null || !map1.equals(map2)) return true;

        Iterator<Map.Entry<String, Object>> it1 = map1.entrySet().iterator();
        Iterator<Map.Entry<String, Object>> it2 = map2.entrySet().iterator();

        while (it1.hasNext() && it2.hasNext()) {
            Map.Entry<String, Object> entry1 = it1.next();
            Map.Entry<String, Object> entry2 = it2.next();

            if (!entry1.getKey().equals(entry2.getKey())) return true;

            if (entry1.getValue() instanceof Map && mapsUnequal((Map<String, Object>) entry1.getValue(), (Map<String, Object>) entry2.getValue())) {
                return true;
            }
        }

        return false;
    }
}
