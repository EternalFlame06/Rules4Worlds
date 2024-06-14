package com.etfl.rules4worlds.settings;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.IntPredicate;
import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.util.Formatting.WHITE;

/**
 * Represents an integer setting in the config.
 */
public class IntConfigSetting implements ConfigSetting {
    private int value;
    private final int defaultValue;
    private final String name;
    private final ArgumentType<Integer> argumentType;
    private final IntPredicate validator;
    private Runnable markDirty = () -> {};
    private Supplier<Map<String, Object>> defaultSupplier = Map::of;

    /**
     * Creates a new integer setting with the given name, argument type, and default value.
     * @param name the name of the setting
     * @param argumentType the argument type for the command
     * @param defaultValue the default value of the setting
     */
    public IntConfigSetting(@NotNull @NotBlank final String name,
                            @NotNull final ArgumentType<Integer> argumentType,
                            final int defaultValue) {
        this(name, argumentType, defaultValue,
                argumentType instanceof IntegerArgumentType ?
                        value -> (value >= ((IntegerArgumentType) argumentType).getMinimum()
                                && value <= ((IntegerArgumentType) argumentType).getMaximum())
                        : value -> true);
    }

    /**
     * Creates a new integer setting with the given name, argument type, default value, and validator.
     * @param name the name of the setting
     * @param argumentType the argument type for the command
     * @param defaultValue the default value of the setting
     * @param validator the validator for the value
     */
    public IntConfigSetting(@NotNull @NotBlank final String name,
                            @NotNull final ArgumentType<Integer> argumentType,
                            final int defaultValue,
                            @NotNull final IntPredicate validator) {
        this.name = name;
        this.argumentType = argumentType;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.validator = validator;
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
        nbt.putInt(name, value);
    }

    @Override
    public void fromNbt(@NotNull NbtCompound nbt) {
        int value = nbt.getInt(name);

        this.value = validator.test(value) ? value : defaultValue;
    }

    /**
     * Gets the value of the setting.
     * @return the value of the setting
     */
    public int get() {
        return value;
    }

    @Override
    public boolean validateOrSetDefault(@NotNull Map<String, Object> map) {
        Object obj = map.get(name);
        boolean incorrect = !(obj instanceof Number) || !validator.test(((Number) obj).intValue());
        if (incorrect) map.put(name, defaultValue);
        return incorrect;
    }

    @Override
    public void fromMap(@NotNull Map<String, Object> map) {
        Object obj = map.get(name);
        boolean isNumber = obj instanceof Number;
        int value = isNumber ? ((Number) obj).intValue() : defaultValue;
        this.value = isNumber && validator.test(value) ? value : defaultValue;
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

        return value > 0 ? 15 : 0;
    }

    /**
     * Called when the command to set the value of the setting is executed.
     * Sets the value of the setting to the value provided in the command.
     * @param context the command context
     * @return a success value (15 is value is true and 0 if false)
     */
    private int set(CommandContext<ServerCommandSource> context) {
        markDirty.run();

        final int value = IntegerArgumentType.getInteger(context, name);

        boolean valueChanged = validator.test(value);

        if (valueChanged) {
            this.value = value;
        }

        context.getSource().sendFeedback(
                () -> Text.literal("Setting: " + name + " is currently set to: " + value).formatted(WHITE),
                valueChanged);

        return value > 0 ? 15 : 0;
    }

    /**
     * Called when the command to reset the value of the setting is executed.
     * Resets the value of the setting to its default value.
     * @param context the command context
     * @return a success value (15 is value is true and 0 if false)
     */
    private int reset(CommandContext<ServerCommandSource> context) {
        markDirty.run();

        fromMap(defaultSupplier.get());

        context.getSource().sendFeedback(
                () -> Text.literal("Setting: " + name + " is currently set to: " + value).formatted(WHITE),
                true);

        return 15;
    }
}