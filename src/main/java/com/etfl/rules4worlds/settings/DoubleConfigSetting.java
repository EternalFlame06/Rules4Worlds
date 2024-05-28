package com.etfl.rules4worlds.settings;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.DoublePredicate;
import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.util.Formatting.WHITE;

public class DoubleConfigSetting implements ConfigSetting {
    private double value;
    private final double defaultValue;
    private final String name;
    private final ArgumentType<Double> argumentType;
    private final DoublePredicate validator;
    private Runnable markDirty = () -> {};
    private Supplier<Map<String, Object>> defaultSupplier = Map::of;

    public DoubleConfigSetting(@NotNull @NotBlank final String name,
                               @NotNull final ArgumentType<Double> argumentType,
                               final double defaultValue) {
        this(name, argumentType, defaultValue, argumentType instanceof DoubleArgumentType ?
                value -> (value >= ((DoubleArgumentType) argumentType).getMinimum()
                        && value <= ((DoubleArgumentType) argumentType).getMaximum())
                : value -> true);
    }

    public DoubleConfigSetting(@NotNull @NotBlank final String name,
                               @NotNull final ArgumentType<Double> argumentType,
                               final double defaultValue,
                               @NotNull final DoublePredicate validator) {
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
        nbt.putDouble(name, value);
    }

    @Override
    public void fromNbt(@NotNull NbtCompound nbt) {
        double value = nbt.getDouble(name);

        this.value = validator.test(value) ? value : defaultValue;
    }

    public double get() {
        return value;
    }

    @Override
    public boolean validateOrSetDefault(@NotNull Map<String, Object> map) {
        Object obj = map.get(name);
        boolean incorrect = !(obj instanceof Number) || !validator.test(((Number) obj).doubleValue());
        if (incorrect) map.put(name, defaultValue);
        return incorrect;
    }

    @Override
    public void fromMap(@NotNull Map<String, Object> map) {
        Object obj = map.get(name);
        boolean isNumber = obj instanceof Number;
        double value = isNumber ? ((Number) obj).doubleValue() : defaultValue;
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

    private int get(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(
                () -> Text.literal("Setting: " + name + " is currently set to: " + value).formatted(WHITE),
                false);

        return value > 0 ? 15 : 0;
    }

    private int set(CommandContext<ServerCommandSource> context) {
        markDirty.run();

        final double value = DoubleArgumentType.getDouble(context, name);

        boolean valueChanged = validator.test(value);

        if (valueChanged) {
            this.value = value;
        }

        context.getSource().sendFeedback(
                () -> Text.literal("Setting: " + name + " is currently set to: " + value).formatted(WHITE),
                valueChanged);

        return value > 0 ? 15 : 0;
    }

    private int reset(CommandContext<ServerCommandSource> context) {
        markDirty.run();

        fromMap(defaultSupplier.get());

        context.getSource().sendFeedback(
                () -> Text.literal("Setting: " + name + " is currently set to: " + value).formatted(WHITE),
                true);

        return 15;
    }
}