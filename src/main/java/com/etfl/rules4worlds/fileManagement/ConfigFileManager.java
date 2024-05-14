package com.etfl.rules4worlds.fileManagement;

import java.util.Map;

/**
 * The ConfigFileManager is an interface for the config file manager.
 * It is used to get the config from the file.
 */
public interface ConfigFileManager {
    /**
     * Gets the config from the file. Creates a new file with the default values if it does not exist and adds missing values.
     * @return the config file as a map
     */
    Map<String, Object> getConfig();
}
