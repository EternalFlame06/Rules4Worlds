package com.etfl.rules4worlds;

import com.etfl.rules4worlds.fileManagement.ConfigFileManager;
import com.etfl.rules4worlds.fileManagement.JsonConfigFileManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The {@code ConfigManager} is the main class for the config library.
 * It is used to manage the {@link ConfigComponent} and register the commands.
 * Should only be instantiated and initialized during initialization of the mod using the constructor and the initialize() method.
 */
public class ConfigManager {
    final String modID;
    private final String baseCommand;
    private final List<ConfigComponent> components = new ArrayList<>();
    private final ConfigFileManager configFileManager;

    /**
     * Creates a new {@code ConfigManager} with the provided {@code modID}.
     * Uses the {@code modID} as {@code baseCommand} for the config.
     * The {@code modID} is used for the config file name and for the data file name of the {@link ServerStateManager}. Should only be instantiated during initialization of the mod.
     * @param modID the modID of the mod
     * @see ConfigManager#ConfigManager(String, String)
     */
    public ConfigManager(@NotNull @NotBlank final String modID) {
        this(modID, modID);
    }

    /**
     * Creates a new {@code ConfigManager} with the provided {@code modID} and {@code baseCommand}.
     * The {@code modID} is used for the config file name and for the data file name of the {@link ServerStateManager}. Should only be instantiated during initialization of the mod.
     * @param modID the modID of the mod
     * @param baseCommand the base command for the config
     */
    public ConfigManager(@NotNull @NotBlank final String modID, @NotNull @NotBlank final String baseCommand) {
        this.modID = modID;
        this.baseCommand = baseCommand;
        this.configFileManager = new JsonConfigFileManager(modID + "_config", this::validateOrSetDefault);

        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStart);
    }

    /**
     * Called when the server starts.
     * Sets the components to their default values and calls the ServerStateManager afterward to load saved data to the settings if present.
     * Adds the markDirty runnable to the components.
     * @param server the server that started
     */
    private void onServerStart(@NotNull MinecraftServer server) {
        resetSettings();

        var state = ServerStateManager.getServerState(server, this);

        setMarkDirtyRunnable(state::markDirty);
    }

    /**
     * Initializes the ConfigManager.
     */
    public void initialize() {
        registerCommands();

        setDefaultSupplier(configFileManager::getConfig);

        configFileManager.getConfig();
    }

    /**
     * Adds the markDirty runnable to the components.
     * @param runnable the runnable to add
     */
    private void setMarkDirtyRunnable(@NotNull Runnable runnable) {
        components.forEach(component -> component.setMarkDirtyRunnable(runnable));
    }

    /**
     * Sets the default supplier for the components.
     * @param supplier the supplier to set
     */
    private void setDefaultSupplier(@NotNull Supplier<Map<String, Object>> supplier) {
        components.forEach(component -> component.setDefaultSupplier(supplier));
    }

    /**
     * Resets all direct and indirect settings.
     */
    private void resetSettings() {
        components.forEach(components -> components.fromMap(configFileManager.getConfig()));
    }

    /**
     * Validates the values and the order of the map. If any values are missing or out of bounds, the default values are set.
     * If the order is incorrect, the method corrects it.
     * @param map the map to validate
     */
    private boolean validateOrSetDefault(@NotNull Map<String, Object> map) {
        boolean changed = false;

        for (ConfigComponent component : components) {
            changed |= component.validateOrSetDefault(map);
        }

        changed |= validateMapOrder(map);

        return changed;
    }

    /**
     * Validates the order of the map.
     * Only works if the map is a LinkedHashMap.
     * @param map the map to validate
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

    /**
     * Registers the commands of the components.
     */
    private void registerCommands() {
        var baseCommand = literal(this.baseCommand);
        components.forEach(component -> component.initialize(baseCommand));

        baseCommand.then(literal("reset")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .executes(context -> {
                    resetSettings();
                    return 15;
                }));

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment)
                        -> dispatcher.register(baseCommand));
    }

    /**
     * Adds a {@code ConfigComponent} to the {@code ConfigManager}.
     * @param component the ConfigComponent to add
     * @return the ConfigManager for chaining
     */
    public ConfigManager add(@NotNull final ConfigComponent component) {
        components.add(component);

        return this;
    }

    /**
     * Stores the values from the elements of components in the provided NbtCompound.
     * @param nbt the NbtCompound to store the values in
     */
    void toNbt(@NotNull NbtCompound nbt) {
        components.forEach(component -> component.toNbt(nbt));
    }

    /**
     * Reads the values from the provided nbtCompound and stores them in their associated components.
     * @param nbt the NbtCompound to read the values from
     */
    void fromNbt(@NotNull NbtCompound nbt) {
        components.forEach(component -> component.fromNbt(nbt));
    }
}