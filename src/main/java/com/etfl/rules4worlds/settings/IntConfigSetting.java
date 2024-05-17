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

public class IntConfigSetting implements ConfigSetting<Integer> {
    private int value;
    private final int defaultValue;
    private final String name;
    private final ArgumentType<Integer> argumentType;
    private final IntPredicate validator;
    private Runnable markDirty = () -> {};
    private Supplier<Map<String, Object>> defaultSupplier = Map::of;

    public IntConfigSetting(@NotNull @NotBlank final String name,
                            @NotNull final ArgumentType<Integer> argumentType,
                            final int defaultValue) {
        this(name, argumentType, defaultValue,
                argumentType instanceof IntegerArgumentType ?
                        value -> (value >= ((IntegerArgumentType) argumentType).getMinimum()
                                && value <= ((IntegerArgumentType) argumentType).getMaximum())
                        : value -> true);
    }

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

    @Override
    public Integer get() {
        return value;
    }

    @Override
    public void validateOrSetDefault(@NotNull Map<String, Object> map) {
        Object obj = map.getOrDefault(name, defaultValue);
        int value = (obj instanceof Number) ? ((Number) obj).intValue() : defaultValue;

        if (!validator.test(value)) value = defaultValue;

        map.put(name, validator.test(value) ? value : defaultValue);
    }

    @Override
    public void fromMap(@NotNull Map<String, Object> map) {
        Object obj = map.getOrDefault(name, defaultValue);
        int value = (obj instanceof Integer) ? (int) obj : defaultValue;

        if (validator.test(value)) this.value = value;
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

    private int get(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(
                () -> Text.literal("ConfigSetting" + name + " is currently set to: " + value).formatted(WHITE),
                false);

        return value > 0 ? 15 : 0;
    }

    private int set(CommandContext<ServerCommandSource> context) {
        markDirty.run();

        final int value = IntegerArgumentType.getInteger(context, name);

        boolean valueChanged = validator.test(value);

        if (valueChanged) {
            this.value = value;
        }

        context.getSource().sendFeedback(
                () -> Text.literal("ConfigSetting" + name + " is currently set to: " + value).formatted(WHITE),
                valueChanged);

        return value > 0 ? 15 : 0;
    }

    private int reset(CommandContext<ServerCommandSource> context) {
        markDirty.run();

        setToDefault();

        context.getSource().sendFeedback(
                () -> Text.literal("ConfigSetting" + name + " is currently set to: " + value).formatted(WHITE),
                true);

        return 15;
    }
}