package com.etfl.rules4worlds.fileManagement;

//import libs.snakeyaml.DumperOptions;

import java.io.IOException;
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
        /*Path configPath = FabricLoader.getInstance().getConfigDir().resolve(configFileName + ".yaml");
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

        return config;*/
        return Map.of();
    }
}
