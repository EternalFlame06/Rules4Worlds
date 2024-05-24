package com.etfl.rules4worlds.fileManagement;

import com.etfl.rules4worlds.ConfigManager;

import java.util.Map;
import java.util.function.Function;

/**
 * The {@code ConfigFileManagerFactory} is an interface that allows you to pass a specific {@link ConfigFileManager} implementation to the {@link ConfigManager}.
 */
public interface ConfigFactory {

    /**
     * Creates a new {@link ConfigFileManager} instance with the provided configValidator. Which implementation of the {@link ConfigFileManager} is used depends on the implementation of the {@code ConfigFileManagerFactory}.
     * @param configValidator the function that validates the config
     * @return a new {@link ConfigFileManager} object
     */
    ConfigFileManager create(Function<Map<String, Object>, Boolean> configValidator);
}
