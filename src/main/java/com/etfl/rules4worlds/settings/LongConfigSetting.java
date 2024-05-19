package com.etfl.rules4worlds.settings;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.LongPredicate;
import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.util.Formatting.WHITE;

public class LongConfigSetting implements ConfigSetting<Long> {
    private long value;
    private final long defaultValue;
    private final String name;
    private final ArgumentType<Long> argumentType;
    private final LongPredicate validator;
    private Runnable markDirty = () -> {};
    private Supplier<Map<String, Object>> defaultSupplier = Map::of;

    public LongConfigSetting(@NotNull @NotBlank final String name,
                             @NotNull final ArgumentType<Long> argumentType,
                             final long defaultValue) {
        this(name, argumentType, defaultValue, argumentType instanceof LongArgumentType ?
                value -> (value >= ((LongArgumentType) argumentType).getMinimum()
                        && value <= ((LongArgumentType) argumentType).getMaximum())
                : value -> true);
    }

    public LongConfigSetting(@NotNull @NotBlank final String name,
                             @NotNull final ArgumentType<Long> argumentType,
                             final long defaultValue,
                             @NotNull final LongPredicate validator) {
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
        nbt.putLong(name, value);
    }

    @Override
    public void fromNbt(@NotNull NbtCompound nbt) {
        long value = nbt.getLong(name);

        this.value = validator.test(value) ? value : defaultValue;
    }

    @Override
    public Long get() {
        return value;
    }

    @Override
    public boolean validateOrSetDefault(@NotNull Map<String, Object> map) {
        Object obj = map.get(name);
        boolean changed = !(obj instanceof Number) || !validator.test(((Number) obj).longValue());
        if (changed) map.put(name, defaultValue);
        return changed;
    }

    @Override
    public void fromMap(@NotNull Map<String, Object> map) {
        Object obj = map.get(name);
        boolean isNumber = obj instanceof Number;
        long value = isNumber ? ((Number) obj).longValue() : defaultValue;
        this.value = isNumber && validator.test(value) ? value : defaultValue;
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

        final long value = LongArgumentType.getLong(context, name);

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

        setToDefault();

        context.getSource().sendFeedback(
                () -> Text.literal("Setting: " + name + " is currently set to: " + value).formatted(WHITE),
                true);

        return 15;
    }
}