package com.etfl.rules4worlds.categories;

import com.etfl.rules4worlds.ConfigComponent;
import com.etfl.rules4worlds.ConfigManager;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code ConfigCategory} class represents a category in a config with its components. It is used to group multiple {@link ConfigComponent ConfigComponents} and initialize them when needed. It also links the {@code ConfigComponents} to the {@link ConfigManager}, the base class of the config.
 * @see ConfigComponent
 * @see ConfigManager
 * @implSpec All methods implemented from the ConfigComponent interface should be implemented in a way that they call the same method on all components in the category.
 */
public interface ConfigCategory extends ConfigComponent {
    /**
     * Get the name of the category.
     * @return the name of the category
     */
    @NotNull String getName();

    /**
     * Adds a component to the category.
     * @param component the component to add
     * @return the category object for method chaining
     * @implSpec This method should add the component to an internal list of components and it should return the object itself for method chaining.
     */
    @NotNull ConfigCategory add(@NotNull ConfigComponent component);
}