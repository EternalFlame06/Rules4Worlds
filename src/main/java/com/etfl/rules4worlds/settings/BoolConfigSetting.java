package com.etfl.rules4worlds.settings;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.util.Formatting.WHITE;

public class BoolConfigSetting implements ConfigSetting {
    private boolean value;
    private final boolean defaultValue;
    private final String name;
    private final ArgumentType<Boolean> argumentType;
    private Runnable markDirty = () -> {};
    private Supplier<Map<String, Object>> defaultSupplier = Map::of;

    public BoolConfigSetting(@NotNull @NotBlank final String name,
                             @NotNull final ArgumentType<Boolean> argumentType,
                             final boolean defaultValue) {
        this.name = name;
        this.argumentType = argumentType;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public void initialize(@NotNull LiteralArgumentBuilder<ServerCommandSource> command) {
        command.then(literal(name)
                .executes(this::get)

                .then(argument(name, argumentType)
                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                        .executes(this::set))

                .then(literal("default")
                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                        .executes(this::reset)
                )
        );
    }

    @Override
    public void toNbt(@NotNull NbtCompound nbt) {
        nbt.putBoolean(name, value);
    }

    @Override
    public void fromNbt(@NotNull NbtCompound nbt) {
        value = nbt.getBoolean(name);
    }

    @Override
    public boolean validateOrSetDefault(@NotNull Map<String, Object> map) {
        Object obj = map.get(name);
        boolean changed = !(obj instanceof Boolean);
        if (changed) map.put(name, defaultValue);
        return changed;
    }

    @Override
    public void fromMap(@NotNull Map<String, Object> map) {
        Object obj = map.getOrDefault(name, defaultValue);
        value = (obj instanceof Boolean) ? (Boolean) obj : defaultValue;
    }

    public Boolean get() {
        return value;
    }

    @Override
    public void setToDefault() {
        fromMap(defaultSupplier.get());
    }

    @Override
    public void setMarkDirtyRunnable(@NotNull Runnable runnable) {
        this.markDirty = runnable;
    }

    @Override
    public void setDefaultSupplier(@NotNull Supplier<Map<String, Object>> supplier) {
        defaultSupplier = supplier;
    }

    /**
     * Called when the command to get the value of the setting is executed.
     * @param context the command context
     * @return a success value (15 is value is true and 0 if false)
     */
    private int get(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(
                () -> Text.literal("Setting: " + name + " is currently set to: " + value).formatted(WHITE),
                false);

        return value ? 15 : 0;
    }

    /**
     * Called when the command to set the value of the setting is executed.
     * Sets the value of the setting to the value provided in the command.
     * @param context the command context
     * @return a success value (15 is value is true and 0 if false)
     */
    private int set(CommandContext<ServerCommandSource> context) {
        markDirty.run();

        final boolean value = BoolArgumentType.getBool(context, name);

        this.value = value;

        context.getSource().sendFeedback(
                () -> Text.literal("Setting: " + name + " is currently set to: " + value).formatted(WHITE),
                true);

        return value ? 15 : 0;
    }

    /**
     * Called when the command to reset the value of the setting is executed.
     * Resets the value of the setting to its default value.
     * @param context the command context
     * @return a success value (15 is value is true and 0 if false)
     */
    private int reset(CommandContext<ServerCommandSource> context) {
        markDirty.run();

        setToDefault();

        context.getSource().sendFeedback(
                () -> Text.literal("Setting: " + name + " is currently set to: " + value).formatted(WHITE),
                true);

        return 15;
    }
}