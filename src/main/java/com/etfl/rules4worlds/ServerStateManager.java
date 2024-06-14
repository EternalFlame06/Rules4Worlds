package com.etfl.rules4worlds;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;


class ServerStateManager extends PersistentState {

    /**
     * The linked config manager for the server.
     */
    private final ConfigManager configManager;

    /**
     * Creates a new {@code ServerStateManager} object with the provided {@link ConfigManager}.
     * @param configManager the config manager to link the {@code ServerStateManager} to
     */
    private ServerStateManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        configManager.toNbt(nbt);

        return nbt;
    }

    /**
     * Creates a {@code ServerStateManager} from the given NBT data.
     * @param nbt the NBT data to create the {@code ServerStateManager} from
     * @param configManager the config manager to link the {@code ServerStateManager} to
     * @return a {@code ServerStateManager} created from the given NBT data
     */
    private static ServerStateManager fromNbt(NbtCompound nbt, ConfigManager configManager) {
        configManager.fromNbt(nbt);
        return new ServerStateManager(configManager);
    }

    /**
     * Used to get the {@code ServerStateManager} for the server.
     * @param server a server instance
     * @param configManager the config manager that calls this method
     * @return a {@code ServerStateManager}
     */
    static ServerStateManager getServerState(@NotNull MinecraftServer server, @NotNull ConfigManager configManager) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) throw new IllegalStateException("Overworld not found");

        PersistentStateManager persistentStateManager = world.getPersistentStateManager();

        var state = persistentStateManager.getOrCreate(
                new Type<>(
                        () -> new ServerStateManager(configManager),
                        nbt -> ServerStateManager.fromNbt(nbt, configManager),
                        null
                ), configManager.modID + "_rules");

        state.markDirty();

        return state;
    }
}