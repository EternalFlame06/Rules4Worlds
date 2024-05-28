package com.etfl.rules4worlds;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public interface ConfigComponent {
    /**
     * Get the name of the component.
     * @return the name of the component
     */
    @NotNull String getName();

    /**
     * Initialize the component. The command is used to add the components command(s) to the command tree.
     * @param command the command to initialize the component with
     */
    void initialize(@NotNull LiteralArgumentBuilder<ServerCommandSource> command);

    /**
     * Adds the component's value to the given NbtCompound.
     */
    void toNbt(@NotNull NbtCompound nbt);

    /**
     * Sets the Component's value from the given NbtCompound.
     * @param nbt the NbtCompound to set the value from
     */
    void fromNbt(@NotNull NbtCompound nbt);

    /**
     * Adds the component's default value to the given map if not present or if the value is outside of bounds.
     * @param map the map to add the value to
     */
    boolean validateOrSetDefault(@NotNull Map<String, Object> map);

    /**
     * Sets the Component's value from the given map.
     * @param map the map to set the value from
     */
    void fromMap(@NotNull Map<String, Object> map);

    /**
     * Sets the markDirty runnable for the component.
     * Should only be called by the parent category or configManager.
     * @param runnable the runnable to set
     */
    void setMarkDirtyRunnable(@NotNull Runnable runnable);

    /**
     * Sets the default supplier for the component.
     * Should only be called by the parent category or configManager.
     * @param supplier the supplier to set
     */
    void setDefaultSupplier(@NotNull Supplier<Map<String, Object>> supplier);
}