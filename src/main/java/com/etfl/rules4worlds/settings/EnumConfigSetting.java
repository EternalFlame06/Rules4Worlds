package com.etfl.rules4worlds.settings;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.util.Formatting.WHITE;

public class EnumConfigSetting<T extends Enum<T> & EnumSettingType<T>> implements
        ConfigSetting{
    private T value;
    private final T defaultValue;
    private final String name;
    private Runnable markDirty = () -> {};
    private Supplier<Map<String, Object>> defaultSupplier = Map::of;

    public EnumConfigSetting(@NotNull @NotBlank final String name,
                             @NotNull final T defaultValue) {
        this.name = name;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public void initialize(@NotNull LiteralArgumentBuilder<ServerCommandSource> command) {
        var settingCommand = literal(name)
                .executes(this::get)
                .then(literal("default")
                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                        .executes(this::reset));

        for (T value : value.getDeclaringClass().getEnumConstants()) {
            settingCommand.then(literal(value.toString())
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                    .executes(context -> set(context, value)));
        }

        command.then(settingCommand);
    }

    @Override
    public void toNbt(@NotNull NbtCompound nbt) {
        nbt.putString(name, value.toString());
    }

    @Override
    public void fromNbt(@NotNull NbtCompound nbt) {
        T value = this.value.fromString(nbt.getString(name));

        this.value = value != null ? value : defaultValue;
    }

    public T get() {
        return value;
    }

    @Override
    public boolean validateOrSetDefault(@NotNull Map<String, Object> map) {
        Object obj = map.get(name);
        boolean incorrect = !(obj instanceof String) || this.value.fromString((String) obj) == null;
        if (incorrect) map.put(name, defaultValue.toString());
        return incorrect;
    }

    @Override
    public void fromMap(@NotNull Map<String, Object> map) {
        T value = this.value.fromString(map.getOrDefault(name, defaultValue).toString());

        if (value != null) this.value = value;
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
                () -> Text.literal("ConfigSetting" + name + " is currently set to: " + value.toString()).formatted(WHITE),
                false);

        return 15;
    }

    private int set(CommandContext<ServerCommandSource> context, T value) {
        markDirty.run();

        this.value = value;

        context.getSource().sendFeedback(
                () -> Text.literal("ConfigSetting" + name + " is currently set to: " + value.toString()).formatted(WHITE),
                true);

        return 15;
    }

    private int reset(CommandContext<ServerCommandSource> context) {
        markDirty.run();

        fromMap(defaultSupplier.get());

        context.getSource().sendFeedback(
                () -> Text.literal("ConfigSetting" + name + " is currently set to: " + value.toString()).formatted(WHITE),
                true);

        return 15;
    }
}