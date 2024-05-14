package com.etfl.rules4worlds.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EnumSettingType<E extends Enum<E>> {
    /**
     * Returns the string representation of the enum value that is used in the config.
     * @return the string representation of the enum value
     */
    @NotNull String toString();

    /**
     * Returns the enum value that corresponds to the given string representation or null if it does not fit any of the values.
     * Reverse of {@link #toString()}.
     * @param value the string representation of the enum value
     * @return the enum value that corresponds to the given string representation or null if it does not fit any of the values
     */
    @Nullable
    E fromString(@Nullable String value);
}