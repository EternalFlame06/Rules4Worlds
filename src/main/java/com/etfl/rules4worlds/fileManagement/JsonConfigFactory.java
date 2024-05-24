package com.etfl.rules4worlds.fileManagement;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

/**
 * A factory for creating a {@link JsonConfigFileManager} object.
 */
public class JsonConfigFactory implements ConfigFactory {
    /**
     * The name of the config file.
     */
    private final String configFileName;

    /**
     * Creates a new {@code JsonConfigFactory} with the provided {@code configFileName}.
     * @param configFileName the name of the config file
     */
    public JsonConfigFactory(@NotNull @NotBlank String configFileName) {
        this.configFileName = configFileName;
    }

    /**
     * Creates a new {@link JsonConfigFileManager} object with the provided {@code configValidator} and the {@code configFileName} provided in the constructor.
     * @param configValidator the function that validates the config
     * @return a new {@link JsonConfigFileManager} object
     */
    @Override
    public ConfigFileManager create(@NotNull Function<Map<String, Object>, Boolean> configValidator) {
        return new JsonConfigFileManager(configFileName, configValidator);
    }
}
