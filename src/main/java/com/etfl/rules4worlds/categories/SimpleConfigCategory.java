package com.etfl.rules4worlds.categories;

import com.etfl.rules4worlds.ConfigComponent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * A simple implementation of the {@code ConfigCategory} interface.
 */
public class SimpleConfigCategory implements ConfigCategory {
    /**
     * The name of the category.
     */
    private final String name;

    /**
     * The command of the category in the command tree of the config.
     */
    private final String commandName;

    /**
     * The components of the category.
     */
    private final List<ConfigComponent> components = new ArrayList<>();

    /**
     * A supplier for the map provided by the config file for the default values of the settings contained within this category.
     */
    private Supplier<Map<String, Object>> reset = Map::of;

    /**
     * Creates a new {@code ConfigCategory} object with the provided name and uses it for the commandName.
     * @param name the name of the category
     * @see SimpleConfigCategory#SimpleConfigCategory(String, String)
     */
    public SimpleConfigCategory(@NotNull @NotBlank String name) {
        this(name, name);
    }

    /**
     * Creates a new {@code ConfigCategory} object with the provided name and commandName.
     * @param name the name of the category
     * @param commandName the command of the category in the command tree of the config
     */
    public SimpleConfigCategory(@NotNull @NotBlank String name, @NotNull @NotBlank String commandName) {
        this.name = name;
        this.commandName = commandName;
    }

    @Override
    public @NotNull SimpleConfigCategory add(@NotNull ConfigComponent component) {
        components.add(component);
        return this;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public void initialize(@NotNull LiteralArgumentBuilder<ServerCommandSource> command) {
        LiteralArgumentBuilder<ServerCommandSource> categoryCommand = literal(commandName);

        components.forEach(component -> component.initialize(categoryCommand));

        categoryCommand.then(literal("reset")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .executes(context -> {
            setToDefault();
            return 15;
        }));

        command.then(categoryCommand);
    }

    @Override
    public void toNbt(@NotNull NbtCompound nbt) {
        NbtCompound categoryNbt = new NbtCompound();

        components.forEach(component -> component.toNbt(categoryNbt));

        nbt.put(name, categoryNbt);
    }

    @Override
    public void fromNbt(@NotNull NbtCompound nbt) {
        NbtCompound categoryNbt = nbt.getCompound(name);

        components.forEach(component -> component.fromNbt(categoryNbt));
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean validateOrSetDefault(@NotNull Map<String, Object> map) {
        Object obj = map.get(name);
        boolean incorrect = !(obj instanceof Map);
        Map<String, Object> categoryMap = incorrect ? new LinkedHashMap<>() : (Map<String, Object>) obj;

        for (ConfigComponent component : components) {
            incorrect |= component.validateOrSetDefault(categoryMap);
        }

        incorrect |= validateMapOrder(categoryMap);

        if (incorrect) map.put(name, categoryMap);

        return incorrect;
    }

    /**
     * Validates the order of the map.
     * Only works if the map is a {@link LinkedHashMap}.
     * @param map the map to validate
     * @return {@code true} if the map was changed, {@code false} otherwise
     */
    private boolean validateMapOrder(@NotNull Map<String, Object> map)  {
        Map<String, Object> categoryMap = new LinkedHashMap<>(map);
        map.clear();

        components.forEach(component -> {
            String key = component.getName();
            map.put(key, categoryMap.get(key));
        });

        return !(new ArrayList<>(categoryMap.keySet()).equals(new ArrayList<>(map.keySet())));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fromMap(@NotNull Map<String, Object> map) {
        Map<String, Object> categoryMap = (Map<String, Object>) map.getOrDefault(name, Map.of());

        components.forEach(component -> component.fromMap(categoryMap));
    }

    /**
     * Set the value of the settings to the default value.
     */
    public void setToDefault() {
        fromMap(reset.get());
    }

    @Override
    public void setMarkDirtyRunnable(@NotNull Runnable runnable) {
        components.forEach(component -> component.setMarkDirtyRunnable(runnable));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setDefaultSupplier(@NotNull Supplier<Map<String, Object>> supplier) {

        reset = () -> (Map<String, Object>) supplier.get().getOrDefault(name, Map.of());

        components.forEach(component -> component.setDefaultSupplier(reset));
    }
}