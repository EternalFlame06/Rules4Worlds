package com.etfl.rules4worlds.settings;

import com.etfl.rules4worlds.ConfigComponent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a setting in the config.
 */
public interface ConfigSetting extends ConfigComponent {

    /**
     * Get the name of the setting.
     * @return the name of the setting
     */
    @NotNull String getName();

    /**
     * Adds the setting's command to the given command tree.
     * @param command the command to add the setting's command to
     */
    void initialize(@NotNull LiteralArgumentBuilder<ServerCommandSource> command);
}